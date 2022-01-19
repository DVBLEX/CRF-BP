package com.crf.server.base.jsonentity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class SystemEnvironmentJson {

    private String                               appName;
    private String                               environment;
    private boolean                              isTestEnvironment;
    private String                               username;
    private String                               firstName;
    private String                               lastName;
    private boolean                              isInvestorOperator;
    private boolean                              isBorrowerOperator;
    private boolean                              isAdmin;
    private String                               deviceType;
    private Integer                              customerCategory;
    private CustomerFlagsJson                    customerFlags;
    private List<CustomerVerificationDenialJson> verificationDenialList;
    private String                               recaptchaKey;
    private String                               userMobileNumber;
    private String                               userEmail;
    private Integer                              customerType;
}
