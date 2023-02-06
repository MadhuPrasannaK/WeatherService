package org.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import static org.service.Constants.SENSOR_COLLECTION;

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

    @PostMapping(value = "/report-metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> reportMetrics(
            @RequestBody String data) throws Exception {
        System.out.println(String.format("Got report metrics request: data: %s", data));

        Map<String, Object> result = new HashMap<>();
        ResponseEntity<Object> response;

        MongoCollection<Document> collection =
                MongoConnection.getInstance().getDatabase().getCollection(SENSOR_COLLECTION);

        Document sensorData = new Document("_id", new ObjectId());
        sensorData.append("date", String.valueOf(java.time.LocalDate.now()));
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(data);
            Iterator<?> keys = jsonObject.keySet().iterator();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key.equals("sensorId")) {
                    sensorData.append(key, jsonObject.get(key));
                } else {
                    Double value = ((Number) jsonObject.get(key)).doubleValue();
                    sensorData.append(key, value);
                }
            }
        } catch (ClassCastException | ParseException e) {
            String msg = "Error occurred while parsing request body: " + e.getMessage();
            System.out.println(msg);
            result.put("message", msg);
            response = new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            return response;
        }
        try {
            collection.insertOne(sensorData);
            String msg = "Successfully reported metrics";
            result.put("message", msg);
            result.put("data", sensorData.toJson());
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(result);
            response = new ResponseEntity<>(json, HttpStatus.OK);
        } catch (Exception e) {
            String msg = "Failed to insert data: " + e.getMessage();
            result.put("message", msg);
            response = new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response;

    }
}