package com.crf.server.web.config;

public class TestData {

    public static final String CALC_INTEREST_5_TERM_1Y_REQUEST   = "{\"depositAmount\":\"10000.00\",\"interestRate\":\"5.00\",\"termYears\":\"1\",\"depositProductJson\":{\"code\":\"e315331b75faafb4399c043ff1b6887fc6ccc82175e050ce5b58916112c9ce21\"}}";
    public static final String CALC_INTEREST_6_TERM_2Y_REQUEST   = "{\"depositAmount\":\"10000.00\",\"interestRate\":\"6.00\",\"termYears\":\"2\",\"depositProductJson\":{\"code\":\"0b5643532998638a67e521b95890561efd672d3ba633e415b0e9a4b5e832af18\"}}";
    public static final String CALC_INTEREST_5_5_TERM_2Y_REQUEST = "{\"depositAmount\":\"10000.00\",\"interestRate\":\"5.50\",\"termYears\":\"2\",\"depositProductJson\":{\"code\":\"0b5643532998638a67e521b95890561efd672d3ba633e415b0e9a4b5e832af18\"}}";
    public static final String SAVE_DEPOSIT_REQUEST              = "{\"depositAmount\":\"10000.00\",\"interestPayoutFrequency\":2,\"interestRate\":\"5.00\",\"termYears\":\"1\",\"bankTransferReference\":\"CRF0C44LR6FB5QAG\",\"depositProductJson\":{\"code\":\"e315331b75faafb4399c043ff1b6887fc6ccc82175e050ce5b58916112c9ce21\"}}";
}
