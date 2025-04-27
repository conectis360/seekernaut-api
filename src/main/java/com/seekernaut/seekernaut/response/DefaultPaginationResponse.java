package com.seekernaut.seekernaut.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefaultPaginationResponse<T> {

    private int totalPages;
    private Long totalRecords;
    private Integer pageNumber;
    private int pageSize;
    private List<T> records;

    public Integer getPageNumber() {
        return totalPages == 0 ? pageNumber : pageNumber+1;
    }
}
