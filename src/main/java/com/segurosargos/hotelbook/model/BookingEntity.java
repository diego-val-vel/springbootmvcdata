package com.segurosargos.hotelbook.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Entidad JPA que representa una reserva de hotel.
 * Vincula una habitación y un huésped en un rango de fechas.
 */
@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

    /*
     * Identificador único de la reserva.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /*
     * Fecha de check-in de la reserva.
     */
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    /*
     * Fecha de check-out de la reserva.
     */
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    /*
     * Importe total de la reserva.
     */
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    /*
     * Estatus de la reserva, por ejemplo: CREATED, CONFIRMED, CANCELLED.
     */
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    /*
     * Habitación asociada a la reserva.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    /*
     * Huésped asociado a la reserva.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private GuestEntity guest;
}
