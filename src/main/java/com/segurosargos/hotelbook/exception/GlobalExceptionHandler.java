package com.segurosargos.hotelbook.exception;

import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.segurosargos.hotelbook.dto.ErrorResponseDto;

/*
 * Manejo global de errores para la aplicación.
 * Centraliza la traducción de excepciones a respuestas HTTP.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Logger para registrar detalles de los errores.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
     * Manejo de errores de validación derivados de @Valid en los controladores.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ErrorResponseDto.FieldErrorEntry> fieldErrorEntries = new ArrayList<>();

        for (FieldError fieldError : fieldErrors) {
            ErrorResponseDto.FieldErrorEntry entry =
                    new ErrorResponseDto.FieldErrorEntry(fieldError.getField(), fieldError.getDefaultMessage());
            fieldErrorEntries.add(entry);
        }

        ErrorResponseDto body = ErrorResponseDto.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Error de validación en la petición.")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrorEntries)
                .build();

        LOGGER.warn("Error de validación en {}: {}", request.getRequestURI(), body);

        return ResponseEntity.status(status).body(body);
    }

    /*
     * Manejo de errores de negocio relacionados con datos inválidos de reserva.
     */
    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidBooking(
            InvalidBookingException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponseDto body = ErrorResponseDto.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        LOGGER.warn("Error de negocio en {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(status).body(body);
    }

    /*
     * Manejo de errores de negocio cuando no se encuentra una reserva o pre-reserva.
     */
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleBookingNotFound(
            BookingNotFoundException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorResponseDto body = ErrorResponseDto.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        LOGGER.warn("Recurso no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(status).body(body);
    }

    /*
     * Manejador genérico para cualquier otra excepción no contemplada.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponseDto body = ErrorResponseDto.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Ocurrió un error inesperado. Consulte al administrador.")
                .path(request.getRequestURI())
                .build();

        LOGGER.error("Error inesperado en {}.", request.getRequestURI(), ex);

        return ResponseEntity.status(status).body(body);
    }
}
