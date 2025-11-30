package com.segurosargos.hotelbook.service;

import com.segurosargos.hotelbook.dto.BookingStatusChangeResponseDto;
import com.segurosargos.hotelbook.exception.BookingNotFoundException;
import com.segurosargos.hotelbook.exception.InvalidBookingException;
import com.segurosargos.hotelbook.model.BookingEntity;
import com.segurosargos.hotelbook.model.GuestEntity;
import com.segurosargos.hotelbook.model.RoomEntity;
import com.segurosargos.hotelbook.repository.BookingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Servicio de gestión de reservas enfocado en flujos transaccionales que
 * modifican de forma consistente la información de Booking, Guest y Room.
 */
@Service
@RequiredArgsConstructor
public class BookingManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingManagementService.class);

    private final BookingJpaRepository bookingJpaRepository;

    /*
     * Confirma una reserva identificada por su id. La operación actualiza:
     * - El estatus de la reserva a CONFIRMED.
     * - El contador de reservas confirmadas del huésped asociado.
     * - La fecha de última reserva confirmada en la habitación asociada.
     *
     * Todos los cambios se aplican dentro de una única transacción.
     */
    @Transactional
    public BookingStatusChangeResponseDto confirmBooking(Long bookingId) {
        return doConfirmBooking(bookingId, false);
    }

    /*
     * Variante de confirmación que, tras aplicar los cambios en memoria y
     * solicitar el guardado, lanza una excepción de forma intencional.
     * Esto permite observar el rollback de la transacción y verificar que
     * ningún cambio queda persistido en la base de datos.
     */
    @Transactional
    public BookingStatusChangeResponseDto confirmBookingWithSimulatedError(Long bookingId) {
        return doConfirmBooking(bookingId, true);
    }

    /*
     * Cancela una reserva identificada por su id. La operación actualiza:
     * - El estatus de la reserva a CANCELLED.
     * - El contador de reservas confirmadas del huésped, decrementando en uno
     *   si la reserva estaba previamente confirmada.
     *
     * Los cambios se aplican dentro de una única transacción.
     */
    @Transactional
    public BookingStatusChangeResponseDto cancelBooking(Long bookingId) {
        LOGGER.info("Iniciando cancelación transaccional de la reserva con id {}.", bookingId);
        BookingEntity booking = bookingJpaRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("No se encontró la reserva con id " + bookingId));

        String previousStatus = booking.getStatus();
        if ("CANCELLED".equalsIgnoreCase(previousStatus)) {
            throw new InvalidBookingException("La reserva con id " + bookingId + " ya está cancelada.");
        }

        booking.setStatus("CANCELLED");

        GuestEntity guest = booking.getGuest();
        if (guest != null && "CONFIRMED".equalsIgnoreCase(previousStatus)) {
            Integer currentCount = guest.getConfirmedBookingsCount();
            if (currentCount == null) {
                currentCount = 0;
            }
            if (currentCount > 0) {
                guest.setConfirmedBookingsCount(currentCount - 1);
            }
        }

        bookingJpaRepository.save(booking);

        GuestEntity effectiveGuest = booking.getGuest();
        RoomEntity room = booking.getRoom();

        Long guestId = effectiveGuest != null ? effectiveGuest.getId() : null;
        String guestEmail = effectiveGuest != null ? effectiveGuest.getEmail() : null;
        Integer confirmedCount = effectiveGuest != null ? effectiveGuest.getConfirmedBookingsCount() : null;

        Long roomId = room != null ? room.getId() : null;
        String roomCode = room != null ? room.getCode() : null;

        BookingStatusChangeResponseDto response = BookingStatusChangeResponseDto.builder()
                .bookingId(booking.getId())
                .previousStatus(previousStatus)
                .newStatus(booking.getStatus())
                .guestId(guestId)
                .guestEmail(guestEmail)
                .guestConfirmedBookingsCount(confirmedCount)
                .roomId(roomId)
                .roomCode(roomCode)
                .roomLastBookingDate(room != null ? room.getLastBookingDate() : null)
                .build();

        LOGGER.info("Cancelación de la reserva con id {} completada. Estatus previo={}, nuevo estatus={}.",
                bookingId, previousStatus, booking.getStatus());

        return response;
    }

    /*
     * Implementación interna de la confirmación de reserva. Si simulateError
     * es true, se lanza una excepción después de intentar persistir los
     * cambios para forzar el rollback de la transacción.
     */
    private BookingStatusChangeResponseDto doConfirmBooking(Long bookingId, boolean simulateError) {
        LOGGER.info("Iniciando confirmación transaccional de la reserva con id {}. simulateError={}.",
                bookingId, simulateError);

        BookingEntity booking = bookingJpaRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("No se encontró la reserva con id " + bookingId));

        String previousStatus = booking.getStatus();
        if ("CONFIRMED".equalsIgnoreCase(previousStatus)) {
            throw new InvalidBookingException("La reserva con id " + bookingId + " ya está confirmada.");
        }

        booking.setStatus("CONFIRMED");

        GuestEntity guest = booking.getGuest();
        if (guest != null) {
            Integer currentCount = guest.getConfirmedBookingsCount();
            if (currentCount == null) {
                currentCount = 0;
            }
            guest.setConfirmedBookingsCount(currentCount + 1);
        }

        RoomEntity room = booking.getRoom();
        if (room != null) {
            room.setLastBookingDate(booking.getCheckInDate());
        }

        bookingJpaRepository.save(booking);

        if (simulateError) {
            LOGGER.error("Se lanzará una excepción intencional para demostrar el rollback de la transacción.");
            throw new IllegalStateException("Falla intencional después de confirmar la reserva con id " + bookingId);
        }

        GuestEntity effectiveGuest = booking.getGuest();
        RoomEntity effectiveRoom = booking.getRoom();

        Long guestId = effectiveGuest != null ? effectiveGuest.getId() : null;
        String guestEmail = effectiveGuest != null ? effectiveGuest.getEmail() : null;
        Integer confirmedCount = effectiveGuest != null ? effectiveGuest.getConfirmedBookingsCount() : null;

        Long roomId = effectiveRoom != null ? effectiveRoom.getId() : null;
        String roomCode = effectiveRoom != null ? effectiveRoom.getCode() : null;

        BookingStatusChangeResponseDto response = BookingStatusChangeResponseDto.builder()
                .bookingId(booking.getId())
                .previousStatus(previousStatus)
                .newStatus(booking.getStatus())
                .guestId(guestId)
                .guestEmail(guestEmail)
                .guestConfirmedBookingsCount(confirmedCount)
                .roomId(roomId)
                .roomCode(roomCode)
                .roomLastBookingDate(effectiveRoom != null ? effectiveRoom.getLastBookingDate() : null)
                .build();

        LOGGER.info("Confirmación de la reserva con id {} completada. Estatus previo={}, nuevo estatus={}.",
                bookingId, previousStatus, booking.getStatus());

        return response;
    }
}
