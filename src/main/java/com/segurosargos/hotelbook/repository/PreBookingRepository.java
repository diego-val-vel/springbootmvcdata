package com.segurosargos.hotelbook.repository;

import java.util.List;
import java.util.Optional;
import com.segurosargos.hotelbook.model.PreBooking;

/*
 * Contrato para la gestión de pre-reservas.
 * En esta práctica se implementa en memoria, pero la interfaz
 * permite cambiar a PostgreSQL más adelante sin tocar las capas superiores.
 */
public interface PreBookingRepository {

    /*
     * Guarda una pre-reserva.
     * Si no tiene id, el repositorio la genera.
     */
    PreBooking save(PreBooking preBooking);

    /*
     * Busca una pre-reserva por su id.
     */
    Optional<PreBooking> findById(Long id);

    /*
     * Regresa todas las pre-reservas registradas.
     */
    List<PreBooking> findAll();
}
