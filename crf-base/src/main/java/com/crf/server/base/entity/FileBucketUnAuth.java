package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileBucketUnAuth extends FileBucket {

    private String email;
    private String t1;
    private String t2;
}
