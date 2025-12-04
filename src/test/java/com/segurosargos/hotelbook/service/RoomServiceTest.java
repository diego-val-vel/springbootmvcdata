package com.segurosargos.hotelbook.service;

import com.segurosargos.hotelbook.dto.RoomCreateRequestDto;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomUpdateRequestDto;
import com.segurosargos.hotelbook.exception.BookingNotFoundException;
import com.segurosargos.hotelbook.exception.InvalidBookingException;
import com.segurosargos.hotelbook.model.Room;
import com.segurosargos.hotelbook.repository.RoomJpaRepository;
import com.segurosargos.hotelbook.repository.RoomRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
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
 * Pruebas unitarias para RoomService enfocadas en la logica de negocio.
 * Se utilizan mocks de los repositorios para evitar el acceso a la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomJpaRepository roomJpaRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("createRoom crea una habitacion valida y devuelve el detalle")
    void createRoom_whenRequestIsValid_createsAndReturnsDetail() {
        RoomCreateRequestDto request = RoomCreateRequestDto.builder()
                .code("HB-101")
                .name("Standard King")
                .capacity(2)
                .basePricePerNight(new BigDecimal("100.00"))
                .build();

        when(roomRepository.findAll()).thenReturn(Collections.emptyList());

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            return Room.builder()
                    .id(1L)
                    .code(room.getCode())
                    .name(room.getName())
                    .capacity(room.getCapacity())
                    .basePricePerNight(room.getBasePricePerNight())
                    .active(room.isActive())
                    .internalNotes(room.getInternalNotes())
                    .build();
        });

        RoomDetailResponseDto result = roomService.createRoom(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).isEqualTo("HB-101");
        assertThat(result.getName()).isEqualTo("Standard King");
        assertThat(result.getCapacity()).isEqualTo(2);
        assertThat(result.getBasePricePerNight()).isEqualByComparingTo("100.00");
        assertThat(result.isActive()).isTrue();

        verify(roomRepository).save(any(Room.class));
    }

    @Test
    @DisplayName("createRoom lanza InvalidBookingException cuando el request es nulo")
    void createRoom_whenRequestIsNull_throwsInvalidBookingException() {
        assertThatThrownBy(() -> roomService.createRoom(null))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    @DisplayName("createRoom lanza InvalidBookingException cuando el codigo ya existe")
    void createRoom_whenCodeAlreadyExists_throwsInvalidBookingException() {
        Room existing = Room.builder()
                .id(10L)
                .code("HB-101")
                .name("Existing room")
                .capacity(2)
                .basePricePerNight(new BigDecimal("80.00"))
                .active(true)
                .build();

        when(roomRepository.findAll()).thenReturn(List.of(existing));

        RoomCreateRequestDto request = RoomCreateRequestDto.builder()
                .code("HB-101")
                .name("New room")
                .capacity(2)
                .basePricePerNight(new BigDecimal("100.00"))
                .build();

        assertThatThrownBy(() -> roomService.createRoom(request))
                .isInstanceOf(InvalidBookingException.class);
    }

    @Test
    @DisplayName("getRoomById devuelve el detalle cuando la habitacion existe")
    void getRoomById_whenRoomExists_returnsDetail() {
        Room room = Room.builder()
                .id(5L)
                .code("HB-200")
                .name("Deluxe Double")
                .capacity(3)
                .basePricePerNight(new BigDecimal("150.00"))
                .active(true)
                .build();

        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));

        RoomDetailResponseDto result = roomService.getRoomById(5L);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getCode()).isEqualTo("HB-200");
        assertThat(result.getName()).isEqualTo("Deluxe Double");
        assertThat(result.getCapacity()).isEqualTo(3);
        assertThat(result.getBasePricePerNight()).isEqualByComparingTo("150.00");
        assertThat(result.isActive()).isTrue();
    }

    @Test
    @DisplayName("getRoomById lanza BookingNotFoundException cuando la habitacion no existe")
    void getRoomById_whenRoomDoesNotExist_throwsBookingNotFoundException() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.getRoomById(99L))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    @DisplayName("updateRoom actualiza los datos basicos cuando la habitacion existe")
    void updateRoom_whenRoomExists_updatesAndReturnsDetail() {
        Room existing = Room.builder()
                .id(10L)
                .code("HB-300")
                .name("Old name")
                .capacity(2)
                .basePricePerNight(new BigDecimal("80.00"))
                .active(false)
                .build();

        when(roomRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoomUpdateRequestDto request = RoomUpdateRequestDto.builder()
                .name("New name")
                .capacity(4)
                .basePricePerNight(new BigDecimal("120.00"))
                .active(Boolean.TRUE)
                .build();

        RoomDetailResponseDto result = roomService.updateRoom(10L, request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getCode()).isEqualTo("HB-300");
        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getCapacity()).isEqualTo(4);
        assertThat(result.getBasePricePerNight()).isEqualByComparingTo("120.00");
        assertThat(result.isActive()).isTrue();

        verify(roomRepository).save(existing);
    }

    @Test
    @DisplayName("updateRoom lanza BookingNotFoundException cuando la habitacion no existe")
    void updateRoom_whenRoomDoesNotExist_throwsBookingNotFoundException() {
        when(roomRepository.findById(123L)).thenReturn(Optional.empty());

        RoomUpdateRequestDto request = RoomUpdateRequestDto.builder()
                .name("Any")
                .capacity(2)
                .basePricePerNight(new BigDecimal("50.00"))
                .active(Boolean.TRUE)
                .build();

        assertThatThrownBy(() -> roomService.updateRoom(123L, request))
                .isInstanceOf(BookingNotFoundException.class);
    }
}
