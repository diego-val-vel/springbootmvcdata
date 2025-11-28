package com.segurosargos.hotelbook.repository;

import com.segurosargos.hotelbook.model.GuestEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Repositorio Spring Data JPA para la entidad GuestEntity.
 */
public interface GuestRepository extends JpaRepository<GuestEntity, Long> {

    /*
     * Busca un huésped por su correo electrónico.
     */
    Optional<GuestEntity> findByEmail(String email);
}
