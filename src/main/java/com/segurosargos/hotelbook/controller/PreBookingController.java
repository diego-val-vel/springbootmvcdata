package com.segurosargos.hotelbook.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.segurosargos.hotelbook.dto.PreBookingRequestDto;
import com.segurosargos.hotelbook.dto.PreBookingResponseDto;
import com.segurosargos.hotelbook.exception.BookingNotFoundException;
import com.segurosargos.hotelbook.model.PreBooking;
import com.segurosargos.hotelbook.repository.PreBookingRepository;
import com.segurosargos.hotelbook.service.PreBookingService;

/*
 * Controlador REST para gestionar pre-reservas.
 * Expone endpoints sencillos para esta práctica.
 */
@RestController
@RequestMapping("/api/pre-bookings")
public class PreBookingController {

    /*
     * Logger para registrar las solicitudes que llegan al controlador.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PreBookingController.class);

    private final PreBookingService preBookingService;
    private final PreBookingRepository preBookingRepository;

    public PreBookingController(PreBookingService preBookingService,
                                PreBookingRepository preBookingRepository) {
        this.preBookingService = preBookingService;
        this.preBookingRepository = preBookingRepository;
    }

    /*
     * Crea una nueva pre-reserva en memoria a partir de los datos recibidos.
     */
    @PostMapping
    public ResponseEntity<PreBookingResponseDto> createPreBooking(
            @Valid @RequestBody PreBookingRequestDto request) {

        LOGGER.info("Solicitud HTTP para crear pre-reserva recibida.");

        PreBookingResponseDto response = preBookingService.createPreBooking(request);

        LOGGER.info("Pre-reserva creada con id {}.", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*
     * Obtiene una pre-reserva por id.
     * Este endpoint sirve para ilustrar el uso de BookingNotFoundException.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PreBookingResponseDto> getPreBookingById(@PathVariable("id") Long id) {

        LOGGER.info("Solicitud HTTP para consultar pre-reserva con id {}.", id);

        PreBooking preBooking = preBookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("No existe una pre-reserva con id " + id + "."));

        PreBookingResponseDto response = PreBookingResponseDto.builder()
                .id(preBooking.getId())
                .guestName(preBooking.getGuestName())
                .roomType(preBooking.getRoomType())
                .numberOfNights(preBooking.getNumberOfNights())
                .nightlyRate(preBooking.getNightlyRate())
                .totalAmount(preBooking.getTotalAmount())
                .createdAt(preBooking.getCreatedAt())
                .build();

        LOGGER.info("Pre-reserva con id {} encontrada.", id);

        return ResponseEntity.ok(response);
    }
}
