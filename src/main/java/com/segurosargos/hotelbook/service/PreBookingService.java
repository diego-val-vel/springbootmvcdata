package com.segurosargos.hotelbook.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.segurosargos.hotelbook.dto.PreBookingRequestDto;
import com.segurosargos.hotelbook.dto.PreBookingResponseDto;
import com.segurosargos.hotelbook.exception.InvalidBookingException;
import com.segurosargos.hotelbook.model.PreBooking;
import com.segurosargos.hotelbook.repository.PreBookingRepository;

/*
 * Servicio que encapsula la lógica de negocio relacionada con las pre-reservas.
 */
@Service
public class PreBookingService {

    /*
     * Logger para registrar la lógica de negocio.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PreBookingService.class);

    private final PreBookingRepository preBookingRepository;

    public PreBookingService(PreBookingRepository preBookingRepository) {
        this.preBookingRepository = preBookingRepository;
    }

    /*
     * Crea una nueva pre-reserva en memoria a partir del DTO de entrada.
     */
    public PreBookingResponseDto createPreBooking(PreBookingRequestDto request) {
        LOGGER.info("Creando pre-reserva para huésped {}.", request.getGuestName());

        validarReglasDeNegocio(request);

        BigDecimal nightlyRate = calcularTarifaPorNoche(request.getRoomType());
        BigDecimal totalAmount = nightlyRate.multiply(BigDecimal.valueOf(request.getNumberOfNights()));

        PreBooking preBooking = PreBooking.builder()
                .guestName(request.getGuestName())
                .roomType(request.getRoomType())
                .numberOfNights(request.getNumberOfNights())
                .nightlyRate(nightlyRate)
                .totalAmount(totalAmount)
                .createdAt(LocalDateTime.now())
                .build();

        PreBooking saved = preBookingRepository.save(preBooking);

        LOGGER.info("Pre-reserva creada en memoria con id {}.", saved.getId());

        return PreBookingResponseDto.builder()
                .id(saved.getId())
                .guestName(saved.getGuestName())
                .roomType(saved.getRoomType())
                .numberOfNights(saved.getNumberOfNights())
                .nightlyRate(saved.getNightlyRate())
                .totalAmount(saved.getTotalAmount())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /*
     * Reglas de negocio básicas aplicadas a la pre-reserva.
     * Se podrían ir moviendo y enriqueciendo según evolucione HotelBook.
     */
    private void validarReglasDeNegocio(PreBookingRequestDto request) {
        if (request.getNumberOfNights() != null && request.getNumberOfNights() > 30) {
            throw new InvalidBookingException("No se permiten pre-reservas mayores a 30 noches.");
        }
    }

    /*
     * Calcula una tarifa diaria simbólica según el tipo de habitación.
     * En esta práctica es una regla fija en código.
     */
    private BigDecimal calcularTarifaPorNoche(String roomType) {
        if (roomType == null) {
            throw new InvalidBookingException("El tipo de habitación no puede ser nulo.");
        }

        String normalized = roomType.trim().toUpperCase();

        if ("STANDARD".equals(normalized)) {
            return BigDecimal.valueOf(1000);
        }
        if ("DOUBLE".equals(normalized)) {
            return BigDecimal.valueOf(1500);
        }
        if ("SUITE".equals(normalized)) {
            return BigDecimal.valueOf(2500);
        }

        LOGGER.warn("Tipo de habitación {} no reconocido, usando tarifa por defecto.", roomType);
        return BigDecimal.valueOf(1200);
    }
}
