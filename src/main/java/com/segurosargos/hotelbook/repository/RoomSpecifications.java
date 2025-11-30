package com.segurosargos.hotelbook.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;
import com.segurosargos.hotelbook.model.BookingEntity;
import com.segurosargos.hotelbook.model.RoomEntity;

/*
 * Conjunto de Specifications reutilizables para filtrar habitaciones.
 * Cada método devuelve una Specification<RoomEntity> que puede componerse
 * dinámicamente en el servicio según los filtros recibidos.
 */
public final class RoomSpecifications {

    private RoomSpecifications() {
    }

    /*
     * Filtra por texto contenido en el nombre de la habitación, ignorando mayúsculas/minúsculas.
     * Si el parámetro es nulo o vacío, no aplica ningún filtro.
     */
    public static Specification<RoomEntity> nameContainsIgnoreCase(String nameContains) {
        return (root, query, criteriaBuilder) -> {
            if (nameContains == null || nameContains.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String pattern = "%" + nameContains.trim().toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    pattern
            );
        };
    }

    /*
     * Filtra por capacidad mínima.
     * Si minCapacity es nulo, no aplica ningún filtro.
     */
    public static Specification<RoomEntity> capacityGreaterOrEqual(Integer minCapacity) {
        return (root, query, criteriaBuilder) -> {
            if (minCapacity == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), minCapacity);
        };
    }

    /*
     * Filtra por precio base mínimo por noche.
     * Si minPrice es nulo, no aplica ningún filtro.
     */
    public static Specification<RoomEntity> basePriceGreaterOrEqual(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("basePricePerNight"), minPrice);
        };
    }

    /*
     * Filtra por precio base máximo por noche.
     * Si maxPrice es nulo, no aplica ningún filtro.
     */
    public static Specification<RoomEntity> basePriceLessOrEqual(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("basePricePerNight"), maxPrice);
        };
    }

    /*
     * Filtra para incluir solo habitaciones activas.
     * Si onlyActive es nulo o false, no aplica ningún filtro adicional.
     */
    public static Specification<RoomEntity> onlyActive(Boolean onlyActive) {
        return (root, query, criteriaBuilder) -> {
            if (onlyActive == null || !onlyActive) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.isTrue(root.get("active"));
        };
    }

    /*
     * Filtra habitaciones disponibles en un rango de fechas.
     * La lógica de disponibilidad considera que una habitación está disponible
     * si no tiene reservas CONFIRMED cuyo rango de fechas se solape con el rango
     * [availableFrom, availableTo).
     *
     * Si availableFrom o availableTo son nulos, no aplica filtro de disponibilidad.
     */
    public static Specification<RoomEntity> availableBetween(LocalDate availableFrom,
                                                             LocalDate availableTo) {
        return (root, query, criteriaBuilder) -> {
            if (availableFrom == null || availableTo == null) {
                return criteriaBuilder.conjunction();
            }

            var subquery = query.subquery(Long.class);
            var bookingRoot = subquery.from(BookingEntity.class);

            subquery.select(criteriaBuilder.literal(1L));

            subquery.where(
                    criteriaBuilder.equal(bookingRoot.get("room"), root),
                    criteriaBuilder.equal(bookingRoot.get("status"), "CONFIRMED"),
                    criteriaBuilder.lessThan(bookingRoot.get("checkInDate"), availableTo),
                    criteriaBuilder.greaterThan(bookingRoot.get("checkOutDate"), availableFrom)
            );

            return criteriaBuilder.not(criteriaBuilder.exists(subquery));
        };
    }
}
