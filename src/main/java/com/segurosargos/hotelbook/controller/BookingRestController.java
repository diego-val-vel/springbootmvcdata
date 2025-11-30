package com.segurosargos.hotelbook.controller;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.segurosargos.hotelbook.dto.BookingDetailResponseDto;
import com.segurosargos.hotelbook.dto.MonthlyBookingStatsDto;
import com.segurosargos.hotelbook.service.BookingReportService;

/*
 * Controlador REST para exponer reportes de reservas.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingRestController.class);

    private final BookingReportService bookingReportService;

    public BookingRestController(BookingReportService bookingReportService) {
        this.bookingReportService = bookingReportService;
    }

    /*
     * Recupera un reporte detallado de reservas para el rango de fechas indicado.
     * De forma opcional, se puede indicar un estatus para filtrar (por ejemplo, CONFIRMED).
     */
    @GetMapping("/report")
    public ResponseEntity<List<BookingDetailResponseDto>> getBookingDetailsReport(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam(name = "status", required = false) String status) {

        LOGGER.info(
                "Recibida solicitud de reporte de reservas. startDate={}, endDate={}, status={}.",
                startDate, endDate, status);

        List<BookingDetailResponseDto> details =
                bookingReportService.getBookingDetails(startDate, endDate, status);

        LOGGER.info(
                "Solicitud de reporte de reservas completada. Total de registros: {}.",
                details.size());

        return ResponseEntity.ok(details);
    }

    /*
     * Recupera estadísticas mensuales de reservas para el rango de fechas indicado.
     * De forma opcional, se puede indicar un estatus para filtrar (por ejemplo, CONFIRMED).
     */
    @GetMapping("/monthly-stats")
    public ResponseEntity<List<MonthlyBookingStatsDto>> getMonthlyStats(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam(name = "status", required = false) String status) {

        LOGGER.info(
                "Recibida solicitud de estadísticas mensuales de reservas. startDate={}, endDate={}, status={}.",
                startDate, endDate, status);

        List<MonthlyBookingStatsDto> stats =
                bookingReportService.getMonthlyStats(startDate, endDate, status);

        LOGGER.info(
                "Solicitud de estadísticas mensuales completada. Total de filas: {}.",
                stats.size());

        return ResponseEntity.ok(stats);
    }
}
