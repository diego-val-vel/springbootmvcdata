package com.segurosargos.hotelbook.controller;

import com.segurosargos.hotelbook.config.SecurityConfig;
import com.segurosargos.hotelbook.dto.BookingStatusChangeResponseDto;
import com.segurosargos.hotelbook.exception.BookingNotFoundException;
import com.segurosargos.hotelbook.service.BookingManagementService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas HTTP para BookingManagementController utilizando MockMvc.
 * Se cubren los endpoints de confirmacion y cancelacion de reservas,
 * incluyendo casos exitosos, errores de negocio y seguridad.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookingManagementController.class)
@Import(SecurityConfig.class)
class BookingManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingManagementService bookingManagementService;

    @Test
    @DisplayName("POST /api/bookings/{id}/confirm sin autenticacion devuelve 401")
    void confirmBooking_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(post("/api/bookings/{bookingId}/confirm", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/bookings/{id}/confirm con rol VIEWER devuelve 403")
    void confirmBooking_withViewerRole_returns403() throws Exception {
        mockMvc.perform(post("/api/bookings/{bookingId}/confirm", 1L)
                        .header("Authorization", basicAuth("viewer", "viewer123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/bookings/{id}/confirm con rol STAFF devuelve 200 y respuesta de cambio de estatus")
    void confirmBooking_withStaffRole_returns200AndBody() throws Exception {
        BookingStatusChangeResponseDto response = BookingStatusChangeResponseDto.builder()
                .bookingId(5L)
                .previousStatus("CREATED")
                .newStatus("CONFIRMED")
                .guestId(10L)
                .guestEmail("guest@example.com")
                .guestConfirmedBookingsCount(3)
                .roomId(7L)
                .roomCode("R-101")
                .roomLastBookingDate(LocalDate.of(2025, 3, 1))
                .build();

        Mockito.when(bookingManagementService.confirmBooking(eq(5L))).thenReturn(response);

        mockMvc.perform(post("/api/bookings/{bookingId}/confirm", 5L)
                        .header("Authorization", basicAuth("staff", "staff123"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId", is(5)))
                .andExpect(jsonPath("$.previousStatus", is("CREATED")))
                .andExpect(jsonPath("$.newStatus", is("CONFIRMED")))
                .andExpect(jsonPath("$.guestId", is(10)))
                .andExpect(jsonPath("$.roomId", is(7)))
                .andExpect(jsonPath("$.roomCode", is("R-101")));
    }

    @Test
    @DisplayName("POST /api/bookings/{id}/confirm devuelve 404 cuando la reserva no existe")
    void confirmBooking_whenBookingDoesNotExist_returns404() throws Exception {
        Mockito.when(bookingManagementService.confirmBooking(eq(99L)))
                .thenThrow(new BookingNotFoundException("Reserva no encontrada"));

        mockMvc.perform(post("/api/bookings/{bookingId}/confirm", 99L)
                        .header("Authorization", basicAuth("staff", "staff123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    @DisplayName("POST /api/bookings/{id}/cancel con rol ADMIN devuelve 200 y respuesta de cambio de estatus")
    void cancelBooking_withAdminRole_returns200AndBody() throws Exception {
        BookingStatusChangeResponseDto response = BookingStatusChangeResponseDto.builder()
                .bookingId(8L)
                .previousStatus("CONFIRMED")
                .newStatus("CANCELLED")
                .guestId(20L)
                .guestEmail("guest2@example.com")
                .guestConfirmedBookingsCount(1)
                .roomId(15L)
                .roomCode("R-202")
                .roomLastBookingDate(LocalDate.of(2025, 4, 10))
                .build();

        Mockito.when(bookingManagementService.cancelBooking(eq(8L))).thenReturn(response);

        mockMvc.perform(post("/api/bookings/{bookingId}/cancel", 8L)
                        .header("Authorization", basicAuth("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId", is(8)))
                .andExpect(jsonPath("$.previousStatus", is("CONFIRMED")))
                .andExpect(jsonPath("$.newStatus", is("CANCELLED")))
                .andExpect(jsonPath("$.guestId", is(20)))
                .andExpect(jsonPath("$.roomId", is(15)))
                .andExpect(jsonPath("$.roomCode", is("R-202")));
    }

    @Test
    @DisplayName("POST /api/bookings/{id}/cancel devuelve 404 cuando la reserva no existe")
    void cancelBooking_whenBookingDoesNotExist_returns404() throws Exception {
        Mockito.when(bookingManagementService.cancelBooking(eq(123L)))
                .thenThrow(new BookingNotFoundException("Reserva no encontrada"));

        mockMvc.perform(post("/api/bookings/{bookingId}/cancel", 123L)
                        .header("Authorization", basicAuth("admin", "admin123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    private String basicAuth(String username, String password) {
        String token = username + ":" + password;
        byte[] encodedBytes = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes, StandardCharsets.UTF_8);
    }
}
