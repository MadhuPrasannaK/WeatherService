package org.weather.service;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.weather.storage.MongoConnection;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class ReportingServiceTest {

    @Test
    public void reportingServiceReportMetrics() {
        try (MockedStatic<MongoConnection> mockedStatic = Mockito.mockStatic(MongoConnection.class)) {
            MongoConnection mongoConnectionObj = mock(MongoConnection.class, RETURNS_DEEP_STUBS);
            MongoCollection<Document> mongoCollection = mock(MongoCollection.class, RETURNS_DEEP_STUBS);
            when(mongoConnectionObj.getDatabase().getCollection(anyString())).thenReturn(mongoCollection);
            mockedStatic.when(MongoConnection::getInstance).thenReturn(mongoConnectionObj);

            ReportingService reportingService = new ReportingService();
            String data = "{ \"sensorId\": \"1abc\", \"temperature\": 75, \"humidity\": 45.8 }";
            reportingService.reportMetrics(data);

            ArgumentCaptor<Document> argumentCaptor = ArgumentCaptor.forClass(Document.class);
            verify(mongoCollection).insertOne(argumentCaptor.capture());

            Document capturedArgument = argumentCaptor.getValue();
            assertEquals(capturedArgument.get("temperature"), 75.0);
            assertEquals(capturedArgument.get("sensorId"), "1abc");
            assertEquals(capturedArgument.get("humidity"), 45.8);
        }
    }

    @Test
    public void testReportMetricsWithBadInput() {
        try (MockedStatic<MongoConnection> mockedStatic = Mockito.mockStatic(MongoConnection.class)) {
            MongoConnection mongoConnectionObj = mock(MongoConnection.class, RETURNS_DEEP_STUBS);
            MongoCollection<Document> mongoCollection = mock(MongoCollection.class, RETURNS_DEEP_STUBS);
            when(mongoConnectionObj.getDatabase().getCollection(anyString())).thenReturn(mongoCollection);
            mockedStatic.when(MongoConnection::getInstance).thenReturn(mongoConnectionObj);

            ReportingService reportingService = new ReportingService();
            String data = "{ \"sensorId\": \"1abc\", \"temperature\": \"hello\", \"humidity\": 45.8 }";
            Map<String, Object> result = reportingService.reportMetrics(data);

            verify(mongoCollection, times(0)).insertOne(any());
            String expected = "Error occurred while parsing request body: class java.lang.String cannot be cast to class java.lang.Number";
            assertTrue(result.get("message").toString().contains(expected));
        }
    }

}