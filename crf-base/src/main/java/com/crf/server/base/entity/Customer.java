package com.crf.server.base.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("customers")
@Getter
@Setter
@ToString
public class Customer {

    @Id
    @Column("id")
    private Long    id;

    @Column("code")
    private String  code;

    @Column("type")
    private int     type;

    @Column("category")
    private int     category;

    @Column("title")
    private String  title;

    @Column("first_name")
    private String  firstName;

    @Column("last_name")
    private String  lastName;

    @Column("date_of_birth")
    private Date    dateOfBirth;

    @Column("email")
    private String  email;

    @Column("msisdn")
    private String  msisdn;

    @Column("national_id_number")
    private String  nationalIdNumber;

    @Column("nationality")
    private String  nationality;

    @Column("residnence_country")
    private String  residnenceCountry;

    @Column("address_1")
    private String  address1;

    @Column("address_2")
    private String  address2;

    @Column("address_3")
    private String  address3;

    @Column("address_4")
    private String  address4;

    @Column("post_code")
    private String  postCode;

    @Column("kyc_option")
    private int     kycOption;

    @Column("id_1_type")
    private int     id1Type;                 // Passport, National ID Card or Driving license

    @Column("id_1_number")
    private String  id1Number;

    @Column("id_2_type")
    private int     id2Type;                 // Passport, National ID Card or Driving license

    @Column("id_2_number")
    private String  id2Number;

    @Column("date_id_1_expiry")
    private Date    dateID1Expiry;

    @Column("date_id_2_expiry")
    private Date    dateID2Expiry;

    @Column("poa_1_type")
    private int     poa1Type;                // Proof of Address type

    @Column("poa_2_type")
    private int     poa2Type;                // Proof of Address type

    @Column("is_passport_scan_uploaded")
    private Boolean isPassportScanUploaded;

    @Column("date_passport_scan_uploaded")
    private Date    datePassportScanUploaded;

    @Column("is_passport_scan_verified")
    private Boolean isPassportScanVerified;

    @Column("date_passport_scan_verified")
    private Date    datePassportScanVerified;

    @Column("is_passport_scan_denied")
    private Boolean isPassportScanDenied;

    @Column("date_passport_scan_denied")
    private Date    datePassportScanDenied;

    @Column("is_photo_uploaded")
    private Boolean isPhotoUploaded;

    @Column("date_photo_uploaded")
    private Date    datePhotoUploaded;

    @Column("is_bank_account_setup")
    private Boolean isBankAccountSetup;

    @Column("is_aml_verified")
    private Boolean isAmlVerified;

    @Column("is_deleted")
    private Boolean isDeleted;

    @Column("date_deleted")
    private Date    dateDeleted;

    @Column("date_created")
    private Date    dateCreated;

    @Column("date_edited")
    private Date    dateEdited;
}
