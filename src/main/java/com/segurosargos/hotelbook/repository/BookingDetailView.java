package com.segurosargos.hotelbook.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

/*
 * Proyección de solo lectura que representa el detalle de una reserva
 * combinando datos de la reserva, la habitación y el huésped.
 */
public interface BookingDetailView {

    /*
     * Identificador interno de la reserva.
     */
    Long getBookingId();

    /*
     * Código de la habitación asociada a la reserva.
     */
    String getRoomCode();

    /*
     * Nombre descriptivo de la habitación asociada.
     */
    String getRoomName();

    /*
     * Nombre del huésped asociado a la reserva.
     */
    String getGuestFirstName();

    /*
     * Apellido del huésped asociado a la reserva.
     */
    String getGuestLastName();

    /*
     * Correo electrónico del huésped asociado a la reserva.
     */
    String getGuestEmail();

    /*
     * Fecha de check-in de la reserva.
     */
    LocalDate getCheckInDate();

    /*
     * Fecha de check-out de la reserva.
     */
    LocalDate getCheckOutDate();

    /*
     * Importe total de la reserva.
     */
    BigDecimal getTotalPrice();

    /*
     * Estatus actual de la reserva.
     */
    String getStatus();
}
