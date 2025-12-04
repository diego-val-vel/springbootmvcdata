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

/**
 * Pruebas con @DataJpaTest para RoomJpaRepository.
 * Se validan consultas derivadas y una consulta JPQL agregada que utiliza una proyeccion.
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
class RoomJpaRepositoryTest {

    @Autowired
    private RoomJpaRepository roomJpaRepository;

    @Autowired
    private BookingJpaRepository bookingJpaRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Test
    @DisplayName("findByNameContainingIgnoreCase devuelve solo las habitaciones cuyo nombre contiene el texto indicado")
    void findByNameContainingIgnoreCase_returnsMatchingRoomsOnly() {
        RoomEntity room1 = new RoomEntity();
        room1.setCode("R-101");
        room1.setName("Standard King");
        room1.setCapacity(2);
        room1.setBasePricePerNight(new BigDecimal("100.00"));
        room1.setActive(true);

        RoomEntity room2 = new RoomEntity();
        room2.setCode("R-102");
        room2.setName("Deluxe Queen");
        room2.setCapacity(3);
        room2.setBasePricePerNight(new BigDecimal("150.00"));
        room2.setActive(true);

        RoomEntity room3 = new RoomEntity();
        room3.setCode("R-103");
        room3.setName("Family Suite");
        room3.setCapacity(4);
        room3.setBasePricePerNight(new BigDecimal("200.00"));
        room3.setActive(true);

        roomJpaRepository.save(room1);
        roomJpaRepository.save(room2);
        roomJpaRepository.save(room3);

        List<RoomEntity> result = roomJpaRepository.findByNameContainingIgnoreCase("standard");

        Assertions.assertThat(result)
                .hasSize(1)
                .first()
                .extracting(RoomEntity::getName)
                .isEqualTo("Standard King");
    }

    @Test
    @DisplayName("findByBasePricePerNightBetween devuelve las habitaciones dentro del rango indicado")
    void findByBasePricePerNightBetween_returnsRoomsWithinRange() {
        RoomEntity cheap = new RoomEntity();
        cheap.setCode("R-201");
        cheap.setName("Economy");
        cheap.setCapacity(2);
        cheap.setBasePricePerNight(new BigDecimal("80.00"));
        cheap.setActive(true);

        RoomEntity mid = new RoomEntity();
        mid.setCode("R-202");
        mid.setName("Standard");
        mid.setCapacity(2);
        mid.setBasePricePerNight(new BigDecimal("120.00"));
        mid.setActive(true);

        RoomEntity expensive = new RoomEntity();
        expensive.setCode("R-203");
        expensive.setName("Premium");
        expensive.setCapacity(2);
        expensive.setBasePricePerNight(new BigDecimal("250.00"));
        expensive.setActive(true);

        roomJpaRepository.save(cheap);
        roomJpaRepository.save(mid);
        roomJpaRepository.save(expensive);

        List<RoomEntity> result = roomJpaRepository.findByBasePricePerNightBetween(
                new BigDecimal("90.00"),
                new BigDecimal("200.00")
        );

        Assertions.assertThat(result)
                .extracting(RoomEntity::getCode)
                .containsExactlyInAnyOrder("R-202");
    }

    @Test
    @DisplayName("findRoomOccupancySummaryByReferenceDate calcula correctamente el numero de reservas activas por habitacion")
    void findRoomOccupancySummaryByReferenceDate_returnsCorrectOccupancySummary() {
        RoomEntity room1 = new RoomEntity();
        room1.setCode("R-301");
        room1.setName("Business");
        room1.setCapacity(2);
        room1.setBasePricePerNight(new BigDecimal("180.00"));
        room1.setActive(true);

        RoomEntity room2 = new RoomEntity();
        room2.setCode("R-302");
        room2.setName("Suite");
        room2.setCapacity(3);
        room2.setBasePricePerNight(new BigDecimal("300.00"));
        room2.setActive(true);

        room1 = roomJpaRepository.save(room1);
        room2 = roomJpaRepository.save(room2);

        Long room1Id = room1.getId();
        Long room2Id = room2.getId();

        GuestEntity guest = GuestEntity.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan.perez@example.com")
                .confirmedBookingsCount(0)
                .build();
        guest = guestRepository.save(guest);

        LocalDate referenceDate = LocalDate.of(2025, 3, 15);

        BookingEntity booking1 = BookingEntity.builder()
                .checkInDate(LocalDate.of(2025, 3, 10))
                .checkOutDate(LocalDate.of(2025, 3, 20))
                .totalPrice(new BigDecimal("500.00"))
                .status("CONFIRMED")
                .room(room1)
                .guest(guest)
                .build();

        BookingEntity booking2 = BookingEntity.builder()
                .checkInDate(LocalDate.of(2025, 3, 1))
                .checkOutDate(LocalDate.of(2025, 3, 5))
                .totalPrice(new BigDecimal("300.00"))
                .status("CONFIRMED")
                .room(room1)
                .guest(guest)
                .build();

        BookingEntity booking3 = BookingEntity.builder()
                .checkInDate(LocalDate.of(2025, 3, 10))
                .checkOutDate(LocalDate.of(2025, 3, 20))
                .totalPrice(new BigDecimal("800.00"))
                .status("CANCELLED")
                .room(room2)
                .guest(guest)
                .build();

        bookingJpaRepository.save(booking1);
        bookingJpaRepository.save(booking2);
        bookingJpaRepository.save(booking3);

        List<RoomOccupancyView> summary = roomJpaRepository
                .findRoomOccupancySummaryByReferenceDate(referenceDate);

        RoomOccupancyView room1Summary = summary.stream()
                .filter(view -> view.getRoomId().equals(room1Id))
                .findFirst()
                .orElseThrow();

        RoomOccupancyView room2Summary = summary.stream()
                .filter(view -> view.getRoomId().equals(room2Id))
                .findFirst()
                .orElseThrow();

        Assertions.assertThat(room1Summary.getRoomCode()).isEqualTo("R-301");
        Assertions.assertThat(room1Summary.getActiveBookingsCount()).isEqualTo(1L);

        Assertions.assertThat(room2Summary.getRoomCode()).isEqualTo("R-302");
        Assertions.assertThat(room2Summary.getActiveBookingsCount()).isEqualTo(0L);
    }
}
