package org.service;

import com.mongodb.MongoClient;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class RetrieveFiltersTest {

    @Test
    public void getFilters() {
        List<Bson> filters = RetrieveFilters.getFilters("1A", "2023-02-05", "2023-02-06");
        List<BsonDocument> bsonDocs = filters.stream().map(filter -> filter.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry())).collect(Collectors.toList());
        assertEquals(bsonDocs.get(0).get("sensorId").toString(), "BsonString{value='1A'}");
        assertEquals(bsonDocs.get(1).get("date").toString(), "{\"$gte\": \"2023-02-05\"}");
        assertEquals(bsonDocs.get(2).get("date").toString(), "{\"$lte\": \"2023-02-06\"}");
    }
}
