package com.crf.server.base.common;

public class ServerConstants {

    public static final String SYSTEM_ENVIRONMENT_LOCAL                                          = "LOCAL";
    public static final String SYSTEM_ENVIRONMENT_DEV                                            = "DEV";
    public static final String SYSTEM_ENVIRONMENT_PROD                                           = "PROD";

    public static final int    DEFAULT_INT                                                       = -1;
    public static final long   DEFAULT_LONG                                                      = -1l;
    public static final String DEFAULT_STRING                                                    = "";
    public static final int    DEFAULT_VALIDATION_LENGTH_16                                      = 16;
    public static final int    DEFAULT_VALIDATION_LENGTH_64                                      = 64;

    public static final long   CUSTOM_EMAIL_CONFIG_ID                                            = 1l;

    public static final String dateFormatddMMyyyy                                                = "dd/MM/yyyy";
    public static final String dateFormatyyyyMMddHHmmss                                          = "yyyyMMddHHmmss";
    public static final String dateFormatMMddyyyy                                                = "MM/dd/yyyy";
    public static final String dateFormatddMMyyyyHHmm                                            = "dd/MM/yyyy HH:mm";

    public static final long   DAY_MILLIS                                                        = 1000l * 60l * 60l * 24l;
    public static final long   NINE_MINUTES_MILLIS                                               = 1000l * 60l * 9l;

    public static final long   SYSTEM_TIMER_TASK_SMS_ID                                          = 101l;
    public static final long   SYSTEM_TIMER_TASK_EMAIL_ID                                        = 102l;
    public static final long   SYSTEM_TIMER_TASK_DAILY_ID                                        = 103l;
    public static final long   SYSTEM_TIMER_TASK_SYSTEM_STATUS_CHECK_ID                          = 110l;

    public static final String REGEX_MESSAGE_FORMAT                                              = "\\$\\{(?:\\s|\\&nbsp\\;)*([\\w\\_\\-]+)(?:\\s|\\&nbsp\\;)*\\}";
    public static final String REGEXP_EMAIL                                                      = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String REGEX_PASSWORD                                                    = "(?=(:?.*[^A-Za-z0-9].*))(?=(:?.*[A-Z].*){1,})(?=(:?.*\\d.*){1,})(:?^[\\w\\&\\?\\!\\$\\#\\*\\+\\=\\%\\^\\@\\-\\.\\,\\_]{8,32}$)";
    public static final String REGEX_REGISTRATION_CODE                                           = "[\\d]{5}";
    public static final String REGEX_UNIVERSAL_COUNTRY_CODE                                      = "[A-Z]{2}";
    public static final String REGEX_SHA256                                                      = "[A-Fa-f0-9]{64}";
    public static final String REGEXP_BASIC_PERMISSIVE_SAFE_TEXT                                 = "(?!^.*(\\&lt|\\&gt).*$)(?:^[^\\<\\>\\\\\\#\\`\\§\\±\\~]+$)";
    public static final String REGEXP_BASIC_ID_NUMBER                                            = "[A-Za-z0-9]+";
    public static final String REGEX_BANK_ACCOUNT_IBAN                                           = "(?:[A-Z]{2})(?:[A-Za-z0-9]){13,39}";
    public static final String REGEX_BANK_TRANSFER_REF                                           = "CRF[A-Z0-9]{13}";

    public static final String SYSTEM_TOKEN_PREFIX                                               = "crf";
    public static final String SYSTEM_TOKEN_SUFFIX1                                              = "7q4HA";
    public static final String SYSTEM_TOKEN_SUFFIX2                                              = "z9X0e";

    public static final String SYSTEM_FILE_STORE_ALIAS_SALT                                      = "Lj3e9e3JoO08h9e";
    public static final String SYSTEM_FILE_STORE_PASSWORD_PROTECTION_SALT                        = "8weQ78hjnE3h1140";

    public static final int    KEYSTORE_NUMBER_OF_KEYS_PERSONAL_DATA                             = 10000;
    public static final int    KEYSTORE_NUMBER_OF_KEYS_BUSINESS_DATA                             = 10000;
    public static final int    KEYSTORE_ID_PERSONAL_DATA                                         = 1;
    public static final int    KEYSTORE_ID_BUSINESS_DATA                                         = 2;

    public static final int    IMAGE_COMPRESSION_LOWER_BOUND                                     = 1048576;

    public static final int    FILE_ROLE_ID_DOCUMENT                                             = 1;
    public static final int    FILE_ROLE_PHOTOGRAPH                                              = 2;
    public static final int    FILE_ROLE_POA_DOCUMENT                                            = 3;

    public static final int    CHANNEL_SYSTEM                                                    = 1024;

    public static final long   SCHEDULER_ID                                                      = -100l;

    public static final int    SIZE_VERIFICATION_CODE                                            = 5;

    public static final int    PROCESS_NOTPROCESSED                                              = 0;
    public static final int    PROCESS_PROGRESS                                                  = 1;
    public static final int    PROCESS_PROCESSED                                                 = 2;

