package com.segurosargos.hotelbook.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.segurosargos.hotelbook.dto.RoomCreateRequestDto;
import com.segurosargos.hotelbook.dto.RoomDetailResponseDto;
import com.segurosargos.hotelbook.dto.RoomOccupancySummaryDto;
import com.segurosargos.hotelbook.dto.RoomPageResultDto;
import com.segurosargos.hotelbook.dto.RoomSearchFilterDto;
import com.segurosargos.hotelbook.dto.RoomSummaryResponseDto;
import com.segurosargos.hotelbook.dto.RoomUpdateRequestDto;
import com.segurosargos.hotelbook.service.RoomService;

/**
 * Version v1 de la API REST para la gestion de habitaciones.
 * Expone operaciones CRUD, busquedas y resumenes sobre el recurso Room
 * bajo el prefijo /api/v1/rooms siguiendo buenas practicas REST.
 */
@RestController
@RequestMapping("/api/v1/rooms")
public class RoomRestV1Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomRestV1Controller.class);

    private final RoomService roomService;

    public RoomRestV1Controller(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Recupera el listado completo de habitaciones en formato resumido.
     */
    @GetMapping
    public ResponseEntity<List<RoomSummaryResponseDto>> getAllRooms() {
        LOGGER.info("V1 - Recibida solicitud para obtener todas las habitaciones.");
        List<RoomSummaryResponseDto> rooms = roomService.getAllRooms();
        LOGGER.info("V1 - Solicitud para obtener todas las habitaciones completada. Total: {}.", rooms.size());
        return ResponseEntity.ok(rooms);
    }

    /**
     * Recupera una pagina de habitaciones aplicando filtros opcionales por nombre y rango de precio.
     * Si no se especifica ningun filtro, utiliza el listado paginado estandar.
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
                "V1 - Recibida solicitud de busqueda de habitaciones. name={}, minPrice={}, maxPrice={}, page={}, size={}, sort={}, direction={}.",
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

        LOGGER.info("V1 - Solicitud de busqueda de habitaciones completada. totalElements={}.",
                pageResult.getTotalElements());

        return ResponseEntity.ok(pageResult);
    }

    /**
     * Recupera una pagina de habitaciones aplicando un conjunto avanzado de filtros
     * basado en Specifications. El filtro se recibe en el cuerpo de la peticion
     * y los parametros de paginacion y orden se reciben como query parameters.
     */
    @PostMapping("/search-advanced")
    public ResponseEntity<RoomPageResultDto> searchRoomsAdvanced(
            @RequestBody(required = false) RoomSearchFilterDto filter,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "sort", required = false, defaultValue = "id") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction) {

        LOGGER.info(
                "V1 - Recibida solicitud de busqueda avanzada de habitaciones. filter={}, page={}, size={}, sort={}, direction={}.",
                filter, page, size, sort, direction);

        RoomPageResultDto pageResult = roomService.searchRoomsAdvanced(
                filter,
                page,
                size,
                sort,
                direction
        );

        LOGGER.info("V1 - Solicitud de busqueda avanzada completada. totalElements={}.",
                pageResult.getTotalElements());

        return ResponseEntity.ok(pageResult);
    }

    /**
     * Recupera el resumen de ocupacion de las habitaciones para una fecha de referencia.
     * Si no se indica la fecha, se utiliza la fecha actual.
     */
    @GetMapping("/occupancy")
    public ResponseEntity<List<RoomOccupancySummaryDto>> getRoomOccupancySummary(
            @RequestParam(name = "date", required = false) LocalDate referenceDate) {

        LOGGER.info("V1 - Recibida solicitud para obtener el resumen de ocupacion de habitaciones para la fecha {}.",
                referenceDate);

        List<RoomOccupancySummaryDto> summaries = roomService.getRoomOccupancySummary(referenceDate);

        LOGGER.info("V1 - Solicitud de resumen de ocupacion completada. Total de registros: {}.", summaries.size());

        return ResponseEntity.ok(summaries);
    }

    /**
     * Recupera el detalle de una habitacion especifica.
     * Si el cliente envia el encabezado If-None-Match con un ETag igual a la version
     * actual del recurso, se responde 304 Not Modified sin cuerpo.
     * En caso contrario, se devuelve 200 OK con el detalle y el ETag actualizado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomDetailResponseDto> getRoomById(
            @PathVariable("id") Long id,
            @RequestHeader(name = "If-None-Match", required = false) String ifNoneMatch) {

        LOGGER.info("V1 - Recibida solicitud para obtener la habitacion con id {}.", id);

        RoomDetailResponseDto responseDto = roomService.getRoomById(id);
        Integer version = roomService.getRoomVersionById(id);

        String eTag = buildRoomEtag(id, version);

        if (ifNoneMatch != null && ifNoneMatch.equals(eTag)) {
            LOGGER.info("V1 - ETag coincidente para la habitacion con id {}. Respondiendo 304 Not Modified.", id);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(eTag)
                    .build();
        }

        LOGGER.info("V1 - Solicitud para obtener la habitacion con id {} completada. ETag={}.", id, eTag);

        return ResponseEntity.ok()
                .eTag(eTag)
                .body(responseDto);
    }

    /**
     * Crea una nueva habitacion a partir de los datos de entrada.
     * Devuelve 201 Created e incluye la cabecera Location apuntando al recurso creado.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<RoomDetailResponseDto> createRoom(
            @Valid @RequestBody RoomCreateRequestDto requestDto) {

        LOGGER.info("V1 - Recibida solicitud para crear una nueva habitacion con codigo {}.", requestDto.getCode());

        RoomDetailResponseDto responseDto = roomService.createRoom(requestDto);

        LOGGER.info("V1 - Habitacion creada con id {} y codigo {}.",
                responseDto.getId(), responseDto.getCode());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    /**
     * Actualiza una habitacion existente identificada por su id.
     * Si el cliente envia el encabezado If-Match y el ETag no coincide con la
     * version actual, se responde 412 Precondition Failed.
     * Si no se envia If-Match, se aplica un update "best effort" y se devuelve
     * el nuevo ETag en la respuesta.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public ResponseEntity<RoomDetailResponseDto> updateRoom(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoomUpdateRequestDto requestDto,
            @RequestHeader(name = "If-Match", required = false) String ifMatch) {

        LOGGER.info("V1 - Recibida solicitud para actualizar la habitacion con id {}.", id);

        // Obtener la version actual y construir el ETag vigente
        Integer currentVersion = roomService.getRoomVersionById(id);
        String currentEtag = buildRoomEtag(id, currentVersion);

        // Si el cliente envia If-Match, aplicamos control de concurrencia optimista
        if (ifMatch != null) {
            if (!currentEtag.equals(ifMatch)) {
                LOGGER.warn(
                        "V1 - ETag no coincide para la habitacion con id {}. If-Match={}, ETag actual={}. Devolviendo 412.",
                        id, ifMatch, currentEtag);

                // Se devuelve el ETag actual para que el cliente pueda reintentar con la version correcta
                return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED)
                        .eTag(currentEtag)
                        .build();
            } else {
                LOGGER.info("V1 - ETag coincidente para la habitacion con id {}. Continuando con la actualizacion.", id);
            }
        } else {
            LOGGER.info(
                    "V1 - Actualizacion de habitacion con id {} sin encabezado If-Match. Se aplicara la actualizacion sin chequeo de version en cabecera.",
                    id);
        }

        RoomDetailResponseDto responseDto = roomService.updateRoom(id, requestDto);

        // Recuperamos la nueva version tras la actualizacion para emitir un ETag actualizado
        Integer newVersion = roomService.getRoomVersionById(id);
        String newEtag = buildRoomEtag(id, newVersion);

        LOGGER.info("V1 - Habitacion actualizada con id {}. Nuevo ETag={}.", responseDto.getId(), newEtag);

        return ResponseEntity.ok()
                .eTag(newEtag)
                .body(responseDto);
    }

    /**
     * Elimina una habitacion identificada por su id.
     * Devuelve 204 No Content cuando la eliminacion es exitosa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id) {
        LOGGER.info("V1 - Recibida solicitud para eliminar la habitacion con id {}.", id);

        roomService.deleteRoom(id);

        LOGGER.info("V1 - Habitacion eliminada con id {}.", id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Construye el ETag para una habitacion a partir de su id y version.
     */
    private String buildRoomEtag(Long id, Integer version) {
        return "room-" + id + "-v" + (version == null ? 0 : version);
    }
}
