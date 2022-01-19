package com.crf.server.base.jsonentity;

import java.util.List;

import com.crf.server.base.entity.FileData;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class VerifyCustomerJson {

    private CustomerJson                         customer;

    private FileData                             photo;
    private List<FileData>                       idScans;
    private List<FileData>                       poaScans;
    private List<CustomerVerificationDenialJson> customerVerificationDenialList;
    private String                               customerAMLResponse;
}
