package com.segurosargos.hotelbook.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

/*
 * Representa estadísticas agregadas de reservas por año y mes.
 * Incluye el número de reservas y el importe total asociado.
 */
@Data
@Builder
public class MonthlyBookingStatsDto {

    /*
     * Año de referencia de la estadística.
     */
    private Integer year;

    /*
     * Número de mes (1-12) de la estadística.
     */
    private Integer month;

    /*
     * Número total de reservas en el mes.
     */
    private Long bookingCount;

    /*
     * Importe total de las reservas en el mes.
     */
    private BigDecimal totalRevenue;
}
