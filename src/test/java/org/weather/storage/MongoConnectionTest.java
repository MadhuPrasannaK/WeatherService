package org.weather.storage;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.weather.constants.Constants.WEATHER_DB;

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