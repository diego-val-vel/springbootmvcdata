package com.segurosargos.hotelbook.repository;

import java.time.LocalDate;
import java.util.List;
import com.segurosargos.hotelbook.model.BookingEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * Repositorio Spring Data JPA para la entidad BookingEntity.
 * Se utiliza para consultas de reporte sobre reservas.
 */
public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {

    /*
     * Recupera los detalles de reserva dentro de un rango de fechas.
     * De forma opcional, permite filtrar por estatus.
     *
     * Si el parámetro status es nulo, se devuelven reservas con cualquier estatus.
     * Si se indica un valor, por ejemplo "CONFIRMED", solo se devuelven las reservas
     * cuyo estatus coincide exactamente.
     */
    @Query(
            "select " +
                    "b.id as bookingId, " +
                    "r.code as roomCode, " +
                    "r.name as roomName, " +
                    "g.firstName as guestFirstName, " +
                    "g.lastName as guestLastName, " +
                    "g.email as guestEmail, " +
                    "b.checkInDate as checkInDate, " +
                    "b.checkOutDate as checkOutDate, " +
                    "b.totalPrice as totalPrice, " +
                    "b.status as status " +
                    "from BookingEntity b " +
                    "join b.room r " +
                    "join b.guest g " +
                    "where b.checkInDate >= :startDate " +
                    "and b.checkOutDate <= :endDate " +
                    "and (:status is null or b.status = :status) " +
                    "order by b.checkInDate, r.code"
    )
    List<BookingDetailView> findBookingDetailsBetweenDatesAndStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);

    /*
     * Recupera estadísticas mensuales de reservas en el rango de fechas indicado.
     * Agrupa por año y mes de la fecha de check-in.
     *
     * Si status es nulo, se incluyen reservas con cualquier estatus.
     * Si se indica un valor, por ejemplo "CONFIRMED", solo se incluyen las reservas
     * cuyo estatus coincide exactamente.
     */
    @Query(
            value = ""
                    + "select "
                    + "    cast(extract(year from b.check_in_date) as int) as year, "
                    + "    cast(extract(month from b.check_in_date) as int) as month, "
                    + "    count(b.id) as bookingCount, "
                    + "    coalesce(sum(b.total_price), 0) as totalRevenue "
                    + "from bookings b "
                    + "where b.check_in_date >= :startDate "
                    + "  and b.check_in_date <= :endDate "
                    + "  and (:status is null or b.status = :status) "
                    + "group by "
                    + "    cast(extract(year from b.check_in_date) as int), "
                    + "    cast(extract(month from b.check_in_date) as int) "
                    + "order by "
                    + "    cast(extract(year from b.check_in_date) as int), "
                    + "    cast(extract(month from b.check_in_date) as int)",
            nativeQuery = true
    )
    List<MonthlyBookingStatsView> findMonthlyStatsBetweenDatesAndStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") String status);

    /*
     * Variante optimizada que utiliza EntityGraph para inicializar de forma
     * anticipada las asociaciones room y guest. Se indica explícitamente la
     * consulta JPQL para evitar que Spring Data intente derivar la consulta
     * a partir del nombre del método y provoque un error de análisis.
     */
    @EntityGraph(attributePaths = {"room", "guest"})
    @Query("select b from BookingEntity b")
    List<BookingEntity> findAllWithRoomAndGuestEntityGraph();

    /*
     * Variante optimizada que utiliza una consulta JPQL con JOIN FETCH para
     * inicializar las asociaciones room y guest en una sola consulta. Esta
     * forma también elimina el problema N+1 al traer toda la información
     * necesaria en el primer acceso.
     */
    @Query(
            "select b " +
                    "from BookingEntity b " +
                    "join fetch b.room r " +
                    "join fetch b.guest g"
    )
    List<BookingEntity> findAllWithRoomAndGuestFetchJoin();
}
