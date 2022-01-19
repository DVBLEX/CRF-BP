package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CustomerJson {

    private String  code;
    private int     type;
    private int     category;
    private String  title;
    private String  firstName;
    private String  lastName;
    private String  dateOfBirthString;
    private String  email;
    private String  msisdn;
    private String  nationalIdNumber;
    private String  nationality;
    private String  residnenceCountry;
    private String  address1;
    private String  address2;
    private String  address3;
    private String  address4;
    private String  postCode;
    private int     kycOption;
    private int     id1Type;                // Passport, National ID Card or Driving license
    private String  id1Number;
    private int     id2Type;                // Passport, National ID Card or Driving license
    private String  id2Number;
    private String  dateId1ExpiryString;
    private String  dateId2ExpiryString;
    private int     poa1Type;               // Proof of Address type
    private int     poa2Type;               // Proof of Address type
    private Boolean isPassportScanUploaded;
    private Boolean isPassportScanVerified;
    private Boolean isPassportScanDenied;
    private Boolean isPhotoUploaded;
    private Boolean isAmlVerified;
}
