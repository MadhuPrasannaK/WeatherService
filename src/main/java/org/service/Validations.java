package org.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.service.Constants.YYYY_MM_DD_FORMAT;

public class Validations {
    public List<Validation> validateRetrieveAverageMetrics(String metric, String dateFrom, String dateTo) throws Exception {
        List<Validation> validations = new ArrayList<>();
        validations.add(validateDateFormat(dateFrom, "dateFrom"));
        validations.add(validateDateFormat(dateTo, "dateTo"));
        validations.add(validateDatesBetween(dateFrom, dateTo));
        return validations;
    }

    public Validation validateDateFormat(String dateStr, String key) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(YYYY_MM_DD_FORMAT));
            return new Validation(ValidationStatus.SUCCESS, "");
        } catch (Exception e) {
            return new Validation(ValidationStatus.FAILURE, String.format("Parameter %s should be valid and in %s format", key, YYYY_MM_DD_FORMAT));
        }
    }

    public Validation validateDatesBetween(String dateFrom, String dateTo) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_FORMAT);
        if (sdf.parse(dateFrom).compareTo(sdf.parse(dateTo)) <= 0) {
            return new Validation(ValidationStatus.SUCCESS, "");
        } else {
            return new Validation(ValidationStatus.FAILURE, "Provided dates are out of range");
        }
    }

    public List<Validation> validateReportMetrics(String requestBody) {
        List<Validation> validations = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(requestBody);
        } catch (org.json.simple.parser.ParseException e) {
            validations.add(
                    new Validation(ValidationStatus.FAILURE, "request body is not a valid json object")
            );
            return validations;
        }
        if (!jsonObject.containsKey("sensorId")) {
            validations.add(new Validation(ValidationStatus.FAILURE, "sensorId is required"));
        }
        Object[] keyArray = jsonObject.keySet().toArray();
        if (!containsAtleast1Metric(keyArray)) {
            validations.add(
                    new Validation(
                            ValidationStatus.FAILURE,
                            String.format("Provide at least 1 valid metric from %s", Arrays.stream(Metrics.values()).map(Enum::name).collect(Collectors.toList()))
                    )
            );
        }
        return validations;
    }

    private boolean containsAtleast1Metric(Object[] keys) {
        for (Object key: keys) {
            for (Metrics metric: Metrics.values()) {
                if (metric.name().equals((String) key)) return true;
            }
        }
        return false;
    }

    public ResponseEntity<Object> respond(List<Validation> failedValidations) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(failedValidations);
        return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
    }
}
