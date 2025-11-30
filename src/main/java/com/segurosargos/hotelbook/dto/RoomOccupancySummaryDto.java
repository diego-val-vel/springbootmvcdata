package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

/*
 * Representa un resumen de ocupación para una habitación en una fecha de referencia.
 * Incluye datos básicos de la habitación y el número de reservas activas asociadas.
 */
@Data
@Builder
public class RoomOccupancySummaryDto {

    /*
     * Identificador interno de la habitación.
     */
    private Long roomId;

    /*
     * Código único de la habitación, por ejemplo "STD-101".
     */
    private String roomCode;

    /*
     * Nombre descriptivo de la habitación, por ejemplo "Standard King".
     */
    private String roomName;

    /*
     * Precio base por noche configurado para la habitación.
     */
    private BigDecimal basePricePerNight;

    /*
     * Número de reservas activas asociadas a la habitación para la fecha de referencia.
     */
    private long activeBookingsCount;
}
