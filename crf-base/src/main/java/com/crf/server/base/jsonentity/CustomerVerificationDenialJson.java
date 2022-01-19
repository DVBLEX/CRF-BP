package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CustomerVerificationDenialJson {

    private long   id;
    private long   reasonId;
    private String denialReason;
    private String additionalDescription;
}
