package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileData {

    private byte[] data;
    private String type;
    private String mimeType;
    private int    entityType;
    private int    entityNumber;
}
