package com.segurosargos.hotelbook.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO de apoyo para inspeccionar reservas junto con los datos
 * esenciales de la habitación y del huésped asociados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSummaryDebugDto {

    /*
     * Identificador único de la reserva.
     */
    private Long bookingId;

    /*
     * Estatus de la reserva, por ejemplo: CREATED, CONFIRMED, CANCELLED.
     */
    private String status;

    /*
     * Fecha de check-in de la reserva.
     */
    private LocalDate checkInDate;

    /*
     * Fecha de check-out de la reserva.
     */
    private LocalDate checkOutDate;

    /*
     * Código de la habitación asociada a la reserva.
     */
    private String roomCode;

    /*
     * Nombre de la habitación asociada a la reserva.
     */
    private String roomName;

    /*
     * Nombre completo del huésped asociado a la reserva.
     */
    private String guestFullName;

    /*
     * Correo electrónico del huésped asociado a la reserva.
     */
    private String guestEmail;
}
