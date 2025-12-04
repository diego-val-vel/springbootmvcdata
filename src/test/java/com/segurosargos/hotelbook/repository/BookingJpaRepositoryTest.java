package com.segurosargos.hotelbook.repository;

import com.segurosargos.hotelbook.model.BookingEntity;
import com.segurosargos.hotelbook.model.GuestEntity;
import com.segurosargos.hotelbook.model.RoomEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pruebas con @DataJpaTest para BookingJpaRepository.
 * Se valida una consulta con JOIN FETCH que inicializa las relaciones room y guest.
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class BookingJpaRepositoryTest {

    @Autowired
    private BookingJpaRepository bookingJpaRepository;

    @Autowired
    private RoomJpaRepository roomJpaRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Test
    @Transactional
    @DisplayName("findAllWithRoomAndGuestFetchJoin devuelve reservas con sus asociaciones room y guest inicializadas")
    void findAllWithRoomAndGuestFetchJoin_returnsBookingsWithInitializedAssociations() {
        RoomEntity room = new RoomEntity();
        room.setCode("R-401");
        room.setName("Executive");
        room.setCapacity(2);
        room.setBasePricePerNight(new BigDecimal("220.00"));
        room.setActive(true);
        room = roomJpaRepository.save(room);

        GuestEntity guest = GuestEntity.builder()
                .firstName("Ana")
                .lastName("López")
                .email("ana.lopez@example.com")
                .confirmedBookingsCount(0)
                .build();
        guest = guestRepository.save(guest);

        BookingEntity booking = BookingEntity.builder()
                .checkInDate(LocalDate.of(2025, 7, 1))
                .checkOutDate(LocalDate.of(2025, 7, 5))
                .totalPrice(new BigDecimal("880.00"))
                .status("CONFIRMED")
                .room(room)
                .guest(guest)
                .build();
        bookingJpaRepository.save(booking);

        List<BookingEntity> result = bookingJpaRepository.findAllWithRoomAndGuestFetchJoin();

        Assertions.assertThat(result).hasSize(1);

        BookingEntity loaded = result.get(0);

        Assertions.assertThat(loaded.getRoom().getId()).isEqualTo(room.getId());
        Assertions.assertThat(loaded.getRoom().getCode()).isEqualTo("R-401");

        Assertions.assertThat(loaded.getGuest().getId()).isEqualTo(guest.getId());
        Assertions.assertThat(loaded.getGuest().getEmail()).isEqualTo("ana.lopez@example.com");
    }
}
