package org.weather.controller;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ResponseHandlerTest {

    @Test
    public void createResponse() throws Exception {
        Map<String, String> output = new HashMap<>();
        output.put("a", "hi");
        ResponseEntity responseEntity = ResponseHandler.createResponse(output, HttpStatus.OK);
        assertEquals(responseEntity.getBody(), "{\"a\":\"hi\"}");
    }
}