package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

/*
 * Representa un detalle de reserva para reportes.
 * Incluye información de la reserva, la habitación y el huésped.
 */
@Data
@Builder
public class BookingDetailResponseDto {

    /*
     * Identificador interno de la reserva.
     */
    private Long bookingId;

    /*
     * Código de la habitación asociada a la reserva.
     */
    private String roomCode;

    /*
     * Nombre descriptivo de la habitación asociada.
     */
    private String roomName;

    /*
     * Nombre del huésped asociado a la reserva.
     */
    private String guestFirstName;

    /*
     * Apellido del huésped asociado a la reserva.
     */
    private String guestLastName;

    /*
     * Correo electrónico del huésped asociado a la reserva.
     */
    private String guestEmail;

    /*
     * Fecha de check-in de la reserva.
     */
    private LocalDate checkInDate;

    /*
     * Fecha de check-out de la reserva.
     */
    private LocalDate checkOutDate;

    /*
     * Importe total de la reserva.
     */
    private BigDecimal totalPrice;

    /*
     * Estatus actual de la reserva, por ejemplo: CREATED, CONFIRMED, CANCELLED.
     */
    private String status;
}
