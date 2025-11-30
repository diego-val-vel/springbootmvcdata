package com.segurosargos.hotelbook.repository;

import java.math.BigDecimal;

/*
 * Proyección de solo lectura que representa estadísticas agregadas
 * de reservas por año y mes.
 */
public interface MonthlyBookingStatsView {

    /*
     * Año de referencia de la estadística.
     */
    Integer getYear();

    /*
     * Número de mes (1-12) de la estadística.
     */
    Integer getMonth();

    /*
     * Número total de reservas en el mes.
     */
    Long getBookingCount();

    /*
     * Importe total de las reservas en el mes.
     */
    BigDecimal getTotalRevenue();
}
