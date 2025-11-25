package com.segurosargos.hotelbook.repository;

import java.util.List;
import java.util.Optional;
import com.segurosargos.hotelbook.model.Room;

/*
 * Contrato para la gestión de habitaciones en memoria.
 * La interfaz permite cambiar la implementación a PostgreSQL u otra tecnología
 * sin impactar las capas superiores.
 */
public interface RoomRepository {

    /*
     * Guarda una habitación.
     * Si no tiene id, el repositorio la genera.
     */
    Room save(Room room);

    /*
     * Busca una habitación por su id.
     */
    Optional<Room> findById(Long id);

    /*
     * Regresa todas las habitaciones registradas.
     */
    List<Room> findAll();

    /*
     * Elimina una habitación por su id.
     */
    void deleteById(Long id);
}
