package com.segurosargos.hotelbook.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Entidad JPA que representa a un huésped persistido en la base de datos.
 */
@Entity
@Table(name = "guests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestEntity {

    /*
     * Identificador único del huésped.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /*
     * Nombre del huésped.
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /*
     * Apellidos del huésped.
     */
    @Column(name = "last_name", nullable = false, length = 150)
    private String lastName;

    /*
     * Correo electrónico del huésped. Debe ser único.
     */
    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    /*
     * Teléfono de contacto del huésped.
     */
    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    /*
     * Documento de identificación del huésped, por ejemplo pasaporte o INE.
     */
    @Column(name = "document_number", length = 100)
    private String documentNumber;

    /*
     * Número de reservas confirmadas asociadas a este huésped.
     */
    @Column(name = "confirmed_bookings_count", nullable = false)
    @Default
    private Integer confirmedBookingsCount = 0;

    /*
     * Lista de reservas asociadas a este huésped.
     */
    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<BookingEntity> bookings = new ArrayList<>();
}
