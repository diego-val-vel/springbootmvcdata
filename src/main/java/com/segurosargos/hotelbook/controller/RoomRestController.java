package com.segurosargos.hotelbook.controller;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.segurosargos.hotelbook.dto.RoomCreateRequestDto;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomPageResultDto;
import com.segurosargos.hotelbook.dto.RoomSummaryResponseDto;
import com.segurosargos.hotelbook.dto.RoomUpdateRequestDto;
import com.segurosargos.hotelbook.service.RoomService;

/*
 * Controlador REST para la gestión de habitaciones.
 * Expone operaciones CRUD y búsqueda sobre el recurso Room.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomRestController.class);

    private final RoomService roomService;

    public RoomRestController(RoomService roomService) {
        this.roomService = roomService;
    }

    /*
     * Recupera el listado de habitaciones en formato resumido.
     */
    @GetMapping
    public ResponseEntity<List<RoomSummaryResponseDto>> getAllRooms() {
        LOGGER.info("Recibida solicitud para obtener todas las habitaciones.");
        List<RoomSummaryResponseDto> rooms = roomService.getAllRooms();
        LOGGER.info("Solicitud para obtener todas las habitaciones completada. Total: {}.", rooms.size());
        return ResponseEntity.ok(rooms);
    }

    /*
     * Recupera una página de habitaciones aplicando filtros opcionales por nombre y rango de precio.
     * Si no se especifica ningún filtro, utiliza el listado paginado estándar.
     */
    @GetMapping("/search")
    public ResponseEntity<RoomPageResultDto> searchRooms(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sort", required = false, defaultValue = "id") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction) {

        LOGGER.info(
                "Recibida solicitud de búsqueda de habitaciones. name={}, minPrice={}, maxPrice={}, page={}, size={}, sort={}, direction={}.",
                name, minPrice, maxPrice, page, size, sort, direction);

        RoomPageResultDto pageResult = roomService.searchRooms(
                name,
                minPrice,
                maxPrice,
                page,
                size,
                sort,
                direction
        );

        LOGGER.info("Solicitud de búsqueda de habitaciones completada. totalElements={}.",
                pageResult.getTotalElements());

        return ResponseEntity.ok(pageResult);
    }

    /*
     * Recupera el detalle de una habitación específica.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomDetailResponseDto> getRoomById(@PathVariable("id") Long id) {
        LOGGER.info("Recibida solicitud para obtener la habitación con id {}.", id);
        RoomDetailResponseDto responseDto = roomService.getRoomById(id);
        LOGGER.info("Solicitud para obtener la habitación con id {} completada.", id);
        return ResponseEntity.ok(responseDto);
    }

    /*
     * Crea una nueva habitación a partir de los datos de entrada.
     */
    @PostMapping
    public ResponseEntity<RoomDetailResponseDto> createRoom(
            @Valid @RequestBody RoomCreateRequestDto requestDto) {
        LOGGER.info("Recibida solicitud para crear una nueva habitación con código {}.", requestDto.getCode());
        RoomDetailResponseDto responseDto = roomService.createRoom(requestDto);
        LOGGER.info("Habitación creada con id {} y código {}.",
                responseDto.getId(), responseDto.getCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /*
     * Actualiza una habitación existente identificada por su id.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RoomDetailResponseDto> updateRoom(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoomUpdateRequestDto requestDto) {
        LOGGER.info("Recibida solicitud para actualizar la habitación con id {}.", id);
        RoomDetailResponseDto responseDto = roomService.updateRoom(id, requestDto);
        LOGGER.info("Habitación actualizada con id {}.", responseDto.getId());
        return ResponseEntity.ok(responseDto);
    }

    /*
     * Elimina una habitación identificada por su id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id) {
        LOGGER.info("Recibida solicitud para eliminar la habitación con id {}.", id);
        roomService.deleteRoom(id);
        LOGGER.info("Habitación eliminada con id {}.", id);
        return ResponseEntity.noContent().build();
    }
}
