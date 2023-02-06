package org.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class RestServer {

    @GetMapping(value = "/retrieve-average-metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> retrieveAverageMetrics(
            @RequestParam() String metric,
            @RequestParam() String dateFrom,
            @RequestParam() String dateTo,
            @RequestParam(required = false, defaultValue = "all") String sensorId) throws Exception {
        System.out.println(String.format("Got retrieve average metric request: metric: %s, dateFrom: %s, dateTo: %s, sensorId: %s", metric, dateFrom, dateTo, sensorId));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString("Hello");

        return new ResponseEntity<>(json, HttpStatus.OK);
    }

}