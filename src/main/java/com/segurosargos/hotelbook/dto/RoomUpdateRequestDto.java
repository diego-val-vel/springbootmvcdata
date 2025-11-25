package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/*
 * Datos de entrada para actualizar una habitación existente.
 * Se utiliza en el endpoint PUT /api/rooms/{id}.
 * El código se mantiene inmutable y no se expone en este DTO.
 */
@Data
@Builder
public class RoomUpdateRequestDto {

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

    /*
     * Indicador de si la habitación está activa en el catálogo.
     */
    @NotNull(message = "El estado activo es obligatorio.")
    private Boolean active;
}
