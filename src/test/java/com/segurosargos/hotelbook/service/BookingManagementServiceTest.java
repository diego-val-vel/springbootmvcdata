package com.segurosargos.hotelbook.service;

import com.segurosargos.hotelbook.dto.BookingStatusChangeResponseDto;
import com.segurosargos.hotelbook.exception.BookingNotFoundException;
import com.segurosargos.hotelbook.exception.InvalidBookingException;
import com.segurosargos.hotelbook.model.BookingEntity;
import com.segurosargos.hotelbook.model.GuestEntity;
import com.segurosargos.hotelbook.model.RoomEntity;
import com.segurosargos.hotelbook.repository.BookingJpaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 * Pruebas unitarias para BookingManagementService.
 * Se simulan reservas, huespedes y habitaciones en memoria utilizando un mock de BookingJpaRepository.
 */
@ExtendWith(MockitoExtension.class)
class BookingManagementServiceTest {

    @Mock
    private BookingJpaRepository bookingJpaRepository;

    @InjectMocks
    private BookingManagementService bookingManagementService;

    @Test
    @DisplayName("confirmBooking lanza BookingNotFoundException cuando la reserva no existe")
    void confirmBooking_whenBookingDoesNotExist_throwsBookingNotFoundException() {
        when(bookingJpaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingManagementService.confirmBooking(1L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    @DisplayName("confirmBooking lanza InvalidBookingException cuando la reserva ya esta confirmada")
    void confirmBooking_whenBookingAlreadyConfirmed_throwsInvalidBookingException() {
        BookingEntity booking = BookingEntity.builder()
                .id(2L)
                .checkInDate(LocalDate.of(2025, 3, 1))
                .checkOutDate(LocalDate.of(2025, 3, 3))
                .totalPrice(new BigDecimal("300.00"))
                .status("CONFIRMED")
                .build();

        when(bookingJpaRepository.findById(2L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingManagementService.confirmBooking(2L))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    @DisplayName("confirmBooking actualiza el estatus, el contador del huesped y la fecha de la habitacion")
    void confirmBooking_whenBookingIsValid_updatesStatusGuestAndRoom() {
        GuestEntity guest = GuestEntity.builder()
                .id(3L)
                .email("guest@example.com")
                .confirmedBookingsCount(0)
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(7L)
                .code("R-101")
                .lastBookingDate(null)
                .build();

        BookingEntity booking = BookingEntity.builder()
                .id(5L)
                .checkInDate(LocalDate.of(2025, 3, 1))
                .checkOutDate(LocalDate.of(2025, 3, 5))
                .totalPrice(new BigDecimal("500.00"))
                .status("CREATED")
                .guest(guest)
                .room(room)
                .build();

        when(bookingJpaRepository.findById(5L)).thenReturn(Optional.of(booking));
        when(bookingJpaRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingStatusChangeResponseDto response = bookingManagementService.confirmBooking(5L);

        assertThat(booking.getStatus()).isEqualTo("CONFIRMED");
        assertThat(guest.getConfirmedBookingsCount()).isEqualTo(1);
        assertThat(room.getLastBookingDate()).isEqualTo(booking.getCheckInDate());

        assertThat(response.getBookingId()).isEqualTo(5L);
        assertThat(response.getPreviousStatus()).isEqualTo("CREATED");
        assertThat(response.getNewStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getGuestId()).isEqualTo(3L);
        assertThat(response.getGuestEmail()).isEqualTo("guest@example.com");
        assertThat(response.getGuestConfirmedBookingsCount()).isEqualTo(1);
        assertThat(response.getRoomId()).isEqualTo(7L);
        assertThat(response.getRoomCode()).isEqualTo("R-101");
        assertThat(response.getRoomLastBookingDate()).isEqualTo(booking.getCheckInDate());

        verify(bookingJpaRepository).save(booking);
    }

    @Test
    @DisplayName("confirmBookingWithSimulatedError guarda y luego lanza IllegalStateException")
    void confirmBookingWithSimulatedError_whenBookingIsValid_savesAndThrowsIllegalStateException() {
        GuestEntity guest = GuestEntity.builder()
                .id(4L)
                .email("demo@example.com")
                .confirmedBookingsCount(1)
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(8L)
                .code("R-202")
                .lastBookingDate(null)
                .build();

        BookingEntity booking = BookingEntity.builder()
                .id(6L)
                .checkInDate(LocalDate.of(2025, 4, 10))
                .checkOutDate(LocalDate.of(2025, 4, 12))
                .totalPrice(new BigDecimal("250.00"))
                .status("CREATED")
                .guest(guest)
                .room(room)
                .build();

        when(bookingJpaRepository.findById(6L)).thenReturn(Optional.of(booking));
        when(bookingJpaRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> bookingManagementService.confirmBookingWithSimulatedError(6L))
                .isInstanceOf(IllegalStateException.class);

        assertThat(booking.getStatus()).isEqualTo("CONFIRMED");
        assertThat(guest.getConfirmedBookingsCount()).isEqualTo(2);
        assertThat(room.getLastBookingDate()).isEqualTo(booking.getCheckInDate());

        verify(bookingJpaRepository).save(booking);
    }

    @Test
    @DisplayName("cancelBooking lanza BookingNotFoundException cuando la reserva no existe")
    void cancelBooking_whenBookingDoesNotExist_throwsBookingNotFoundException() {
        when(bookingJpaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingManagementService.cancelBooking(10L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    @DisplayName("cancelBooking lanza InvalidBookingException cuando la reserva ya esta cancelada")
    void cancelBooking_whenBookingAlreadyCancelled_throwsInvalidBookingException() {
        BookingEntity booking = BookingEntity.builder()
                .id(11L)
                .checkInDate(LocalDate.of(2025, 5, 1))
                .checkOutDate(LocalDate.of(2025, 5, 3))
                .totalPrice(new BigDecimal("180.00"))
                .status("CANCELLED")
                .build();

        when(bookingJpaRepository.findById(11L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingManagementService.cancelBooking(11L))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    @DisplayName("cancelBooking desde CONFIRMED decrementa el contador del huesped y marca CANCELLED")
    void cancelBooking_whenBookingWasConfirmed_decrementsGuestCountAndCancels() {
        GuestEntity guest = GuestEntity.builder()
                .id(12L)
                .email("guest2@example.com")
                .confirmedBookingsCount(2)
                .build();

        RoomEntity room = RoomEntity.builder()
                .id(20L)
                .code("R-303")
                .lastBookingDate(LocalDate.of(2025, 6, 1))
                .build();

        BookingEntity booking = BookingEntity.builder()
                .id(15L)
                .checkInDate(LocalDate.of(2025, 6, 10))
                .checkOutDate(LocalDate.of(2025, 6, 12))
                .totalPrice(new BigDecimal("220.00"))
                .status("CONFIRMED")
                .guest(guest)
                .room(room)
                .build();

        when(bookingJpaRepository.findById(15L)).thenReturn(Optional.of(booking));
        when(bookingJpaRepository.save(any(BookingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingStatusChangeResponseDto response = bookingManagementService.cancelBooking(15L);

        assertThat(booking.getStatus()).isEqualTo("CANCELLED");
        assertThat(guest.getConfirmedBookingsCount()).isEqualTo(1);

        assertThat(response.getBookingId()).isEqualTo(15L);
        assertThat(response.getPreviousStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getNewStatus()).isEqualTo("CANCELLED");
        assertThat(response.getGuestId()).isEqualTo(12L);
        assertThat(response.getGuestEmail()).isEqualTo("guest2@example.com");
        assertThat(response.getGuestConfirmedBookingsCount()).isEqualTo(1);
        assertThat(response.getRoomId()).isEqualTo(20L);
        assertThat(response.getRoomCode()).isEqualTo("R-303");
        assertThat(response.getRoomLastBookingDate()).isEqualTo(room.getLastBookingDate());

        verify(bookingJpaRepository).save(booking);
    }
}
