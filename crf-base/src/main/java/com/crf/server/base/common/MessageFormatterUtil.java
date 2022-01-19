package com.crf.server.base.common;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crf.server.base.exception.CRFException;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class MessageFormatterUtil {

    public static String formatText(String regex, String textTemplate, HashMap<String, Object> params) throws CRFException {

        final StringBuffer sb = new StringBuffer();
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(textTemplate);

        try {
            while (matcher.find()) {

                String expressionVariable = matcher.group(1);
                Object variableValue = params.get(expressionVariable);

                if (variableValue == null) {
                    log.error("formatText###Exception: Parameter " + expressionVariable + " not found.");
                    continue;
                }

                matcher.appendReplacement(sb, variableValue.toString());
            }
            matcher.appendTail(sb);

        } catch (Exception e) {

            log.error("MessageFormatterUtil##formatText##Exception: ", e);

            throw new CRFException(ServerResponseConstants.API_FAILURE_CODE, ServerResponseConstants.API_FAILURE_TEXT, "");
        }

        return sb.toString();
    }

    public static String formatText(String textTemplate, HashMap<String, Object> params) throws CRFException {
        return formatText(ServerConstants.REGEX_MESSAGE_FORMAT, textTemplate, params);
    }

}
