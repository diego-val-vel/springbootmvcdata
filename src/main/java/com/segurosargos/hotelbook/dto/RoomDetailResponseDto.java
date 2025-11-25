package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

/*
 * Representación detallada de una habitación.
 * Se utiliza, por ejemplo, en GET /api/rooms/{id} y en respuestas de creación o actualización.
 */
@Data
@Builder
public class RoomDetailResponseDto {

    /*
     * Identificador interno de la habitación.
     */
    private Long id;

    /*
     * Código de la habitación.
     */
    private String code;

    /*
     * Nombre descriptivo de la habitación.
     */
    private String name;

    /*
     * Capacidad máxima de huéspedes.
     */
    private Integer capacity;

    /*
     * Precio base por noche.
     */
    private BigDecimal basePricePerNight;

    /*
     * Indicador de si la habitación está activa en el catálogo.
     */
    private boolean active;
}
