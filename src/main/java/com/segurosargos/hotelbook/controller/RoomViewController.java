package com.segurosargos.hotelbook.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomPageResultDto;
import com.segurosargos.hotelbook.dto.RoomSummaryResponseDto;
import com.segurosargos.hotelbook.service.RoomService;

/*
 * Controlador MVC para mostrar vistas relacionadas con habitaciones.
 * Utiliza RoomService para recuperar los datos y delega el renderizado a plantillas.
 */
@Controller
@RequestMapping("/rooms")
public class RoomViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomViewController.class);

    private final RoomService roomService;

    public RoomViewController(RoomService roomService) {
        this.roomService = roomService;
    }

    /*
     * Muestra el listado de habitaciones utilizando la vista rooms/list,
     * con soporte para paginación y ordenación básica.
     */
    @GetMapping
    public String showRoomList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "name") String sort,
            @RequestParam(name = "dir", defaultValue = "asc") String direction,
            Model model) {

        LOGGER.info(
                "Renderizando vista de listado de habitaciones. page={}, size={}, sort={}, dir={}.",
                page, size, sort, direction);

        RoomPageResultDto roomPage = roomService.getRoomsPage(page, size, sort, direction);

        model.addAttribute("roomPage", roomPage);

        List<RoomSummaryResponseDto> rooms = roomPage.getRooms();
        model.addAttribute("rooms", rooms);

        model.addAttribute("currentPage", roomPage.getPageNumber());
        model.addAttribute("pageSize", roomPage.getPageSize());
        model.addAttribute("totalPages", roomPage.getTotalPages());
        model.addAttribute("totalElements", roomPage.getTotalElements());
        model.addAttribute("sort", roomPage.getSort());
        model.addAttribute("direction", roomPage.getDirection());

        return "rooms/list";
    }

    /*
     * Muestra el detalle de una habitación específica utilizando la vista rooms/detail.
     */
    @GetMapping("/{id}")
    public String showRoomDetail(@PathVariable("id") Long id, Model model) {
        LOGGER.info("Renderizando vista de detalle para la habitación con id {}.", id);
        RoomDetailResponseDto room = roomService.getRoomById(id);
        model.addAttribute("room", room);
        return "rooms/detail";
    }
}
