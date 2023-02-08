package org.weather.controller;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.weather.storage.MongoConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RestServerTest {

    @Test
    public void retrieveAverageMetrics() throws Exception {
        try (MockedStatic<MongoConnection> mockedStatic = Mockito.mockStatic(MongoConnection.class)) {
            MongoConnection mongoConnectionObj = mock(MongoConnection.class, RETURNS_DEEP_STUBS);
            MongoCollection<Document> mongoCollection = mock(MongoCollection.class, RETURNS_DEEP_STUBS);
            when(mongoConnectionObj.getDatabase().getCollection(anyString())).thenReturn(mongoCollection);
            mockedStatic.when(MongoConnection::getInstance).thenReturn(mongoConnectionObj);

            List<Document> outputResults = new ArrayList<>();
            Map<String, Object> docObject = new HashMap<>();
            docObject.put("sensorId", "1A");
            docObject.put("temperature", 100.1);
            Document doc = new Document("_id", docObject);
            outputResults.add(doc);

            AggregateIterable aggregateIterable = mock(AggregateIterable.class, RETURNS_DEEP_STUBS);
            when(aggregateIterable.into(anyList())).thenReturn(outputResults);
            when(mongoCollection.aggregate(anyList())).thenReturn(aggregateIterable);

            RestServer server = new RestServer();
            ResponseEntity responseEntity = server.retrieveAverageMetrics(
                    "temperature","2023-02-05", "2023-02-06", "all"
            );
            assertEquals(responseEntity.getBody(), "[{\"_id\":{\"temperature\":100.1,\"sensorId\":\"1A\"}}]");
        }
    }

    @Test
    public void reportMetrics() throws Exception {
        try (MockedStatic<MongoConnection> mockedStatic = Mockito.mockStatic(MongoConnection.class)) {
            MongoConnection mongoConnectionObj = mock(MongoConnection.class, RETURNS_DEEP_STUBS);
            MongoCollection<Document> mongoCollection = mock(MongoCollection.class, RETURNS_DEEP_STUBS);
            when(mongoConnectionObj.getDatabase().getCollection(anyString())).thenReturn(mongoCollection);
            mockedStatic.when(MongoConnection::getInstance).thenReturn(mongoConnectionObj);

            RestServer server = new RestServer();
            String data = "{ \"sensorId\": \"1abc\", \"temperature\": 75, \"humidity\": 45.8 }";
            server.reportMetrics(data);

            ArgumentCaptor<Document> argumentCaptor = ArgumentCaptor.forClass(Document.class);
            verify(mongoCollection).insertOne(argumentCaptor.capture());

            Document capturedArgument = argumentCaptor.getValue();

            assertEquals(capturedArgument.get("temperature"), 75.0);
            assertEquals(capturedArgument.get("sensorId"), "1abc");
            assertEquals(capturedArgument.get("humidity"), 45.8);
        }
    }

}