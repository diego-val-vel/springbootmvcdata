package com.segurosargos.hotelbook.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.segurosargos.hotelbook.model.Room;

/*
 * Implementación en memoria de RoomRepository.
 * Usa un mapa concurrente y un contador atómico para generar ids.
 * Esta implementación es adecuada para ejercicios y pruebas simples.
 */
@Repository
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
}
