package com.segurosargos.hotelbook.dto;

import java.util.List;

/**
 * Representa una página de habitaciones para ser utilizada en las vistas MVC.
 */
public class RoomPageResultDto {

    private final List<RoomSummaryResponseDto> rooms;

    private final int pageNumber;

    private final int pageSize;

    private final long totalElements;

    private final int totalPages;

    private final boolean first;

    private final boolean last;

    private final String sort;

    private final String direction;

    public RoomPageResultDto(List<RoomSummaryResponseDto> rooms,
                             int pageNumber,
                             int pageSize,
                             long totalElements,
                             int totalPages,
                             boolean first,
                             boolean last,
                             String sort,
                             String direction) {
        this.rooms = rooms;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.sort = sort;
        this.direction = direction;
    }

    public List<RoomSummaryResponseDto> getRooms() {
        return rooms;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isLast() {
        return last;
    }

    public String getSort() {
        return sort;
    }

    public String getDirection() {
        return direction;
    }
}
