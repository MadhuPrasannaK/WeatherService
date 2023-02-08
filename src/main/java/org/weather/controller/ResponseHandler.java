package org.weather.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class that converts a POJO (plain old java object) object into a json http response
 */
public class ResponseHandler {
    /**
     * Creates a response in json to be returned to the client
     * @param outputResults object that contains the results to return to the client
     * @param httpStatus HTTP status of the response. e.g. OK, BAD_REQUEST etc.
     * @return Response entity object that wraps the json response
     * @throws JsonProcessingException
     */
    public static ResponseEntity<Object> createResponse(Object outputResults, HttpStatus httpStatus) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(outputResults);

        return new ResponseEntity<>(json, httpStatus);
    }
}