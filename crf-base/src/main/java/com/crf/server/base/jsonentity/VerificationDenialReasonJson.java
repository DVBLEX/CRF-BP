package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class VerificationDenialReasonJson {

    private long    id;
    private String  description;
    private String  question;
    private Boolean isPOIDRelated;
    private Boolean isPOARelated;
    private Boolean isPhotoRelated;
}
