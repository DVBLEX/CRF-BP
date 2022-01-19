package com.crf.server.base.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NameValuePair implements Serializable {

    private static final long serialVersionUID = 1L;

    private String            name;
    private Object            value;

    public NameValuePair(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
