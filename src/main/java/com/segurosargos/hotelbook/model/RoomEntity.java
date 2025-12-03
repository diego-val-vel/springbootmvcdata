package com.segurosargos.hotelbook.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Entidad JPA que representa una habitación persistida en la base de datos.
 */
@Entity
@Table(name = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity {

    /*
     * Identificador único de la habitación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /*
     * Código único de la habitación, por ejemplo "STD-101".
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /*
     * Nombre descriptivo de la habitación, por ejemplo "Standard King".
     */
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /*
     * Capacidad máxima de huéspedes para esta habitación.
     */
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    /*
     * Precio base por noche de la habitación.
     */
    @Column(name = "base_price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePricePerNight;

    /*
     * Indicador de si la habitación está activa en el catálogo.
     */
    @Column(name = "active", nullable = false)
    private boolean active;

    /*
     * Notas internas de la habitación que no deben exponerse en las respuestas JSON.
     */
    @JsonIgnore
    @Column(name = "internal_notes")
    private String internalNotes;

    /*
     * Fecha de la última reserva confirmada asociada a esta habitación.
     */
    @Column(name = "last_booking_date")
    private LocalDate lastBookingDate;

    /*
     * Version de la entidad para soporte de concurrencia optimista.
     */
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    /*
     * Lista de reservas asociadas a esta habitación.
     */
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<BookingEntity> bookings = new ArrayList<>();
}
