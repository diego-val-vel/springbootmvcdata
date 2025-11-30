package com.segurosargos.hotelbook.service;

import java.util.ArrayList;
import java.util.List;
import com.segurosargos.hotelbook.dto.BookingSummaryDebugDto;
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
 * Servicio de apoyo para explorar el comportamiento de carga Lazy/Eager
 * y el problema N+1 en el contexto de las reservas.
 */
@Service
@RequiredArgsConstructor
public class BookingDebugService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDebugService.class);

    private final BookingJpaRepository bookingJpaRepository;

    /*
     * Variante inicial que utiliza findAll() sin optimizaciones. Este enfoque
     * provoca el patrón N+1 cuando se acceden a las asociaciones LAZY de room
     * y guest dentro del ciclo que recorre la lista de reservas.
     */
    @Transactional(readOnly = true)
    public List<BookingSummaryDebugDto> getAllBookingsWithAssociationsNaive() {
        LOGGER.info("Iniciando carga de todas las reservas (variante naive con N+1).");
        List<BookingEntity> bookings = bookingJpaRepository.findAll();
        LOGGER.info("Variante naive: se recuperaron {} reservas desde la base de datos.", bookings.size());
        List<BookingSummaryDebugDto> result = buildDebugSummaries(bookings);
        LOGGER.info("Variante naive: se construyeron {} elementos BookingSummaryDebugDto.", result.size());
        return result;
    }

    /*
     * Variante optimizada que utiliza EntityGraph en el repositorio para cargar
     * en una sola consulta las asociaciones room y guest. De esta forma se
     * evita el problema N+1 manteniendo la carga Lazy en el mapeo JPA.
     */
    @Transactional(readOnly = true)
    public List<BookingSummaryDebugDto> getAllBookingsWithAssociationsUsingEntityGraph() {
        LOGGER.info("Iniciando carga de todas las reservas usando EntityGraph.");
        List<BookingEntity> bookings = bookingJpaRepository.findAllWithRoomAndGuestEntityGraph();
        LOGGER.info("Variante EntityGraph: se recuperaron {} reservas desde la base de datos.", bookings.size());
        List<BookingSummaryDebugDto> result = buildDebugSummaries(bookings);
        LOGGER.info("Variante EntityGraph: se construyeron {} elementos BookingSummaryDebugDto.", result.size());
        return result;
    }

    /*
     * Variante optimizada que utiliza JOIN FETCH en la consulta JPQL del
     * repositorio. Las asociaciones room y guest se inicializan en una sola
     * consulta grande, eliminando igualmente el patrón N+1.
     */
    @Transactional(readOnly = true)
    public List<BookingSummaryDebugDto> getAllBookingsWithAssociationsUsingFetchJoin() {
        LOGGER.info("Iniciando carga de todas las reservas usando JOIN FETCH.");
        List<BookingEntity> bookings = bookingJpaRepository.findAllWithRoomAndGuestFetchJoin();
        LOGGER.info("Variante JOIN FETCH: se recuperaron {} reservas desde la base de datos.", bookings.size());
        List<BookingSummaryDebugDto> result = buildDebugSummaries(bookings);
        LOGGER.info("Variante JOIN FETCH: se construyeron {} elementos BookingSummaryDebugDto.", result.size());
        return result;
    }

    /*
     * Construye la lista de BookingSummaryDebugDto a partir de la lista de
     * BookingEntity recibida, accediendo de forma explícita a las asociaciones
     * room y guest para armar el resumen de datos.
     */
    private List<BookingSummaryDebugDto> buildDebugSummaries(List<BookingEntity> bookings) {
        List<BookingSummaryDebugDto> result = new ArrayList<>();

        for (BookingEntity booking : bookings) {
            RoomEntity room = booking.getRoom();
            GuestEntity guest = booking.getGuest();

            String roomCode = room != null ? room.getCode() : null;
            String roomName = room != null ? room.getName() : null;

            String guestFullName = null;
            String guestEmail = null;

            if (guest != null) {
                String firstName = guest.getFirstName();
                String lastName = guest.getLastName();
                guestFullName = buildGuestFullName(firstName, lastName);
                guestEmail = guest.getEmail();
            }

            BookingSummaryDebugDto summary = BookingSummaryDebugDto.builder()
                    .bookingId(booking.getId())
                    .status(booking.getStatus())
                    .checkInDate(booking.getCheckInDate())
                    .checkOutDate(booking.getCheckOutDate())
                    .roomCode(roomCode)
                    .roomName(roomName)
                    .guestFullName(guestFullName)
                    .guestEmail(guestEmail)
                    .build();

            result.add(summary);
        }

        return result;
    }

    /*
     * Construye el nombre completo del huésped a partir del nombre y los apellidos.
     */
    private String buildGuestFullName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
