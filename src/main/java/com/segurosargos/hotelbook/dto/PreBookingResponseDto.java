package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/*
 * DTO de salida para devolver la información de una pre-reserva creada.
 */
@Data
@Builder
public class PreBookingResponseDto {

    /*
     * Identificador generado para la pre-reserva.
     */
    private Long id;

    /*
     * Nombre del huésped asociado a la pre-reserva.
     */
    private String guestName;

    /*
     * Tipo de habitación solicitada.
     */
    private String roomType;

    /*
     * Número de noches de la estancia.
     */
    private Integer numberOfNights;

    /*
     * Tarifa por noche utilizada para el cálculo.
     */
    private BigDecimal nightlyRate;

    /*
     * Importe total estimado.
     */
    private BigDecimal totalAmount;

    /*
     * Fecha y hora de creación de la pre-reserva.
     */
    private LocalDateTime createdAt;
}