    public static final long   EMAIL_VERIFICATION_CODE_TEMPLATE_TYPE                             = 1;
    public static final long   EMAIL_CUSTOMER_REGISTRATION_LINK_TEMPLATE_ID                      = 2;
    public static final long   EMAIL_CUSTOMER_REGISTRATION_SUCCESSFUL_TEMPLATE_ID                = 3;
    public static final long   EMAIL_MOBILE_UPLOAD_LINK_TEMPLATE_ID                              = 4;
    public static final long   EMAIL_CUSTOMER_VERIFICATION_SUCCESSFUL_TEMPLATE_ID                = 5;
    public static final long   EMAIL_CUSTOMER_VERIFICATION_DENIED_TEMPLATE_ID                    = 6;
    public static final long   EMAIL_CUSTOMER_INVESTMENT_PRODUCT_SELECTED_TEMPLATE_ID            = 7;
    public static final long   EMAIL_CUSTOMER_INVESTMENT_PRODUCT_APPROVED_TEMPLATE_ID            = 8;
    public static final long   EMAIL_PASSWORD_FORGOT_TEMPLATE_ID                                 = 9;
    public static final long   EMAIL_CUSTOMER_INVESTMENT_PRODUCT_WITHDRAWAL_APPROVED_TEMPLATE_ID = 10;
    public static final long   EMAIL_CUSTOMER_INVESTMENT_PRODUCT_EXPIRED_TEMPLATE_ID             = 11;
    public static final long   EMAIL_CUSTOMER_INVESTMENT_PRODUCT_MATURED_TEMPLATE_ID             = 12;
    public static final long   EMAIL_CUSTOMER_SUPPORT_QUERY_ID                                   = 13;
    public static final long   EMAIL_CUSTOMER_INVESTMENT_PRODUCT_INTEREST_PAID_TEMPLATE_ID       = 14;
    public static final long   EMAIL_ADMIN_REGISTRATION_LINK_TEMPLATE_ID                         = 15;

    public static final long   SMS_VERIFICATION_CODE_TEMPLATE_TYPE                               = 1;

    public static final int    CUSTOMER_TYPE_INVESTOR                                            = 1;
    public static final int    CUSTOMER_TYPE_BORROWER                                            = 2;
    public static final int    CUSTOMER_TYPE_INVESTOR_AND_BORROWER                               = 3;

    public static final int    CUSTOMER_CATEGORY_INDIVIDUAL                                      = 1;
    public static final int    CUSTOMER_CATEGORY_COMPANY                                         = 2;

    public static final int    DEPOSIT_ACCOUNT_STATUS_INITIATED                                  = 1;
    public static final int    DEPOSIT_ACCOUNT_STATUS_ACTIVE                                     = 2;
    public static final int    DEPOSIT_ACCOUNT_STATUS_WITHDRAWAL_REQUESTED                       = 3;
    public static final int    DEPOSIT_ACCOUNT_STATUS_WITHDRAWN                                  = 4;
    public static final int    DEPOSIT_ACCOUNT_STATUS_INITIATED_EXPIRED                          = 5;
    public static final int    DEPOSIT_ACCOUNT_STATUS_MATURED                                    = 10;

    public static final int    DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY               = 1;
    public static final int    DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY                  = 2;
    public static final int    DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY             = 3;

    public static final int    ID_TYPE_PASSPORT                                                  = 1;
    public static final int    ID_TYPE_NATIONAL_ID_CARD                                          = 2;
    public static final int    ID_TYPE_DRIVING_LICENSE                                           = 3;

    public static final int    POA_TYPE_UTILITY_BILL                                             = 4;
    public static final int    POA_TYPE_BANK_STATEMENT                                           = 5;
    public static final int    POA_TYPE_TAX_NOTICE                                               = 6;
    public static final int    POA_TYPE_SOCIAL_WELFARE                                           = 7;
    public static final int    POA_TYPE_MOTOR_TAX                                                = 8;
    public static final int    POA_TYPE_HOME_OR_MOTOR_INSURANCE_CERT                             = 9;

    public static final int    KYC_OPTION_COPY                                                   = 1;
    public static final int    KYC_OPTION_ORIGINAL_OR_CERTIFIED                                  = 2;

    public static final int    OPERATOR_ROLE_INVESTOR                                            = 1;
    public static final int    OPERATOR_ROLE_BORROWER                                            = 2;
    public static final int    OPERATOR_ROLE_INVESTOR_AND_BORROWER                               = 3;
    public static final int    OPERATOR_ROLE_ADMIN                                               = 100;

    public static final String SPRING_SECURITY_ROLE_PREFIX                                       = "ROLE_";

    public static final int    CAPTCHA_MAX_ATTEMPT                                               = 4;

    public static final int    AML_API_RESPONSE_MAX_LENGTH                                       = 65535;

    public static final String BANK_ACCOUNT_NAME                                                 = "JMGC Finance Limited";
    public static final String BANK_IBAN                                                         = "IE74 BOFI 901490 59821380";
    public static final String BANK_BIC                                                          = "BOFIIE2D";
    public static final String BANK_NAME                                                         = "Bank of Ireland";
    public static final String BANK_ADDRESS                                                      = "26 Mountjoy Square, Dublin 1, Ireland";

    public static final int    PDF_DOC_TYPE_DEPOSIT_INITIATED                                    = 1;
    public static final int    PDF_DOC_TYPE_DEPOSIT_INTEREST_PAYMENT                             = 2;

    public static final int    DEPOSIT_STATEMENT_TYPE_DEPOSIT_APPROVED                           = 1;
    public static final int    DEPOSIT_STATEMENT_TYPE_INTEREST_EARNED                            = 2;
    public static final int    DEPOSIT_STATEMENT_TYPE_INTEREST_PAYMENT                           = 3;
    public static final int    DEPOSIT_STATEMENT_TYPE_INTEREST_OFFSET_DUE_TO_EARLY_WITHDRAWAL    = 4;
    public static final int    DEPOSIT_STATEMENT_TYPE_INTEREST_EARNED_DUE_TO_EARLY_WITHDRAWAL    = 5;
    public static final int    DEPOSIT_STATEMENT_TYPE_WITHDRAWAL_FEE                             = 6;
    public static final int    DEPOSIT_STATEMENT_TYPE_WITHDRAWAL                                 = 7;
}
