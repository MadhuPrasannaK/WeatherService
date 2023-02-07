package org.service;

public class Constants {
    static final String SENSOR_COLLECTION = "sensor_data";
    static final String WEATHER_DB = "weather";
    static final String MONGO_CONNECTION_URL = "mongodb://localhost:27017/";
    static final String YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
}

enum Metrics {
    temperature, humidity, wind, AQI
}
