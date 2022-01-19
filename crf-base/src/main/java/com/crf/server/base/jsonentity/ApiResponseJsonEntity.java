package com.crf.server.base.jsonentity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ApiResponseJsonEntity {

    private int       responseCode;
    private String    responseText;
    private Date      responseDate;
    private List<?>   dataList;
    private Map<?, ?> dataMap;
    private PageInfo  page;
    private Object    data;
    private Object    singleData;
}
