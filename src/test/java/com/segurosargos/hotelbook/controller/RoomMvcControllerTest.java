package com.segurosargos.hotelbook.controller;

import com.segurosargos.hotelbook.config.SecurityConfig;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomPageResultDto;
import com.segurosargos.hotelbook.dto.RoomSummaryResponseDto;
import com.segurosargos.hotelbook.service.RoomService;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas MVC para RoomViewController y RoomFormController utilizando MockMvc.
 * Se validan nombres de vista, atributos del modelo y flujo de formularios,
 * utilizando la configuracion de seguridad real.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {RoomViewController.class, RoomFormController.class})
@Import(SecurityConfig.class)
class RoomMvcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Test
    @DisplayName("GET /rooms con usuario viewer devuelve la vista de listado con el modelo esperado")
    void showRoomList_withViewer_returnsListViewAndModel() throws Exception {
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

        RoomPageResultDto pageResult = new RoomPageResultDto(
                List.of(room1, room2),
                0,
                10,
                2L,
                1,
                true,
                true,
                "name",
                "asc"
        );

        Mockito.when(roomService.getRoomsPage(0, 10, "name", "asc")).thenReturn(pageResult);

        mockMvc.perform(get("/rooms")
                        .header("Authorization", basicAuth("viewer", "viewer123")))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/list"))
                .andExpect(model().attributeExists("roomPage"))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("pageSize", 10))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("totalElements", 2L))
                .andExpect(model().attribute("sort", "name"))
                .andExpect(model().attribute("direction", "asc"))
                .andExpect(model().attribute("rooms", Matchers.hasSize(2)));
    }

    @Test
    @DisplayName("GET /rooms/{id} con usuario viewer devuelve la vista de detalle")
    void showRoomDetail_withViewer_returnsDetailView() throws Exception {
        RoomDetailResponseDto detail = RoomDetailResponseDto.builder()
                .id(10L)
                .code("R-201")
                .name("Suite")
                .capacity(4)
                .basePricePerNight(new BigDecimal("250.00"))
                .active(true)
                .build();

        Mockito.when(roomService.getRoomById(10L)).thenReturn(detail);

        mockMvc.perform(get("/rooms/{id}", 10L)
                        .header("Authorization", basicAuth("viewer", "viewer123")))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/detail"))
                .andExpect(model().attributeExists("room"))
                .andExpect(model().attribute("room", Matchers.hasProperty("id", Matchers.equalTo(10L))))
                .andExpect(model().attribute("room", Matchers.hasProperty("code", Matchers.equalTo("R-201"))));
    }

    @Test
    @DisplayName("GET /rooms/new con usuario staff devuelve la vista de creacion con roomForm vacio")
    void showCreateForm_withStaff_returnsCreateView() throws Exception {
        mockMvc.perform(get("/rooms/new")
                        .header("Authorization", basicAuth("staff", "staff123")))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/create"))
                .andExpect(model().attributeExists("roomForm"));
    }

    @Test
    @DisplayName("POST /rooms con datos invalidos devuelve la misma vista de creacion con errores")
    void handleCreate_withInvalidForm_returnsCreateViewWithErrors() throws Exception {
        mockMvc.perform(post("/rooms")
                        .with(csrf())
                        .header("Authorization", basicAuth("staff", "staff123"))
                        .param("code", "")
                        .param("name", "")
                        .param("capacity", "0")
                        .param("basePricePerNight", "0")
                        .param("active", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/create"))
                .andExpect(model().attributeHasFieldErrors(
                        "roomForm",
                        "code",
                        "name",
                        "capacity",
                        "basePricePerNight",
                        "active"
                ));
    }

    @Test
    @DisplayName("POST /rooms con datos validos redirige a /rooms")
    void handleCreate_withValidForm_redirectsToRooms() throws Exception {
        mockMvc.perform(post("/rooms")
                        .with(csrf())
                        .header("Authorization", basicAuth("staff", "staff123"))
                        .param("code", "R-300")
                        .param("name", "Family")
                        .param("capacity", "4")
                        .param("basePricePerNight", "180.00")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"));
    }

    @Test
    @DisplayName("GET /rooms/{id}/edit con usuario staff devuelve la vista de edicion con roomForm cargado")
    void showEditForm_withStaff_returnsEditView() throws Exception {
        RoomDetailResponseDto detail = RoomDetailResponseDto.builder()
                .id(5L)
                .code("R-150")
                .name("Business")
                .capacity(2)
                .basePricePerNight(new BigDecimal("200.00"))
                .active(true)
                .build();

        Mockito.when(roomService.getRoomById(5L)).thenReturn(detail);

        mockMvc.perform(get("/rooms/{id}/edit", 5L)
                        .header("Authorization", basicAuth("staff", "staff123")))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/edit"))
                .andExpect(model().attributeExists("roomForm"))
                .andExpect(model().attributeExists("roomId"))
                .andExpect(model().attribute("roomId", 5L));
    }

    @Test
    @DisplayName("POST /rooms/{id}/edit con datos invalidos devuelve la vista de edicion con errores")
    void handleEdit_withInvalidForm_returnsEditViewWithErrors() throws Exception {
        mockMvc.perform(post("/rooms/{id}/edit", 7L)
                        .with(csrf())
                        .header("Authorization", basicAuth("staff", "staff123"))
                        .param("code", "R-700")
                        .param("name", "")
                        .param("capacity", "0")
                        .param("basePricePerNight", "0")
                        .param("active", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/edit"))
                .andExpect(model().attributeHasFieldErrors(
                        "roomForm",
                        "name",
                        "capacity",
                        "basePricePerNight",
                        "active"
                ))
                .andExpect(model().attribute("roomId", 7L));
    }

    @Test
    @DisplayName("POST /rooms/{id}/edit con datos validos redirige a /rooms")
    void handleEdit_withValidForm_redirectsToRooms() throws Exception {
        mockMvc.perform(post("/rooms/{id}/edit", 9L)
                        .with(csrf())
                        .header("Authorization", basicAuth("staff", "staff123"))
                        .param("code", "R-900")
                        .param("name", "Updated name")
                        .param("capacity", "3")
                        .param("basePricePerNight", "160.00")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"));
    }

    private String basicAuth(String username, String password) {
        String token = username + ":" + password;
        byte[] encodedBytes = Base64.getEncoder().encode(token.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes, StandardCharsets.UTF_8);
    }
}
