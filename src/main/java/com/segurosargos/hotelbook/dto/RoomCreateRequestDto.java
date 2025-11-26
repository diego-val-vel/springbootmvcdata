package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Datos de entrada para crear una habitación.
 * Se utiliza en el endpoint POST /api/rooms.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreateRequestDto {

    /*
     * Código único de la habitación, por ejemplo "STD-101".
     */
    @NotBlank(message = "El código de la habitación es obligatorio.")
    @Size(max = 20, message = "El código de la habitación no debe exceder 20 caracteres.")
    private String code;

    /*
     * Nombre descriptivo de la habitación.
     */
    @NotBlank(message = "El nombre de la habitación es obligatorio.")
    @Size(max = 100, message = "El nombre de la habitación no debe exceder 100 caracteres.")
    private String name;

    /*
     * Capacidad máxima de huéspedes.
     */
    @NotNull(message = "La capacidad es obligatoria.")
    @Min(value = 1, message = "La capacidad debe ser al menos 1.")
    private Integer capacity;

    /*
     * Precio base por noche.
     */
    @NotNull(message = "El precio base por noche es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio base por noche debe ser mayor que 0.")
    @Digits(integer = 10, fraction = 2, message = "El precio base por noche debe tener hasta 10 dígitos enteros y 2 decimales.")
    private BigDecimal basePricePerNight;
}
