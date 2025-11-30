package com.segurosargos.hotelbook.repository;

import java.math.BigDecimal;

/*
 * Proyección de solo lectura que representa el resumen de ocupación
 * de una habitación en una consulta agregada.
 */
public interface RoomOccupancyView {

    /*
     * Identificador interno de la habitación.
     */
    Long getRoomId();

    /*
     * Código único de la habitación.
     */
    String getRoomCode();

    /*
     * Nombre descriptivo de la habitación.
     */
    String getRoomName();

    /*
     * Precio base por noche configurado para la habitación.
     */
    BigDecimal getBasePricePerNight();

    /*
     * Número de reservas activas asociadas a la habitación.
     */
    long getActiveBookingsCount();
}
