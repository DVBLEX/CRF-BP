package com.crf.server.base.common;

public class ServerResponseConstants {

    // [0] Generic Success Code
    public static final int    SUCCESS_CODE                                   = 0;
    public static final String SUCCESS_TEXT                                   = "Success.";

    // [1] Generic Failure Code
    public static final int    FAILURE_CODE                                   = 1;
    public static final String FAILURE_TEXT                                   = "Failure.";

    // [1000] API Validation - Invalid Request Format
    public static final int    INVALID_REQUEST_FORMAT_CODE                    = 1000;
    public static final String INVALID_REQUEST_FORMAT_TEXT                    = "Invalid Request Format.";

    // [1001 - 1040] API Validation - Missing Fields
    public static final int    MISSING_DEPOSIT_AMOUNT_CODE                    = 1001;
    public static final String MISSING_DEPOSIT_AMOUNT_TEXT                    = "Missing Deposit Amount.";

    public static final int    MISSING_INTEREST_RATE_CODE                     = 1002;
    public static final String MISSING_INTEREST_RATE_TEXT                     = "Missing Interest Rate.";

    public static final int    MISSING_TERM_YEARS_CODE                        = 1003;
    public static final String MISSING_TERM_YEARS_TEXT                        = "Missing Term Years.";

    public static final int    MISSING_DEPOSIT_DATE_OPEN_CODE                 = 1004;
    public static final String MISSING_DEPOSIT_DATE_OPEN_TEXT                 = "Missing Deposit Date Open.";

    public static final int    MISSING_DEPOSIT_INTEREST_PAYOUT_FREQUENCY_CODE = 1005;
    public static final String MISSING_DEPOSIT_INTEREST_PAYOUT_FREQUENCY_TEXT = "Missing Deposit Interest Payout Frequency.";

    public static final int    MISSING_BANK_TRANSFER_REF_CODE                  = 1006;
    public static final String MISSING_BANK_TRANSFER_REF_TEXT                  = "Missing Bank Transfer Reference.";

    // [1041 - 1100] API Validation - Invalid Data Type / Range
    public static final int    INVALID_TERM_YEARS_CODE                        = 1041;
    public static final String INVALID_TERM_YEARS_TEXT                        = "Invalid Term Years. Please enter a valid Term: min value = 1, max value = 99.";

    public static final int    INVALID_EMAIL_CODE                             = 1042;
    public static final String INVALID_EMAIL_TEXT                             = "Invalid Email.";

    public static final int    INVALID_MSISDN_CODE                            = 1043;
    public static final String INVALID_MSISDN_TEXT                            = "Invalid Mobile Number.";

    public static final int    INVALID_DEPOSIT_AMOUNT_CODE                    = 1044;
    public static final String INVALID_DEPOSIT_AMOUNT_TEXT                    = "Invalid Deposit Amount. Please enter amount between the minimum and maximum allowed for this product.";

    public static final int    INVALID_BANK_NAME_CODE                         = 1045;
    public static final String INVALID_BANK_NAME_TEXT                         = "Invalid Bank Name. The bank name must be between 2 and 64 characters.";

    public static final int    INVALID_BANK_ACCOUNT_NAME_CODE                 = 1046;
    public static final String INVALID_BANK_ACCOUNT_NAME_TEXT                 = "Invalid Bank Account Name. The bank account name must be between 2 and 64 characters.";

    public static final int    INVALID_BANK_ADDRESS_CODE                      = 1047;
    public static final String INVALID_BANK_ADDRESS_TEXT                      = "Invalid Bank Address. The bank address must be between 2 and 256 characters.";

    public static final int    INVALID_IBAN_CODE                              = 1048;
    public static final String INVALID_IBAN_TEXT                              = "Invalid IBAN. The IBAN number consists of a two-letter country code, followed by two check digits, and up to thirty-five alphanumeric characters.";

    public static final int    INVALID_BIC_CODE                               = 1049;
    public static final String INVALID_BIC_TEXT                               = "Invalid BIC. It is an eight to eleven character code that is used to identify a specific bank";

    public static final int    INVALID_DEPOSIT_INTEREST_PAYOUT_FREQUENCY_CODE = 1050;
    public static final String INVALID_DEPOSIT_INTEREST_PAYOUT_FREQUENCY_TEXT = "Invalid Deposit Interest Payout Frequency.";

