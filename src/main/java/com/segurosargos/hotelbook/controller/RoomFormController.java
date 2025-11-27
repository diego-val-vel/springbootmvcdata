package com.segurosargos.hotelbook.controller;

import com.segurosargos.hotelbook.dto.RoomCreateRequestDto;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomFormDto;
import com.segurosargos.hotelbook.dto.RoomUpdateRequestDto;
import com.segurosargos.hotelbook.service.RoomService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador MVC para formularios de alta y edición de habitaciones.
 */
@Controller
public class RoomFormController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomFormController.class);

    private final RoomService roomService;

    public RoomFormController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Muestra el formulario para crear una nueva habitación.
     */
    @GetMapping("/rooms/new")
    public String showCreateForm(Model model) {
        LOGGER.info("Mostrando formulario de creación de habitación.");
        if (!model.containsAttribute("roomForm")) {
            model.addAttribute("roomForm", new RoomFormDto());
        }
        return "rooms/create";
    }

    /**
     * Procesa el envío del formulario de creación de habitación.
     */
    @PostMapping("/rooms")
    public String handleCreate(
            @Valid @ModelAttribute("roomForm") RoomFormDto roomFormDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        LOGGER.info("Procesando creación de habitación desde formulario MVC.");

        if (bindingResult.hasErrors()) {
            LOGGER.info("Se encontraron errores de validación en el formulario de creación de habitación.");
            model.addAttribute("roomForm", roomFormDto);
            return "rooms/create";
        }

        RoomCreateRequestDto requestDto = new RoomCreateRequestDto();
        requestDto.setCode(roomFormDto.getCode());
        requestDto.setName(roomFormDto.getName());
        requestDto.setCapacity(roomFormDto.getCapacity());
        requestDto.setBasePricePerNight(roomFormDto.getBasePricePerNight());

        roomService.createRoom(requestDto);

        redirectAttributes.addFlashAttribute("roomCreated", Boolean.TRUE);
        return "redirect:/rooms";
    }

    /**
     * Muestra el formulario para editar una habitación existente.
     */
    @GetMapping("/rooms/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        LOGGER.info("Mostrando formulario de edición para la habitación con id {}.", id);

        RoomDetailResponseDto roomDetail = roomService.getRoomById(id);

        if (!model.containsAttribute("roomForm")) {
            RoomFormDto roomFormDto = new RoomFormDto(
                    roomDetail.getCode(),
                    roomDetail.getName(),
                    roomDetail.getCapacity(),
                    roomDetail.getBasePricePerNight(),
                    roomDetail.isActive()
            );
            model.addAttribute("roomForm", roomFormDto);
        }

        model.addAttribute("roomId", id);

        return "rooms/edit";
    }

    /**
     * Procesa el envío del formulario de edición de habitación.
     */
    @PostMapping("/rooms/{id}/edit")
    public String handleEdit(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("roomForm") RoomFormDto roomFormDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        LOGGER.info("Procesando edición de habitación con id {} desde formulario MVC.", id);

        if (bindingResult.hasErrors()) {
            LOGGER.info("Se encontraron errores de validación en el formulario de edición de habitación.");
            model.addAttribute("roomId", id);
            return "rooms/edit";
        }

        RoomUpdateRequestDto requestDto = new RoomUpdateRequestDto();
        requestDto.setName(roomFormDto.getName());
        requestDto.setCapacity(roomFormDto.getCapacity());
        requestDto.setBasePricePerNight(roomFormDto.getBasePricePerNight());
        requestDto.setActive(roomFormDto.getActive());

        roomService.updateRoom(id, requestDto);

        redirectAttributes.addFlashAttribute("roomUpdated", Boolean.TRUE);
        return "redirect:/rooms";
    }
}
