package com.segurosargos.hotelbook.dto;

import java.util.List;

/**
 * Representa una página de resultados para listados en la aplicación.
 *
 * @param <T> tipo de los elementos contenidos en la página
 */
public class PageResult<T> {

    private final List<T> content;

    private final int pageNumber;

    private final int pageSize;

    private final long totalElements;

    private final int totalPages;

    private final boolean first;

    private final boolean last;

    public PageResult(List<T> content,
                      int pageNumber,
                      int pageSize,
                      long totalElements,
                      int totalPages,
                      boolean first,
                      boolean last) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public List<T> getContent() {
        return content;
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
}
