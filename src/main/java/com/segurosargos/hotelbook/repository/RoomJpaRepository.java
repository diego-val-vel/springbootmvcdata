package com.segurosargos.hotelbook.repository;

import java.math.BigDecimal;
import java.util.List;
import com.segurosargos.hotelbook.model.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Repositorio Spring Data JPA para la entidad RoomEntity.
 */
public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long> {

    /*
     * Busca habitaciones cuyo nombre contenga el texto indicado, ignorando mayúsculas/minúsculas.
     */
    List<RoomEntity> findByNameContainingIgnoreCase(String name);

    /*
     * Busca habitaciones cuyo precio base por noche esté dentro del rango indicado.
     */
    List<RoomEntity> findByBasePricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice);
}
