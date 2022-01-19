package com.crf.server.base.jsonentity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DenyCustomerJson {

    private String                               customerCode;
    private List<CustomerVerificationDenialJson> reasonList;
}
