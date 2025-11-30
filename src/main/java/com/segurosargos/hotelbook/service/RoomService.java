package com.segurosargos.hotelbook.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.segurosargos.hotelbook.dto.RoomCreateRequestDto;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomOccupancySummaryDto;
import com.segurosargos.hotelbook.dto.RoomPageResultDto;
import com.segurosargos.hotelbook.dto.RoomSearchFilterDto;
import com.segurosargos.hotelbook.dto.RoomSummaryResponseDto;
import com.segurosargos.hotelbook.dto.RoomUpdateRequestDto;
import com.segurosargos.hotelbook.exception.BookingNotFoundException;
import com.segurosargos.hotelbook.exception.InvalidBookingException;
import com.segurosargos.hotelbook.model.Room;
import com.segurosargos.hotelbook.model.RoomEntity;
import com.segurosargos.hotelbook.repository.RoomJpaRepository;
import com.segurosargos.hotelbook.repository.RoomOccupancyView;
import com.segurosargos.hotelbook.repository.RoomRepository;
import com.segurosargos.hotelbook.repository.RoomSpecifications;

/*
 * Servicio que encapsula la lógica de negocio relacionada con las habitaciones.
 */
@Service
public class RoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

    private static final int MAX_CAPACITY = 10;

    private static final BigDecimal MAX_BASE_PRICE_PER_NIGHT = BigDecimal.valueOf(50000);

    private final RoomRepository roomRepository;

    private final RoomJpaRepository roomJpaRepository;

    public RoomService(RoomRepository roomRepository, RoomJpaRepository roomJpaRepository) {
        this.roomRepository = roomRepository;
        this.roomJpaRepository = roomJpaRepository;
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
     * Recupera todas las habitaciones para su uso en listados no paginados.
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
     * Recupera una página de habitaciones ordenadas según los criterios indicados.
     * Este método simula la paginación que luego realizará Spring Data JPA.
     */
    public RoomPageResultDto getRoomsPage(int page, int size, String sort, String direction) {
        LOGGER.info("Recuperando página de habitaciones. page={}, size={}, sort={}, direction={}.",
                page, size, sort, direction);

        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        List<Room> allRooms = roomRepository.findAll();

        Comparator<Room> comparator = buildRoomComparator(sort);

        if (comparator != null) {
            if ("desc".equalsIgnoreCase(direction)) {
                comparator = comparator.reversed();
            }
            allRooms = allRooms.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        return buildPageResult(allRooms, normalizedPage, normalizedSize, sort, direction);
    }

    /*
     * Recupera una página de habitaciones aplicando filtros opcionales por nombre y rango de precio.
     * Si no se especifica ningún filtro, delega en getRoomsPage.
     */
    public RoomPageResultDto searchRooms(String nameFilter,
                                         BigDecimal minPrice,
                                         BigDecimal maxPrice,
                                         int page,
                                         int size,
                                         String sort,
                                         String direction) {

        LOGGER.info(
                "Buscando habitaciones con filtros. nameFilter={}, minPrice={}, maxPrice={}, page={}, size={}, sort={}, direction={}.",
                nameFilter, minPrice, maxPrice, page, size, sort, direction);

        boolean hasName = nameFilter != null && !nameFilter.trim().isEmpty();
        boolean hasPriceRange = minPrice != null || maxPrice != null;

        if (!hasName && !hasPriceRange) {
            LOGGER.info("Sin filtros, delegando en getRoomsPage.");
            return getRoomsPage(page, size, sort, direction);
        }

        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        List<Room> filteredRooms;

        if (hasName && !hasPriceRange) {
            String normalizedName = nameFilter.trim();
            filteredRooms = roomRepository.findByNameContainingIgnoreCase(normalizedName);
        } else if (!hasName && hasPriceRange) {
            BigDecimal effectiveMin = minPrice == null ? BigDecimal.ZERO : minPrice;
            BigDecimal effectiveMax = maxPrice == null ? MAX_BASE_PRICE_PER_NIGHT : maxPrice;
            filteredRooms = roomRepository.findByBasePricePerNightBetween(effectiveMin, effectiveMax);
        } else {
            String normalizedName = nameFilter.trim();
            BigDecimal effectiveMin = minPrice == null ? BigDecimal.ZERO : minPrice;
            BigDecimal effectiveMax = maxPrice == null ? MAX_BASE_PRICE_PER_NIGHT : maxPrice;
            List<Room> roomsByName = roomRepository.findByNameContainingIgnoreCase(normalizedName);
            filteredRooms = roomsByName.stream()
                    .filter(room -> room.getBasePricePerNight() != null)
                    .filter(room -> room.getBasePricePerNight().compareTo(effectiveMin) >= 0)
                    .filter(room -> room.getBasePricePerNight().compareTo(effectiveMax) <= 0)
                    .collect(Collectors.toList());
        }

        Comparator<Room> comparator = buildRoomComparator(sort);

        if (comparator != null) {
            if ("desc".equalsIgnoreCase(direction)) {
                comparator = comparator.reversed();
            }
            filteredRooms = filteredRooms.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        return buildPageResult(filteredRooms, normalizedPage, normalizedSize, sort, direction);
    }

    /*
     * Recupera una página de habitaciones aplicando un conjunto avanzado de filtros
     * basado en Specifications. Todos los campos del filtro son opcionales.
     */
    public RoomPageResultDto searchRoomsAdvanced(RoomSearchFilterDto filter,
                                                 int page,
                                                 int size,
                                                 String sort,
                                                 String direction) {

        LOGGER.info(
                "Buscando habitaciones con filtro avanzado. filter={}, page={}, size={}, sort={}, direction={}.",
                filter, page, size, sort, direction);

        if (filter == null) {
            LOGGER.info("Filtro avanzado nulo, delegando en getRoomsPage.");
            return getRoomsPage(page, size, sort, direction);
        }

        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size <= 0 ? 10 : size;

        Specification<RoomEntity> specification = Specification
                .where(RoomSpecifications.nameContainsIgnoreCase(filter.getNameContains()))
                .and(RoomSpecifications.capacityGreaterOrEqual(filter.getMinCapacity()))
                .and(RoomSpecifications.basePriceGreaterOrEqual(filter.getMinBasePricePerNight()))
                .and(RoomSpecifications.basePriceLessOrEqual(filter.getMaxBasePricePerNight()))
                .and(RoomSpecifications.onlyActive(filter.getOnlyActive()))
                .and(RoomSpecifications.availableBetween(filter.getAvailableFrom(),
                        filter.getAvailableTo()));

        List<RoomEntity> entities = roomJpaRepository.findAll(specification);

        LOGGER.info("La consulta avanzada recuperó {} habitaciones desde la base de datos.",
                entities.size());

        List<Room> rooms = entities.stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());

        Comparator<Room> comparator = buildRoomComparator(sort);

        if (comparator != null) {
            if ("desc".equalsIgnoreCase(direction)) {
                comparator = comparator.reversed();
            }
            rooms = rooms.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        return buildPageResult(rooms, normalizedPage, normalizedSize, sort, direction);
    }

    /*
     * Recupera un resumen de ocupación por habitación para una fecha de referencia.
     * Si la fecha de referencia es nula, se utiliza la fecha actual del sistema.
     */
    public List<RoomOccupancySummaryDto> getRoomOccupancySummary(LocalDate referenceDate) {
        LOGGER.info("Recuperando resumen de ocupación de habitaciones para la fecha {}.",
                referenceDate);

        LocalDate effectiveDate = referenceDate != null ? referenceDate : LocalDate.now();

        List<RoomOccupancyView> views = roomJpaRepository
                .findRoomOccupancySummaryByReferenceDate(effectiveDate);

        LOGGER.info("Se construirá el resumen de ocupación para {} habitaciones.", views.size());

        return views.stream()
                .map(this::mapToRoomOccupancySummaryDto)
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
     * Construye un comparador para ordenar habitaciones según el campo indicado.
     */
    private Comparator<Room> buildRoomComparator(String sort) {
        if (sort == null || sort.isBlank()) {
            return Comparator.comparing(Room::getId, Comparator.nullsLast(Long::compareTo));
        }

        String normalizedSort = sort.trim().toLowerCase();

        switch (normalizedSort) {
            case "code":
                return Comparator.comparing(
                        Room::getCode,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "name":
                return Comparator.comparing(
                        Room::getName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "capacity":
                return Comparator.comparing(
                        Room::getCapacity,
                        Comparator.nullsLast(Integer::compareTo));
            case "price":
            case "basepricepernight":
                return Comparator.comparing(
                        Room::getBasePricePerNight,
                        Comparator.nullsLast(BigDecimal::compareTo));
            case "active":
                return Comparator.comparing(Room::isActive);
            case "id":
            default:
                return Comparator.comparing(
                        Room::getId,
                        Comparator.nullsLast(Long::compareTo));
        }
    }

    /*
     * Construye un resultado de página a partir de una lista ya filtrada y ordenada.
     */
    private RoomPageResultDto buildPageResult(List<Room> rooms,
                                              int page,
                                              int size,
                                              String sort,
                                              String direction) {

        long totalElements = rooms.size();
        int totalPages = totalElements == 0 ? 0
                : (int) ((totalElements + size - 1) / size);

        int normalizedPage = page;
        if (totalPages > 0 && normalizedPage >= totalPages) {
            normalizedPage = totalPages - 1;
        }

        int fromIndex = normalizedPage * size;
        int toIndex = Math.min(fromIndex + size, (int) totalElements);

        List<RoomSummaryResponseDto> content;

        if (fromIndex >= toIndex || totalElements == 0) {
            content = List.of();
        } else {
            content = rooms.subList(fromIndex, toIndex).stream()
                    .map(this::mapToSummaryResponse)
                    .collect(Collectors.toList());
        }

        boolean first = totalPages == 0 || normalizedPage == 0;
        boolean last = totalPages == 0 || normalizedPage >= totalPages - 1;

        LOGGER.info(
                "Página de habitaciones construida. page={}, size={}, totalElements={}, totalPages={}.",
                normalizedPage, size, totalElements, totalPages);

        return new RoomPageResultDto(
                content,
                normalizedPage,
                size,
                totalElements,
                totalPages,
                first,
                last,
                sort,
                direction
        );
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

    private RoomOccupancySummaryDto mapToRoomOccupancySummaryDto(RoomOccupancyView view) {
        return RoomOccupancySummaryDto.builder()
                .roomId(view.getRoomId())
                .roomCode(view.getRoomCode())
                .roomName(view.getRoomName())
                .basePricePerNight(view.getBasePricePerNight())
                .activeBookingsCount(view.getActiveBookingsCount())
                .build();
    }

    private Room mapToModel(RoomEntity entity) {
        if (entity == null) {
            return null;
        }
        return Room.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .capacity(entity.getCapacity())
                .basePricePerNight(entity.getBasePricePerNight())
                .active(entity.isActive())
                .internalNotes(entity.getInternalNotes())
                .build();
    }
}
