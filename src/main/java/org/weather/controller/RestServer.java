package org.weather.controller;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.weather.service.AggregationService;
import org.weather.service.ReportingService;
import org.weather.validations.Validation;
import org.weather.validations.ValidationStatus;
import org.weather.validations.Validations;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Defines the REST API endpoints
 */
@RestController
public class RestServer {

    // Initialize sub-services and validations
    Validations validations = new Validations();
    // aggregation service is the service to get aggregated metrics like average metrics
    AggregationService aggregationService = new AggregationService();
    // reporting service is the service used by sensors to report metrics
    ReportingService reportingService = new ReportingService();

    /**
     * Method for the GET endpoint to fetch average metrics
     * example: GET /retrieve-average-metrics?metric=wind&dateFrom=2023-02-04&dateTo=2023-02-05
     *
     * @param metric metric name. Allowed metrics are temperature, wind, humidity, AQI
     * @param dateFrom date (in yyyy-mm-dd format) from which we want to apply the filter
     * @param dateTo date (in yyyy-mm-dd format) up to which we want to apply the filter
     * @param sensorId optional sensor id to filter metrics for the given sensor id
     * @return JSON response containing a list of objects where each object contains the average metric and sensor id
     * @throws Exception
     */
    @GetMapping(value = "/retrieve-average-metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> retrieveAverageMetrics(
            @RequestParam() String metric,
            @RequestParam() String dateFrom,
            @RequestParam() String dateTo,
            @RequestParam(required = false, defaultValue = "all") String sensorId) throws Exception {
        System.out.println(String.format("Got retrieve average metric request: metric: %s, dateFrom: %s, dateTo: %s, sensorId: %s", metric, dateFrom, dateTo, sensorId));
        // First validate the query parameters
        List<Validation> validations = this.validations.validateRetrieveAverageMetrics(metric, dateFrom, dateTo);
        List<Validation> failedValidations = validations.stream().filter(validation -> validation.status.equals(ValidationStatus.FAILURE)).collect(Collectors.toList());
        // If there are failed validations on the query parameters like dates are out of range, send an error response
        if (failedValidations.size() > 0) {
            return ResponseHandler.createResponse(failedValidations, HttpStatus.BAD_REQUEST);
        }

        // call aggregation service which runs the business logic to fetch metrics from the database
        List<Document> outputResults = aggregationService.getAverageMetrics(sensorId, dateFrom, dateTo, metric);
        // call response handler to format the results into a JSON response
        return ResponseHandler.createResponse(outputResults, HttpStatus.OK);
    }

    /**
     * Method for the POST endpoint to report metrics to the server - used by sensors to publish metrics
     * example: POST /report-metrics
     * body = {
     *     "sensorId": "6AE",
     *     "wind": 20,
     *     "humidity": 5
     * }
     *
     * @param data JSON data in string format where sensorId is provided as string and at least 1 valid metric. Allowed metrics are temperature, humidity, wind, AQI
     * @return JSON response that the metric has been stored in the database
     * @throws Exception
     */
    @PostMapping(value = "/report-metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> reportMetrics(
            @RequestBody String data) throws Exception {
        System.out.println(String.format("Got report metrics request: data: %s", data));
        // First validate the request body for e.g. sensorId is required, at least 1 metric must be provided
        List<Validation> validations = this.validations.validateReportMetrics(data);
        List<Validation> failedValidations = validations.stream().filter(validation -> validation.status.equals(ValidationStatus.FAILURE)).collect(Collectors.toList());
        // If there are failed validations on the request body, return an error response
        if (failedValidations.size() > 0) {
            return ResponseHandler.createResponse(failedValidations, HttpStatus.BAD_REQUEST);
        }

        // call the reporting service which has the logic to store the data in the database
        Map<String, Object> result = reportingService.reportMetrics(data);
        // call response handler to format the results into a JSON response
        return ResponseHandler.createResponse(result, (HttpStatus) result.get("status"));
    }
}