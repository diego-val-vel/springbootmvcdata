package com.segurosargos.hotelbook.controller;

import com.segurosargos.hotelbook.dto.BookingStatusChangeResponseDto;
import com.segurosargos.hotelbook.service.BookingManagementService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controlador REST dedicado a exponer operaciones de negocio sobre reservas
 * que requieren consistencia transaccional entre Booking, Guest y Room.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class BookingManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingManagementController.class);

    private final BookingManagementService bookingManagementService;

    /*
     * Confirma una reserva de forma transaccional. Todos los cambios en la
     * reserva, el huésped y la habitación se aplican o se deshacen en bloque.
     */
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingStatusChangeResponseDto> confirmBooking(@PathVariable Long bookingId) {
        LOGGER.info("HTTP POST /api/bookings/{}/confirm recibido.", bookingId);
        BookingStatusChangeResponseDto response = bookingManagementService.confirmBooking(bookingId);
        return ResponseEntity.ok(response);
    }

    /*
     * Variante de confirmación que lanza una excepción intencional al final
     * del flujo para demostrar el rollback de la transacción.
     */
    @PostMapping("/{bookingId}/confirm-with-error")
    public ResponseEntity<BookingStatusChangeResponseDto> confirmBookingWithError(@PathVariable Long bookingId) {
        LOGGER.info("HTTP POST /api/bookings/{}/confirm-with-error recibido.", bookingId);
        BookingStatusChangeResponseDto response = bookingManagementService.confirmBookingWithSimulatedError(bookingId);
        return ResponseEntity.ok(response);
    }

    /*
     * Cancela una reserva de forma transaccional. Ajusta el estatus de la
     * reserva y el contador de reservas confirmadas del huésped.
     */
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingStatusChangeResponseDto> cancelBooking(@PathVariable Long bookingId) {
        LOGGER.info("HTTP POST /api/bookings/{}/cancel recibido.", bookingId);
        BookingStatusChangeResponseDto response = bookingManagementService.cancelBooking(bookingId);
        return ResponseEntity.ok(response);
    }
}
