package com.segurosargos.hotelbook.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.segurosargos.hotelbook.model.Room;
import com.segurosargos.hotelbook.model.RoomEntity;

/*
 * Implementación de RoomRepository basada en JPA y PostgreSQL.
 * Adapta el modelo Room usado por las capas superiores a la entidad RoomEntity.
 */
@Repository
public class JpaRoomRepositoryAdapter implements RoomRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JpaRoomRepositoryAdapter.class);

    private final RoomJpaRepository roomJpaRepository;

    public JpaRoomRepositoryAdapter(RoomJpaRepository roomJpaRepository) {
        this.roomJpaRepository = roomJpaRepository;
    }

    @Override
    public Room save(Room room) {
        RoomEntity entityToSave = mapToEntity(room);
        RoomEntity savedEntity = roomJpaRepository.save(entityToSave);
        Room savedRoom = mapToModel(savedEntity);
        LOGGER.debug("Habitación persistida en base de datos con id {} y código {}.",
                savedRoom.getId(), savedRoom.getCode());
        return savedRoom;
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomJpaRepository.findById(id)
                .map(this::mapToModel);
    }

    @Override
    public List<Room> findAll() {
        List<RoomEntity> entities = roomJpaRepository.findAll();
        LOGGER.debug("Se recuperaron {} habitaciones desde la base de datos.", entities.size());
        return entities.stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        LOGGER.debug("Eliminando habitación en base de datos con id {}.", id);
        roomJpaRepository.deleteById(id);
    }

    @Override
    public List<Room> findByNameContainingIgnoreCase(String name) {
        List<RoomEntity> entities = roomJpaRepository.findByNameContainingIgnoreCase(name);
        LOGGER.debug("Se encontraron {} habitaciones en base de datos que coinciden con el nombre '{}'.",
                entities.size(), name);
        return entities.stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> findByBasePricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        List<RoomEntity> entities = roomJpaRepository.findByBasePricePerNightBetween(minPrice, maxPrice);
        LOGGER.debug(
                "Se encontraron {} habitaciones en base de datos dentro del rango de precio [{}, {}].",
                entities.size(), minPrice, maxPrice);
        return entities.stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    private RoomEntity mapToEntity(Room room) {
        if (room == null) {
            return null;
        }
        return RoomEntity.builder()
                .id(room.getId())
                .code(room.getCode())
                .name(room.getName())
                .capacity(room.getCapacity())
                .basePricePerNight(room.getBasePricePerNight())
                .active(room.isActive())
                .internalNotes(room.getInternalNotes())
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
