package org.service;

import org.junit.Test;

import static org.service.Constants.WEATHER_DB;
import static org.junit.Assert.*;

public class MongoConnectionTest {

    @Test
    public void getInstance() {
        MongoConnection mongoConnection = MongoConnection.getInstance();
        assertNotNull(mongoConnection);
        MongoConnection anotherMongoConnection = MongoConnection.getInstance();
        assertEquals(mongoConnection, anotherMongoConnection);
    }

    @Test
    public void getDatabase() {
        MongoConnection mongoConnection = MongoConnection.getInstance();
        assertEquals(mongoConnection.getDatabase().getName(), WEATHER_DB);
    }
}
