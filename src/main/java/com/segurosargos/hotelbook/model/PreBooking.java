package com.segurosargos.hotelbook.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Modelo simple de una pre-reserva en memoria.
 * No usa JPA ni anotaciones de persistencia.
 * Se utiliza solo para esta práctica como ejemplo de dominio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreBooking {

    /*
     * Identificador interno de la pre-reserva.
     * En esta práctica lo genera el repositorio en memoria.
     */
    private Long id;

    /*
     * Nombre del huésped que está solicitando la pre-reserva.
     */
    private String guestName;

    /*
     * Tipo de habitación solicitada.
     * Por ejemplo: STANDARD, DOUBLE, SUITE.
     */
    private String roomType;

    /*
     * Número de noches de la estancia.
     */
    private Integer numberOfNights;

    /*
     * Tarifa por noche que se utilizó para calcular el importe total.
     */
    private BigDecimal nightlyRate;

    /*
     * Importe total estimado de la estancia.
     */
    private BigDecimal totalAmount;

    /*
     * Fecha y hora en que se registró la pre-reserva.
     */
    private LocalDateTime createdAt;
}