    //[1051 - 1061] deposit product validation codes and messages:
    public static final int    INVALID_DEPOSIT_PRODUCT_NAME_CODE              = 1151;
    public static final String INVALID_DEPOSIT_PRODUCT_NAME_TEXT              = "Invalid Deposit Product name, max length must be 32 characters";

    public static final int    INVALID_DEPOSIT_PRODUCT_DESCRIPTION_CODE       = 1152;
    public static final String INVALID_DEPOSIT_PRODUCT_DESCRIPTION_TEXT       = "Invalid Deposit Product description, max length must be 256 characters";

    public static final int    INVALID_MIN_DEPOSIT_AMOUNT_CODE                = 1053;
    public static final String INVALID_MIN_DEPOSIT_AMOUNT_TEXT                = "Invalid Deposit Product minimum value, min value allowed = 5000.00";

    public static final int    INVALID_MAX_DEPOSIT_AMOUNT_CODE                = 1054;
    public static final String INVALID_MAX_DEPOSIT_AMOUNT_TEXT                = "Invalid Deposit Product maximum value, max value cannot be less than minimum deposit amount value ";

    public static final int    INVALID_DEPOSIT_QUARTERLY_INTEREST_RATE_CODE   = 1055;
    public static final String INVALID_DEPOSIT_QUARTERLY_INTEREST_RATE_TEXT   = "Invalid quarterly interest rate, min value = 0.01, max value = 10.00";

    public static final int    INVALID_DEPOSIT_YEARLY_INTEREST_RATE_CODE      = 1056;
    public static final String INVALID_DEPOSIT_YEARLY_INTEREST_RATE_TEXT      = "Invalid yearly interest rate, min value = 0.01, max value = 10.00";

    public static final int    INVALID_DEPOSIT_TWICE_YEARLY_INTEREST_RATE_CODE= 1057;
    public static final String INVALID_DEPOSIT_TWICE_YEARLY_INTEREST_RATE_TEXT= "Invalid twice yearly interest rate, min value = 0.01, max value = 10.00";

    public static final int    INVALID_PREMATURE_WITHDRAWAL_MIN_DAYS_CODE     = 1058;
    public static final String INVALID_PREMATURE_WITHDRAWAL_MIN_DAYS_TEXT     = "Invalid premature withdrawal min days, min value allowed = 0, max value = 80";

    public static final int    INVALID_PREMATURE_WITHDRAWAL_INTEREST_RATE_CODE= 1059;
    public static final String INVALID_PREMATURE_WITHDRAWAL_INTEREST_RATE_TEXT= "Invalid premature withdrawal interest rate, min value = 0.01, max value = 10.00";

    public static final int    INVALID_WITHDRAWAL_FEE_VALUE_CODE              = 1060;
    public static final String INVALID_WITHDRAWAL_FEE_VALUE_TEXT              = "Invalid premature withdrawal fee, min value = 0.00, max value = 1000.00";

    public static final int    INVALID_BIG_DECIMAL_NUMBER_FORMAT_CODE         = 1061;
    public static final String INVALID_BIG_DECIMAL_NUMBER_FORMAT_TEXT         = "Invalid number format, allowed decimal digit number, decimal point typing in following format: '10.23' or '2.35'";

    public static final int    INVALID_AGE_CODE                               = 1062;
    public static final String INVALID_AGE_TEXT                               = "Invalid age, must be at least 18 years";

    public static final int    INVALID_BANK_TRANSFER_REF_CODE                  = 1063;
    public static final String INVALID_BANK_TRANSFER_REF_TEXT                  = "Invalid Bank Transfer Reference.";

    public static final int    INVALID_TOO_WEAK_PASSWORD_CODE                 = 1128;
    public static final String INVALID_TOO_WEAK_PASSWORD_TEXT                 = "The given new password is too weak. Use at least 1 capital letter, 1 number and 1 special character. The password length has to be between 8 and 32 characters.";

    public static final int    MISMATCH_PASSWORD_CODE                         = 1129;
    public static final String MISMATCH_PASSWORD_TEXT                         = "Confirm Password does not match Password.";

    public static final int    INVALID_CURRENT_PASSWORD_CODE                  = 1130;
    public static final String INVALID_CURRENT_PASSWORD_TEXT                  = "The Current Password does not match";

    public static final int    LIMIT_EXCEEDED_VERIFICATION_CODE_SENT_CODE     = 1131;
    public static final String LIMIT_EXCEEDED_VERIFICATION_CODE_SENT_TEXT     = "You have exceeded the number of attempts allowed.";

