package com.segurosargos.hotelbook.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.segurosargos.hotelbook.dto.RoomCreateRequestDto;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomSummaryResponseDto;
import com.segurosargos.hotelbook.dto.RoomUpdateRequestDto;
import com.segurosargos.hotelbook.exception.BookingNotFoundException;
import com.segurosargos.hotelbook.exception.InvalidBookingException;
import com.segurosargos.hotelbook.model.Room;
import com.segurosargos.hotelbook.repository.RoomRepository;

/*
 * Servicio que encapsula la lógica de negocio relacionada con las habitaciones.
 */
@Service
public class RoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

    private static final int MAX_CAPACITY = 10;

    private static final BigDecimal MAX_BASE_PRICE_PER_NIGHT = BigDecimal.valueOf(50000);

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /*
     * Crea una nueva habitación a partir de los datos de entrada.
     */
    public RoomDetailResponseDto createRoom(RoomCreateRequestDto requestDto) {
        LOGGER.info("Creando una nueva habitación con código {}.", requestDto.getCode());

        validateBusinessRulesForCreate(requestDto);

        Room room = Room.builder()
                .id(null)
                .code(requestDto.getCode().trim())
                .name(requestDto.getName().trim())
                .capacity(requestDto.getCapacity())
                .basePricePerNight(requestDto.getBasePricePerNight())
                .active(true)
                .build();

        Room saved = roomRepository.save(room);

        LOGGER.info("Habitación creada con id {} y código {}.", saved.getId(), saved.getCode());

        return mapToDetailResponse(saved);
    }

    /*
     * Actualiza una habitación existente identificada por su id.
     */
    public RoomDetailResponseDto updateRoom(Long id, RoomUpdateRequestDto requestDto) {
        LOGGER.info("Actualizando habitación con id {}.", id);

        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        "No se encontró la habitación con id " + id));

        validateBusinessRulesForUpdate(requestDto);

        existing.setName(requestDto.getName().trim());
        existing.setCapacity(requestDto.getCapacity());
        existing.setBasePricePerNight(requestDto.getBasePricePerNight());
        existing.setActive(Boolean.TRUE.equals(requestDto.getActive()));

        Room saved = roomRepository.save(existing);

        LOGGER.info("Habitación actualizada con id {}.", saved.getId());

        return mapToDetailResponse(saved);
    }

    /*
     * Obtiene el detalle de una habitación por su id.
     */
    public RoomDetailResponseDto getRoomById(Long id) {
        LOGGER.info("Buscando habitación con id {}.", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        "No se encontró la habitación con id " + id));

        LOGGER.info("Habitación encontrada con id {} y código {}.", room.getId(), room.getCode());

        return mapToDetailResponse(room);
    }

    /*
     * Recupera todas las habitaciones para su uso en listados.
     */
    public List<RoomSummaryResponseDto> getAllRooms() {
        LOGGER.info("Recuperando todas las habitaciones.");

        List<Room> rooms = roomRepository.findAll();

        LOGGER.info("Se encontraron {} habitaciones.", rooms.size());

        return rooms.stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    /*
     * Elimina una habitación por su id.
     */
    public void deleteRoom(Long id) {
        LOGGER.info("Eliminando habitación con id {}.", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        "No se encontró la habitación con id " + id));

        roomRepository.deleteById(room.getId());

        LOGGER.info("Habitación eliminada con id {}.", id);
    }

    /*
     * Reglas de negocio adicionales para la creación de habitaciones.
     */
    private void validateBusinessRulesForCreate(RoomCreateRequestDto requestDto) {
        if (requestDto == null) {
            throw new InvalidBookingException("Los datos de la habitación no pueden ser nulos.");
        }

        String normalizedCode = requestDto.getCode() == null ? null : requestDto.getCode().trim();

        if (normalizedCode == null || normalizedCode.isEmpty()) {
            throw new InvalidBookingException("El código de la habitación es obligatorio.");
        }

        boolean codeAlreadyExists = roomRepository.findAll().stream()
                .map(Room::getCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .anyMatch(existingCode -> existingCode.equalsIgnoreCase(normalizedCode));

        if (codeAlreadyExists) {
            throw new InvalidBookingException(
                    "Ya existe una habitación registrada con el código " + normalizedCode + ".");
        }

        validateCapacityAndPrice(requestDto.getCapacity(), requestDto.getBasePricePerNight());
    }

    /*
     * Reglas de negocio adicionales para la actualización de habitaciones.
     */
    private void validateBusinessRulesForUpdate(RoomUpdateRequestDto requestDto) {
        if (requestDto == null) {
            throw new InvalidBookingException("Los datos de la habitación no pueden ser nulos.");
        }
        validateCapacityAndPrice(requestDto.getCapacity(), requestDto.getBasePricePerNight());
    }

    /*
     * Reglas de negocio comunes relacionadas con capacidad y precio.
     */
    private void validateCapacityAndPrice(Integer capacity, BigDecimal basePricePerNight) {
        if (capacity != null && capacity > MAX_CAPACITY) {
            throw new InvalidBookingException(
                    "La capacidad máxima permitida para una habitación es " + MAX_CAPACITY + ".");
        }

        if (basePricePerNight != null
                && basePricePerNight.compareTo(MAX_BASE_PRICE_PER_NIGHT) > 0) {
            throw new InvalidBookingException(
                    "El precio base por noche no puede ser mayor a " + MAX_BASE_PRICE_PER_NIGHT
                            + ".");
        }
    }

    /*
     * Conversión de entidad Room a DTO de resumen.
     */
    private RoomSummaryResponseDto mapToSummaryResponse(Room room) {
        return RoomSummaryResponseDto.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .capacity(room.getCapacity())
                .basePricePerNight(room.getBasePricePerNight())
                .active(room.isActive())
                .build();
    }

    /*
     * Conversión de entidad Room a DTO de detalle.
     */
    private RoomDetailResponseDto mapToDetailResponse(Room room) {
        return RoomDetailResponseDto.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .capacity(room.getCapacity())
                .basePricePerNight(room.getBasePricePerNight())
                .active(room.isActive())
                .build();
    }
}
