package org.service;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import static org.service.Constants.MONGO_CONNECTION_URL;
import static org.service.Constants.WEATHER_DB;

public class MongoConnection {
    private static MongoConnection mongoConnection = null;

    public static MongoConnection getInstance() {

        if (mongoConnection == null) {
            mongoConnection = new MongoConnection();
        }

        return mongoConnection;
    }

    private MongoClient mongoClient = null;

    private MongoConnection() {
        ConnectionString connectionString = new ConnectionString(MONGO_CONNECTION_URL);
        mongoClient = MongoClients.create(connectionString);
    }

    public MongoDatabase getDatabase() {
        return mongoClient.getDatabase(WEATHER_DB);
    }
}
