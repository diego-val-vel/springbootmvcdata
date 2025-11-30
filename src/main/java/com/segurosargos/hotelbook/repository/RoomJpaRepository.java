package com.segurosargos.hotelbook.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.segurosargos.hotelbook.model.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * Repositorio Spring Data JPA para la entidad RoomEntity.
 */
public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long>,
        JpaSpecificationExecutor<RoomEntity> {

    /*
     * Busca habitaciones cuyo nombre contenga el texto indicado, ignorando mayúsculas/minúsculas.
     */
    List<RoomEntity> findByNameContainingIgnoreCase(String name);

    /*
     * Busca habitaciones cuyo precio base por noche esté dentro del rango indicado.
     */
    List<RoomEntity> findByBasePricePerNightBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /*
     * Obtiene un resumen de ocupación por habitación para una fecha de referencia.
     * Para cada habitación se recupera el identificador, código, nombre, precio base
     * y el número de reservas activas en esa fecha.
     *
     * Se consideran activas las reservas con estatus CONFIRMED y cuyo rango de fechas
     * contiene la fecha de referencia.
     */
    @Query(
            "select " +
                    "r.id as roomId, " +
                    "r.code as roomCode, " +
                    "r.name as roomName, " +
                    "r.basePricePerNight as basePricePerNight, " +
                    "count(b.id) as activeBookingsCount " +
                    "from RoomEntity r " +
                    "left join r.bookings b with " +
                    "b.status = 'CONFIRMED' " +
                    "and b.checkInDate <= :referenceDate " +
                    "and b.checkOutDate > :referenceDate " +
                    "group by r.id, r.code, r.name, r.basePricePerNight " +
                    "order by r.code"
    )
    List<RoomOccupancyView> findRoomOccupancySummaryByReferenceDate(
            @Param("referenceDate") LocalDate referenceDate);
}
