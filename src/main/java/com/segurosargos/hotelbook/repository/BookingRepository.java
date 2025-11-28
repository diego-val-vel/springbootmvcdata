package com.segurosargos.hotelbook.repository;

import com.segurosargos.hotelbook.model.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Repositorio Spring Data JPA para la entidad BookingEntity.
 */
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
}
