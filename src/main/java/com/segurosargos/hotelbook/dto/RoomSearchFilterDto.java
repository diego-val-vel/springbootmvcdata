package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Objeto de filtro para la búsqueda avanzada de habitaciones.
 * Todos los campos son opcionales.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchFilterDto {

    /*
     * Texto opcional que debe contener el nombre de la habitación
     * (búsqueda case-insensitive).
     */
    private String nameContains;

    /*
     * Capacidad mínima de la habitación.
     */
    private Integer minCapacity;

    /*
     * Precio base mínimo por noche.
     */
    private BigDecimal minBasePricePerNight;

    /*
     * Precio base máximo por noche.
     */
    private BigDecimal maxBasePricePerNight;

    /*
     * Indica si solo se deben considerar habitaciones activas.
     */
    private Boolean onlyActive;

    /*
     * Fecha inicial del rango para verificar disponibilidad.
     */
    private LocalDate availableFrom;

    /*
     * Fecha final del rango para verificar disponibilidad.
     */
    private LocalDate availableTo;
}
