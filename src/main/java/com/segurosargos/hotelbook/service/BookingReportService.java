package com.segurosargos.hotelbook.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.segurosargos.hotelbook.dto.BookingDetailResponseDto;
import com.segurosargos.hotelbook.dto.MonthlyBookingStatsDto;
import com.segurosargos.hotelbook.repository.BookingDetailView;
import com.segurosargos.hotelbook.repository.BookingJpaRepository;
import com.segurosargos.hotelbook.repository.MonthlyBookingStatsView;

/*
 * Servicio que encapsula consultas de reporte sobre reservas.
 * Combina datos de la reserva, la habitación y el huésped.
 */
@Service
public class BookingReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingReportService.class);

    /*
     * Estatus por defecto para filtrar reservas cuando no se indica explícitamente.
     */
    private static final String DEFAULT_STATUS = "CONFIRMED";

    private final BookingJpaRepository bookingJpaRepository;

    public BookingReportService(BookingJpaRepository bookingJpaRepository) {
        this.bookingJpaRepository = bookingJpaRepository;
    }

    /*
     * Recupera los detalles de reserva en el rango de fechas indicado.
     * Si no se indica estatus, se utiliza un estatus por defecto.
     */
    public List<BookingDetailResponseDto> getBookingDetails(LocalDate startDate,
                                                            LocalDate endDate,
                                                            String status) {

        LOGGER.info(
                "Recuperando detalles de reservas. startDate={}, endDate={}, status={}.",
                startDate, endDate, status);

        validateDateRange(startDate, endDate);

        String effectiveStatus = status != null && !status.isBlank() ? status : DEFAULT_STATUS;

        List<BookingDetailView> views = bookingJpaRepository
                .findBookingDetailsBetweenDatesAndStatus(startDate, endDate, effectiveStatus);

        LOGGER.info("Se recuperaron {} reservas para el reporte de detalle.", views.size());

        return views.stream()
                .map(this::mapToBookingDetailResponseDto)
                .collect(Collectors.toList());
    }

    /*
     * Recupera estadísticas mensuales de reservas para el rango de fechas indicado.
     * Si no se indica estatus, se utiliza un estatus por defecto.
     */
    public List<MonthlyBookingStatsDto> getMonthlyStats(LocalDate startDate,
                                                        LocalDate endDate,
                                                        String status) {

        LOGGER.info(
                "Recuperando estadísticas mensuales de reservas. startDate={}, endDate={}, status={}.",
                startDate, endDate, status);

        validateDateRange(startDate, endDate);

        String effectiveStatus = status != null && !status.isBlank() ? status : DEFAULT_STATUS;

        List<MonthlyBookingStatsView> views = bookingJpaRepository
                .findMonthlyStatsBetweenDatesAndStatus(startDate, endDate, effectiveStatus);

        LOGGER.info("Se recuperaron {} filas de estadísticas mensuales.", views.size());

        return views.stream()
                .map(this::mapToMonthlyBookingStatsDto)
                .collect(Collectors.toList());
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "Las fechas de inicio y fin son obligatorias para consultar reservas.");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }

    private BookingDetailResponseDto mapToBookingDetailResponseDto(BookingDetailView view) {
        return BookingDetailResponseDto.builder()
                .bookingId(view.getBookingId())
                .roomCode(view.getRoomCode())
                .roomName(view.getRoomName())
                .guestFirstName(view.getGuestFirstName())
                .guestLastName(view.getGuestLastName())
                .guestEmail(view.getGuestEmail())
                .checkInDate(view.getCheckInDate())
                .checkOutDate(view.getCheckOutDate())
                .totalPrice(view.getTotalPrice())
                .status(view.getStatus())
                .build();
    }

    private MonthlyBookingStatsDto mapToMonthlyBookingStatsDto(MonthlyBookingStatsView view) {
        Integer year = view.getYear();
        Integer month = view.getMonth();
        Long bookingCount = view.getBookingCount();

        if (bookingCount == null) {
            bookingCount = 0L;
        }

        return MonthlyBookingStatsDto.builder()
                .year(year)
                .month(month)
                .bookingCount(bookingCount)
                .totalRevenue(view.getTotalRevenue())
                .build();
    }
}
