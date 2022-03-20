package com.lamin.webapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponse<T> {

    /**
     * entity count
     */
    private Long count;
    /**
     * page number, 0 indicate the first page.
     */
    private int pageNumber;
    /**
     * size of page, 0 indicate infinite-sized.
     */
    private int pageSize;
    /**
     * Offset from the of pagination.
     */
    private long pageOffset;
    /**
     * the number total of pages.
     */
    private int pageTotal;
    /**
     * elements of page.
     */
    private List<T> elements;
}