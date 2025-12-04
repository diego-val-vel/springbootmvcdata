package com.segurosargos.hotelbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.segurosargos.hotelbook.config.SecurityConfig;
import com.segurosargos.hotelbook.dto.RoomCreateRequestDto;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomSummaryResponseDto;
import com.segurosargos.hotelbook.dto.RoomUpdateRequestDto;
import com.segurosargos.hotelbook.exception.BookingNotFoundException;
import com.segurosargos.hotelbook.service.RoomService;
import jakarta.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.hamcrest.Matchers;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas HTTP para RoomRestV1Controller utilizando MockMvc.
 * Se valida el comportamiento de la API REST versionada bajo /api/v1/rooms,
 * incluyendo codigos de estado, cuerpo JSON, validacion y errores.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = RoomRestV1Controller.class)
@Import(SecurityConfig.class)
class RoomRestV1ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/rooms sin autenticacion devuelve 401")
    void getAllRooms_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/rooms con usuario viewer devuelve 200 y lista de habitaciones")
    void getAllRooms_withViewer_returns200AndList() throws Exception {
        RoomSummaryResponseDto room1 = RoomSummaryResponseDto.builder()
                .id(1L)
                .code("R-101")
                .name("Standard")
                .capacity(2)
                .basePricePerNight(new BigDecimal("100.00"))
                .active(true)
                .build();

        RoomSummaryResponseDto room2 = RoomSummaryResponseDto.builder()
                .id(2L)
                .code("R-102")
                .name("Deluxe")
                .capacity(3)
                .basePricePerNight(new BigDecimal("150.00"))
                .active(true)
                .build();

        Mockito.when(roomService.getAllRooms()).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/api/v1/rooms")
                        .header("Authorization", basicAuth("viewer", "viewer123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].code", is("R-101")))
                .andExpect(jsonPath("$[0].name", is("Standard")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].code", is("R-102")))
                .andExpect(jsonPath("$[1].name", is("Deluxe")));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/{id} devuelve 200 cuando la habitacion existe")
    void getRoomById_whenRoomExists_returns200() throws Exception {
        RoomDetailResponseDto detail = RoomDetailResponseDto.builder()
                .id(10L)
                .code("R-201")
                .name("Suite")
                .capacity(4)
                .basePricePerNight(new BigDecimal("250.00"))
                .active(true)
                .build();

        Mockito.when(roomService.getRoomById(10L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/rooms/{id}", 10L)
                        .header("Authorization", basicAuth("viewer", "viewer123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.code", is("R-201")))
                .andExpect(jsonPath("$.name", is("Suite")))
                .andExpect(jsonPath("$.capacity", is(4)));
    }

    @Test
    @DisplayName("GET /api/v1/rooms/{id} devuelve 404 cuando la habitacion no existe")
    void getRoomById_whenRoomDoesNotExist_returns404() throws Exception {
        Mockito.when(roomService.getRoomById(99L))
                .thenThrow(new BookingNotFoundException("No se encontró la habitación con id 99"));

        mockMvc.perform(get("/api/v1/rooms/{id}", 99L)
                        .header("Authorization", basicAuth("viewer", "viewer123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }

    @Test
    @DisplayName("POST /api/v1/rooms con datos validos devuelve 201 y Location")
    void createRoom_whenRequestIsValid_returns201() throws Exception {
        RoomCreateRequestDto request = RoomCreateRequestDto.builder()
                .code("R-300")
                .name("Family")
                .capacity(4)
                .basePricePerNight(new BigDecimal("180.00"))
                .build();

        RoomDetailResponseDto created = RoomDetailResponseDto.builder()
                .id(30L)
                .code("R-300")
                .name("Family")
                .capacity(4)
                .basePricePerNight(new BigDecimal("180.00"))
                .active(true)
                .build();

        Mockito.when(roomService.createRoom(any(RoomCreateRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", basicAuth("staff", "staff123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.endsWith("/api/v1/rooms/30")))
                .andExpect(jsonPath("$.id", is(30)))
                .andExpect(jsonPath("$.code", is("R-300")))
                .andExpect(jsonPath("$.name", is("Family")))
                .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("POST /api/v1/rooms con datos invalidos devuelve 400 y errores de validacion")
    void createRoom_whenRequestIsInvalid_returns400() throws Exception {
        RoomCreateRequestDto invalidRequest = RoomCreateRequestDto.builder()
                .code(null)
                .name("")
                .capacity(null)
                .basePricePerNight(null)
                .build();

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", basicAuth("staff", "staff123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    @Test
    @DisplayName("PUT /api/v1/rooms/{id} con conflicto de concurrencia devuelve 412")
    void updateRoom_whenOptimisticLockingOccurs_returns412() throws Exception {
        RoomDetailResponseDto current = RoomDetailResponseDto.builder()
                .id(40L)
                .code("R-400")
                .name("Business")
                .capacity(2)
                .basePricePerNight(new BigDecimal("200.00"))
                .active(true)
                .build();

        Mockito.when(roomService.getRoomById(40L)).thenReturn(current);

        Mockito.when(roomService.updateRoom(eq(40L), any(RoomUpdateRequestDto.class)))
                .thenThrow(new OptimisticLockException("Simulated concurrent update"));

        String requestBody = """
                {
                  "name": "Business Updated",
                  "capacity": 2,
                  "basePricePerNight": 210.00,
                  "active": true
                }
                """;

        mockMvc.perform(put("/api/v1/rooms/{id}", 40L)
                        .header("Authorization", basicAuth("staff", "staff123"))
                        .header("If-Match", "room-40-v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isPreconditionFailed())
                .andExpect(header().string("ETag", "\"room-40-v0\""));
    }

    @Test
    @DisplayName("POST /api/v1/rooms con usuario viewer devuelve 403 por falta de rol")
    void createRoom_withViewerRole_returns403() throws Exception {
        RoomCreateRequestDto request = RoomCreateRequestDto.builder()
                .code("R-999")
                .name("Blocked")
                .capacity(2)
                .basePricePerNight(new BigDecimal("99.00"))
                .build();

        mockMvc.perform(post("/api/v1/rooms")
                        .header("Authorization", basicAuth("viewer", "viewer123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    private String basicAuth(String username, String password) {
        String token = username + ":" + password;
        byte[] encodedBytes = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes, StandardCharsets.UTF_8);
    }
}
