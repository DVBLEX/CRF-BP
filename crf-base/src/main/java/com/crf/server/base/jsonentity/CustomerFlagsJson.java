package com.crf.server.base.jsonentity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class CustomerFlagsJson {

    private Boolean isPassportScanUploaded;
    private Boolean isPassportScanVerified;
    private Boolean isPassportScanDenied;
    private Boolean isPhotoUploaded;
}
