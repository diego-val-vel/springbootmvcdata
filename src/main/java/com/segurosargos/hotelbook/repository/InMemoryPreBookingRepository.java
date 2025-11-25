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
import com.segurosargos.hotelbook.model.PreBooking;

/*
 * Implementación en memoria de PreBookingRepository.
 * Usa un mapa concurrente y un contador atómico para generar ids.
 * Es suficiente para esta práctica y no persiste datos fuera del ciclo de vida de la aplicación.
 */
@Repository
public class InMemoryPreBookingRepository implements PreBookingRepository {

    /*
     * Logger para registrar operaciones de repositorio.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryPreBookingRepository.class);

    /*
     * Almacenamiento en memoria de las pre-reservas.
     */
    private final ConcurrentMap<Long, PreBooking> storage = new ConcurrentHashMap<>();

    /*
     * Secuencia atómica para generar ids.
     */
    private final AtomicLong sequence = new AtomicLong(0L);

    @Override
    public PreBooking save(PreBooking preBooking) {
        if (preBooking.getId() == null) {
            Long newId = sequence.incrementAndGet();
            preBooking.setId(newId);
            LOGGER.debug("Generando nuevo id {} para pre-reserva en memoria.", newId);
        }
        storage.put(preBooking.getId(), preBooking);
        LOGGER.debug("Pre-reserva guardada en memoria con id {}.", preBooking.getId());
        return preBooking;
    }

    @Override
    public Optional<PreBooking> findById(Long id) {
        PreBooking preBooking = storage.get(id);
        if (preBooking == null) {
            LOGGER.debug("No se encontró pre-reserva en memoria con id {}.", id);
        } else {
            LOGGER.debug("Se encontró pre-reserva en memoria con id {}.", id);
        }
        return Optional.ofNullable(preBooking);
    }

    @Override
    public List<PreBooking> findAll() {
        return new ArrayList<>(storage.values());
    }
}
