package com.crf.server.base.common;

import com.crf.server.base.exception.CRFException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.mobile.device.Device;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServerUtil {

    public static Date parseDate(String dateFormat, String dateString) throws ParseException {

        return new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(dateString);
    }

    public static String formatDate(String dateFormat, Date date) throws ParseException {

        if (date == null)
            return "";
        else
            return new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(date);
    }

    public static String restrictLength(String str, int length) {

        return ((str != null && str.length() > length) ? str.substring(0, length) : str);
    }

    public static String getValidNumber(String numberString, String responseSource) throws CRFException {

        numberString = numberString.toUpperCase().trim();
        numberString = numberString.replaceAll(" ", "");
        numberString = numberString.replaceAll("-", "");
        numberString = numberString.replaceAll(",", "");
        numberString = numberString.replaceAll("#", "");
        numberString = numberString.replaceAll("_", "");
        numberString = numberString.replaceAll("=", "");
        numberString = numberString.replaceAll(":", "");
        numberString = numberString.replaceAll("!", "");
        numberString = numberString.replaceAll("\\+", "");
        numberString = numberString.replaceAll("\\.", "");
        numberString = numberString.replaceAll("\\*", "");
        numberString = numberString.replaceAll("\\(", "");
        numberString = numberString.replaceAll("\\)", "");
        numberString = numberString.replaceAll("\\[", "");
        numberString = numberString.replaceAll("\\]", "");
        numberString = numberString.replaceAll("\\{", "");
        numberString = numberString.replaceAll("\\}", "");
        numberString = numberString.replaceAll("O", "0");

        try {
            if (Long.parseLong(numberString) <= 0)
                throw new CRFException(ServerResponseConstants.INVALID_MSISDN_CODE, ServerResponseConstants.INVALID_MSISDN_TEXT, responseSource + "1#");
        } catch (Exception e) {
            throw new CRFException(ServerResponseConstants.INVALID_MSISDN_CODE, ServerResponseConstants.INVALID_MSISDN_TEXT, responseSource + "2#");
        }

        return numberString;
    }

    public static String getDeviceTypeString(Device device) {

        if (device.isNormal())
            return "normal";
        else if (device.isMobile())
            return "mobile";
        else if (device.isTablet())
            return "tablet";
        return "unknown";
    }

    public static Pageable createDefaultPageRequest() {
        return PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
    }

    public static Pageable createDefaultPageRequest(int page, int size) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    }

    public static <T> Page<T> extractPage(Pageable pageable, List<T> contents) {
        final int total = contents.size();
        List<T> pagedContents;

        int startIndex = pageable.getPageNumber() == 0 ? pageable.getPageNumber() : pageable.getPageNumber() * pageable.getPageSize();
        int toIndex = startIndex + pageable.getPageSize();

        toIndex = toIndex > contents.size() ? contents.size() : toIndex;

        if (startIndex < contents.size()) {
            pagedContents = contents.subList(startIndex, toIndex);
        } else {
            pagedContents = new ArrayList<>();
        }

        return PageableExecutionUtils.getPage(pagedContents, pageable, () -> total);
    }

    public static String toJson(Object object) {
        ObjectMapper compact = new ObjectMapper();
        try {
            compact.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            compact.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return compact.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static int getNumberOfDaysInTheQuarter(int quarterId, boolean isLeapYear) {
        // First quarter, Q1: 1 January – 31 March (90 days or 91 days in leap years)
        // Second quarter, Q2: 1 April – 30 June (91 days)
        // Third quarter, Q3: 1 July – 30 September (92 days)
        // Fourth quarter, Q4: 1 October – 31 December (92 days)

        switch (quarterId) {
            case 1:
                if (isLeapYear)
                    return 91;
                else
                    return 90;

            case 2:
                return 91;

            case 3:
            case 4:
                return 92;

            default:
                return 91;
        }
    }

    public static boolean isBeginningOfTheQuarter(LocalDate localDateToday) {

        return (localDateToday.getMonthValue() == 1 || localDateToday.getMonthValue() == 4 || localDateToday.getMonthValue() == 7 || localDateToday.getMonthValue() == 10)
                && localDateToday.getDayOfMonth() == 1;
    }

    public static boolean isBeginningOfTheYear(LocalDate localDateToday) {

        return localDateToday.getMonthValue() == 1 && localDateToday.getDayOfMonth() == 1;
    }

    public static String getInterestFrequencyDescription(int interestPayoutFrequency) {

        String description = "";
        switch (interestPayoutFrequency) {
            case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_QUARTERLY:
                description = "Quarterly";
                break;

            case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_YEARLY:
                description = "Yearly";
                break;

            case ServerConstants.DEPOSIT_ACCOUNT_INTEREST_PAYOUT_FREQUENCY_HALF_YEARLY:
                description = "Half-Yearly";
                break;

            default:
                break;
        }

        return description;
    }

}
