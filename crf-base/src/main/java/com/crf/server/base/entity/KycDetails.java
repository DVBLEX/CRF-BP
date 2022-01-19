package com.crf.server.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
@ToString
public class KycDetails {
    private Integer         kycOption;
    private Integer         id1Type;
    private String          id1Number;
    private Integer         id2Type;
    private String          id2Number;
    private String          dateId1ExpiryString;
    private String          dateId2ExpiryString;
    private Date            dateId1Expiry;
    private Date            dateId2Expiry;
    private Integer         poa1Type;
    private Integer         poa2Type;
    private MultipartFile[] id1Files;
    private MultipartFile[] id2Files;
    private MultipartFile[] poa1Files;
    private MultipartFile[] poa2Files;
    private MultipartFile[] photoFiles;
    private String          isResubmission;
}
