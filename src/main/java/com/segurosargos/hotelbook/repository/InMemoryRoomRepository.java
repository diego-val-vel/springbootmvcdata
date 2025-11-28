package com.segurosargos.hotelbook.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import com.segurosargos.hotelbook.model.Room;

/*
 * Implementación en memoria de RoomRepository.
 * Usa un mapa concurrente y un contador atómico para generar ids.
 * Esta implementación es adecuada para ejercicios y pruebas simples.
 * Se activa solo cuando está presente el perfil "inmemory".
 */
@Repository
@Profile("inmemory")
public class InMemoryRoomRepository implements RoomRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryRoomRepository.class);

    private final ConcurrentMap<Long, Room> storage = new ConcurrentHashMap<>();

    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public Room save(Room room) {
        if (room.getId() == null) {
            Long newId = sequence.incrementAndGet();
            room.setId(newId);
            LOGGER.debug("Creando nueva habitación en memoria con id {}.", newId);
        } else {
            LOGGER.debug("Actualizando habitación en memoria con id {}.", room.getId());
        }
        storage.put(room.getId(), room);
        LOGGER.debug("Habitación guardada en memoria con id {}.", room.getId());
        return room;
    }

    @Override
    public Optional<Room> findById(Long id) {
        Room room = storage.get(id);
        if (room == null) {
            LOGGER.debug("No se encontró habitación en memoria con id {}.", id);
        } else {
            LOGGER.debug("Se encontró habitación en memoria con id {}.", id);
        }
        return Optional.ofNullable(room);
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>(storage.values());
        LOGGER.debug("Se recuperaron {} habitaciones desde la memoria.", rooms.size());
        return rooms;
    }

    @Override
    public void deleteById(Long id) {
        Room removed = storage.remove(id);
        if (removed == null) {
            LOGGER.debug("No se encontró habitación para eliminar en memoria con id {}.", id);
        } else {
            LOGGER.debug("Habitación eliminada de memoria con id {}.", id);
        }
    }

    @Override
    public List<Room> findByNameContainingIgnoreCase(String name) {
        if (name == null || name.isBlank()) {
            return findAll();
        }
        String normalized = name.toLowerCase(Locale.ROOT).trim();
        List<Room> result = storage.values().stream()
                .filter(room -> room.getName() != null)
                .filter(room -> room.getName().toLowerCase(Locale.ROOT).contains(normalized))
                .collect(Collectors.toList());
        LOGGER.debug("Se encontraron {} habitaciones en memoria que coinciden con el nombre '{}'.",
                result.size(), name);
        return result;
    }

    @Override
    public List<Room> findByBasePricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return findAll();
        }
        BigDecimal effectiveMin = minPrice == null ? BigDecimal.ZERO : minPrice;
        BigDecimal effectiveMax = maxPrice == null ? BigDecimal.valueOf(Long.MAX_VALUE) : maxPrice;

        List<Room> result = storage.values().stream()
                .filter(room -> room.getBasePricePerNight() != null)
                .filter(room -> room.getBasePricePerNight().compareTo(effectiveMin) >= 0)
                .filter(room -> room.getBasePricePerNight().compareTo(effectiveMax) <= 0)
                .collect(Collectors.toList());
        LOGGER.debug(
                "Se encontraron {} habitaciones en memoria dentro del rango de precio [{}, {}].",
                result.size(), effectiveMin, effectiveMax);
        return result;
    }
}
