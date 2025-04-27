package com.seekernaut.seekernaut.utils;


import com.seekernaut.seekernaut.response.DefaultRequestParams;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class PageRequestHelper {
    public PageRequest getPageRequest(DefaultRequestParams requestParams) {
        if (requestParams.getSortColumn() == null || requestParams.getSortDirection() == null) {
            return PageRequest.of(requestParams.getPageNumber(), requestParams.getPageSize());
        }
        return PageRequest.of(requestParams.getPageNumber(), requestParams.getPageSize(), requestParams.getSortDirection(), requestParams.getSortColumn());
    }
}
