package com.segurosargos.hotelbook.model;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Modelo simple que representa una habitación del hotel.
 * En este punto solo se utiliza en memoria, sin anotaciones de JPA.
 * Más adelante se podrá persistir en una base de datos relacional.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    /*
     * Identificador interno de la habitación.
     */
    private Long id;

    /*
     * Código único de la habitación, por ejemplo "STD-101".
     */
    private String code;

    /*
     * Nombre descriptivo de la habitación, por ejemplo "Standard King".
     */
    private String name;

    /*
     * Capacidad máxima de huéspedes para esta habitación.
     */
    private Integer capacity;

    /*
     * Precio base por noche de la habitación.
     */
    private BigDecimal basePricePerNight;

    /*
     * Indicador de si la habitación está activa en el catálogo.
     */
    private boolean active;

    /*
     * Notas internas de la habitación que no deben exponerse en las respuestas JSON.
     */
    @JsonIgnore
    private String internalNotes;
}
