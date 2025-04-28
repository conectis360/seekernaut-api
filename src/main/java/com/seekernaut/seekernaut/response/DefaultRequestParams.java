package com.seekernaut.seekernaut.response;

import com.seekernaut.seekernaut.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefaultRequestParams {

    private Integer pageNumber;

    private Integer pageSize;

    private Sort.Direction sortDirection;

    private String sortColumn;

    public Integer getPageNumber() {
        if (pageNumber == null) {
            pageNumber = 1;
        }
        // cliente vai passar a partir 1 e a busca na base comeca no 0
        if (pageNumber == 0) {
            throw new ValidationException("pageNumber must start at 1");
        }
        return pageNumber - 1;
    }

    public Integer getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }
}
