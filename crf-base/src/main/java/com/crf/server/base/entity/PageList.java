package com.crf.server.base.entity;

import java.util.List;

import com.crf.server.base.jsonentity.PageInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageList<E> {

    private List<E>  dataList;
    private PageInfo page;

    public PageList(List<E> dataList, PageInfo page) {
        this.dataList = dataList;
        this.page = page;
    }
}
