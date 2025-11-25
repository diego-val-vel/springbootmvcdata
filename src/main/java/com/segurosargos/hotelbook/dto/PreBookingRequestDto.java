package com.segurosargos.hotelbook.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/*
 * DTO de entrada para crear una pre-reserva.
 * Aquí se aplican las validaciones de Bean Validation.
 */
@Data
public class PreBookingRequestDto {

    /*
     * Nombre del huésped que hace la solicitud de pre-reserva.
     */
    @NotBlank(message = "El nombre del huésped es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre del huésped debe tener entre 3 y 100 caracteres.")
    private String guestName;

    /*
     * Tipo de habitación solicitada.
     * En esta práctica usamos valores simbólicos: STANDARD, DOUBLE, SUITE.
     */
    @NotBlank(message = "El tipo de habitación es obligatorio.")
    @Size(min = 3, max = 50, message = "El tipo de habitación debe tener entre 3 y 50 caracteres.")
    private String roomType;

    /*
     * Número de noches de la estancia.
     * Se limita a un rango razonable para el ejemplo.
     */
    @NotNull(message = "El número de noches es obligatorio.")
    @Min(value = 1, message = "El número de noches debe ser al menos 1.")
    private Integer numberOfNights;
}
