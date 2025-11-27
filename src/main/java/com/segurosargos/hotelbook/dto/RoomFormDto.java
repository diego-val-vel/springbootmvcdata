package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para formularios MVC de alta y edición de habitaciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomFormDto {

    @NotBlank
    @Size(max = 20)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer capacity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal basePricePerNight;

    @NotNull
    private Boolean active;
}
