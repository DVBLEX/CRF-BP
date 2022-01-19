package com.crf.server.base.entity;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileBucket {

    private MultipartFile file;
    private int           fileRole;

}
