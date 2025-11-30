package com.segurosargos.hotelbook.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO de respuesta para operaciones de cambio de estatus de una reserva.
 * Resume la información relevante del cambio incluyendo datos del huésped
 * y de la habitación asociados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusChangeResponseDto {

    /*
     * Identificador de la reserva afectada.
     */
    private Long bookingId;

    /*
     * Estatus previo de la reserva antes de aplicar la operación.
     */
    private String previousStatus;

    /*
     * Nuevo estatus de la reserva después de aplicar la operación.
     */
    private String newStatus;

    /*
     * Identificador del huésped asociado.
     */
    private Long guestId;

    /*
     * Correo electrónico del huésped asociado.
     */
    private String guestEmail;

    /*
     * Número de reservas confirmadas que tiene el huésped después del cambio.
     */
    private Integer guestConfirmedBookingsCount;

    /*
     * Identificador de la habitación asociada.
     */
    private Long roomId;

    /*
     * Código de la habitación asociada.
     */
    private String roomCode;

    /*
     * Fecha de la última reserva confirmada para la habitación
     * después de aplicar la operación.
     */
    private LocalDate roomLastBookingDate;
}
