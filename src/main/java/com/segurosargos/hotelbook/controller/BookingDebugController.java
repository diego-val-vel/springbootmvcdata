package com.segurosargos.hotelbook.controller;

import java.util.List;
import com.segurosargos.hotelbook.dto.BookingSummaryDebugDto;
import com.segurosargos.hotelbook.service.BookingDebugService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Controlador REST de apoyo para explorar el comportamiento de las reservas
 * en términos de carga Lazy/Eager y consultas emitidas por Hibernate.
 */
@RestController
@RequestMapping("/api/debug/bookings")
@RequiredArgsConstructor
public class BookingDebugController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingDebugController.class);

    private final BookingDebugService bookingDebugService;

    /*
     * Variante inicial basada en findAll() sin optimizaciones explícitas. Este
     * endpoint permite observar el patrón N+1 al revisar las consultas SQL en
     * los logs cuando se acceden a las asociaciones room y guest.
     */
    @GetMapping("/nplus1-naive")
    public ResponseEntity<List<BookingSummaryDebugDto>> getAllBookingsNaive() {
        LOGGER.info("Invocando endpoint /api/debug/bookings/nplus1-naive.");
        List<BookingSummaryDebugDto> bookings = bookingDebugService.getAllBookingsWithAssociationsNaive();
        LOGGER.info("El endpoint /api/debug/bookings/nplus1-naive devolverá {} elementos.", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    /*
     * Endpoint equivalente a la variante naive expuesta originalmente como
     * /api/debug/bookings/lazy-basic, conservado para compatibilidad con las
     * invocaciones previas.
     */
    @GetMapping("/lazy-basic")
    public ResponseEntity<List<BookingSummaryDebugDto>> getAllBookingsLazyBasic() {
        LOGGER.info("Invocando endpoint /api/debug/bookings/lazy-basic (alias de nplus1-naive).");
        List<BookingSummaryDebugDto> bookings = bookingDebugService.getAllBookingsWithAssociationsNaive();
        LOGGER.info("El endpoint /api/debug/bookings/lazy-basic devolverá {} elementos.", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    /*
     * Variante optimizada que utiliza EntityGraph en el repositorio. Permite
     * comparar las consultas SQL generadas frente a la variante naive y
     * observar cómo se reduce el número total de consultas al inicializar
     * room y guest en una sola consulta.
     */
    @GetMapping("/entitygraph")
    public ResponseEntity<List<BookingSummaryDebugDto>> getAllBookingsUsingEntityGraph() {
        LOGGER.info("Invocando endpoint /api/debug/bookings/entitygraph.");
        List<BookingSummaryDebugDto> bookings = bookingDebugService.getAllBookingsWithAssociationsUsingEntityGraph();
        LOGGER.info("El endpoint /api/debug/bookings/entitygraph devolverá {} elementos.", bookings.size());
        return ResponseEntity.ok(bookings);
    }

    /*
     * Variante optimizada que utiliza una consulta JPQL con JOIN FETCH. Esta
     * versión también reduce el problema N+1 al traer toda la información de
     * room y guest en la consulta principal, permitiendo comparar su
     * comportamiento con la variante basada en EntityGraph.
     */
    @GetMapping("/join-fetch")
    public ResponseEntity<List<BookingSummaryDebugDto>> getAllBookingsUsingJoinFetch() {
        LOGGER.info("Invocando endpoint /api/debug/bookings/join-fetch.");
        List<BookingSummaryDebugDto> bookings = bookingDebugService.getAllBookingsWithAssociationsUsingFetchJoin();
        LOGGER.info("El endpoint /api/debug/bookings/join-fetch devolverá {} elementos.", bookings.size());
        return ResponseEntity.ok(bookings);
    }
}
