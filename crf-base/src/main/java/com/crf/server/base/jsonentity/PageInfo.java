package com.crf.server.base.jsonentity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageInfo {

    private int  totalPages;
    private long totalElements;

    public PageInfo(int totalPages, long totalElements) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
