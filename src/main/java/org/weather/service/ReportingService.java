package org.weather.service;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.weather.storage.MongoConnection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.weather.constants.Constants.SENSOR_COLLECTION;


/**
 * Reporting Service that contains business logic to parse the metrics sent by sensor and store them in the database
 */
public class ReportingService {
    // Initialize mongoDB connection
    MongoCollection<Document> collection =
            MongoConnection.getInstance().getDatabase().getCollection(SENSOR_COLLECTION);

    /**
     * Reports metrics by reading the stringified json data and store it in database. It returns a helpful error response if the data is not if correct format
     * @param data stringified json input containing metrics and sensorId
     * @return map of key and value where value is the data representation from database or error message
     */
    public Map<String, Object> reportMetrics(String data) {
        Map<String, Object> result = new HashMap<>();

        // create a document object
        Document sensorData = new Document("_id", new ObjectId());
        // add a timestamp
        sensorData.append("date", String.valueOf(java.time.LocalDate.now()));
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(data);
            Iterator<?> keys = jsonObject.keySet().iterator();

            while(keys.hasNext()) {
                String key = (String) keys.next();
                // since sensorId is string, append it as is
                if (key.equals("sensorId")) {
                    sensorData.append(key, jsonObject.get(key));
                } else {
                    // it will be treated as a metric which is read as double value
                    Double value = ((Number) jsonObject.get(key)).doubleValue();
                    sensorData.append(key, value);
                }
            }
        } catch (ClassCastException | ParseException e) {
            String msg = "Error occurred while parsing request body: " + e.getMessage();
            System.out.println(msg);
            result.put("message", msg);
            result.put("status", HttpStatus.BAD_REQUEST);
            return result;
        }
        try {
            // If the data is of the correct format, insert the document i.e. record the metrics
            collection.insertOne(sensorData);
            String msg = "Successfully reported metrics";
            result.put("message", msg);
            result.put("data", sensorData.toJson());
            result.put("status", HttpStatus.OK);
        } catch (Exception e) {
            // If there is database error, send the error response back
            String msg = "Failed to insert data: " + e.getMessage();
            result.put("message", msg);
            result.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
            return result;
        }
        return result;
    }

}