    public static final int    LIMIT_EXCEEDED_EMAIL_FORGOT_PASSWORD_CODE      = 1132;
    public static final String LIMIT_EXCEEDED_EMAIL_FORGOT_PASSWORD_TEXT      = "You have exceeded the number of attempts allowed.";

    public static final int    INVALID_VERIFICATION_CODE_CODE                 = 1133;
    public static final String INVALID_VERIFICATION_CODE_TEXT                 = "Invalid Verification Code.";

    public static final int    LIMIT_VERIFICATION_EXCEEDED_CODE               = 1134;
    public static final String LIMIT_VERIFICATION_EXCEEDED_TEXT               = "You have exceeded the number of attempts allowed.";

    public static final int    EXPIRED_VERIFICATION_CODE_CODE                 = 1135;
    public static final String EXPIRED_VERIFICATION_CODE_TEXT                 = "The Verification Code is expired.";

    public static final int    VERIFICATION_EXPIRED_CODE                      = 1136;
    public static final String VERIFICATION_EXPIRED_TEXT                      = "The time for completing the registration is up. Please start the registration process again.";

    public static final int    INVALID_ID_TYPE_CODE                           = 1139;
    public static final String INVALID_ID_TYPE_TEXT                           = "Invalid ID type.";

    public static final int    INVALID_ID_NUMBER_CODE                         = 1140;
    public static final String INVALID_ID_NUMBER_TEXT                         = "Invalid ID Number.";

    public static final int    INVALID_POA_TYPE_CODE                          = 1141;
    public static final String INVALID_POA_TYPE_TEXT                          = "Invalid Proof of Address type.";

    public static final int    INVALID_EXPIRY_DATE_CODE                       = 1142;
    public static final String INVALID_EXPIRY_DATE_TEXT                       = "Invalid expiry date.";

    public static final int    INVALID_OLD_PASSWORD_CODE                      = 1143;
    public static final String INVALID_OLD_PASSWORD_TEXT                      = "Invalid old password.";

    public static final int    INVALID_NEW_PASSWORD_CODE                      = 1144;
    public static final String INVALID_NEW_PASSWORD_TEXT                      = "Invalid New Password. New Password should not be the same as Current Password.";

    public static final int    CUSTOMER_MSISDN_ALREADY_REGISTERED_CODE        = 1163;
    public static final String CUSTOMER_MSISDN_ALREADY_REGISTERED_TEXT        = "Mobile number is already registered.";

    // [1186 - 1199] Miscellaneous Validation errors
    public static final int    CUSTOMER_EMAIL_ALREADY_REGISTERED_CODE         = 1186;
    public static final String CUSTOMER_EMAIL_ALREADY_REGISTERED_TEXT         = "Email is already registered.";

    public static final int    CUSTOMER_EMAIL_BLOCKED_DOMAIN_CODE             = 1187;
    public static final String CUSTOMER_EMAIL_BLOCKED_DOMAIN_TEXT             = "Email domain is not allowed.";

    public static final int    FILE_NOT_FOUND_CODE                            = 1197;
    public static final String FILE_NOT_FOUND_TEXT                           = "File not found";

    public static final int    MISSING_FILE_ON_FILEUPLOAD_CODE                = 1198;
    public static final String MISSING_FILE_ON_FILEUPLOAD_TEXT                = "Missing File.";

    public static final int    URL_EXPIRED_CODE                               = 1199;
    public static final String URL_EXPIRED_TEXT                               = "The URL is expired.";

    // [1200] API Failure - Internal Generic
    public static final int    API_FAILURE_CODE                               = 1200;
    public static final String API_FAILURE_TEXT                               = "Failure.";

    // [1301-1399] API Validation - Limit Exceeded (Security)
    public static final int    LIMIT_EXCEEDED_SMARTPHONE_UPLOAD_LINK_CODE     = 1306;
    public static final String LIMIT_EXCEEDED_SMARTPHONE_UPLOAD_LINK_TEXT     = "You have exceeded the number of attempts allowed.";

    // [1400 - 1499] API Failure - External
    public static final int    EXTERNAL_API_SOCKET_TIMEOUT_CODE               = 1400;
    public static final String EXTERNAL_API_SOCKET_TIMEOUT_TEXT               = "Connection Timeout.";

    public static final int    EXTERNAL_API_CONNECTION_FAILURE_CODE           = 1401;
    public static final String EXTERNAL_API_CONNECTION_FAILURE_TEXT           = "Connection Failure.";

    public static final int    SUCCESS_TEST_CODE                              = 3000;

}
