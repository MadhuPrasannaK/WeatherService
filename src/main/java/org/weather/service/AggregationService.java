package org.weather.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.weather.storage.MongoConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.weather.constants.Constants.SENSOR_COLLECTION;

/**
 * Aggregation Service that contains business logic to fetch average metrics based on the filters provided
 */
public class AggregationService {
    MongoCollection<Document> collection =
            MongoConnection.getInstance().getDatabase().getCollection(SENSOR_COLLECTION);

    /**
     * Get average metrics by querying the database
     * @param sensorId sensor id
     * @param dateFrom date from which we want to get metrics from
     * @param dateTo date up to which we want to get metrics from
     * @param metric metric name e.g. wind, humidity, temperature, AQI
     * @return list of documents that match the provided parameters
     */
    public List<Document> getAverageMetrics(String sensorId, String dateFrom, String dateTo, String metric) {
        // Get filters to apply
        List<Bson> filters = RetrieveFilters.getFilters(sensorId, dateFrom, dateTo);
        List<Document> outputResults = new ArrayList<>();

        String outputFieldName = String.format("%sAverage", metric);
        String expression = String.format("$%s", metric);
        // aggregate logic for querying average metrics
        outputResults = collection.aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.and(filters)),
                        // group by sensorId and calculate average metric
                        Aggregates.group("$sensorId", Accumulators.avg(outputFieldName, expression)),
                        Aggregates.project(
                                // projections are used to select specific fields with aliases and exclude certain fields
                                Projections.fields(
                                        Projections.computed("sensorId", "$_id"),
                                        Projections.computed(outputFieldName, "$" + outputFieldName),
                                        Projections.excludeId()
                                )
                        )
                )
        ).into(outputResults);

        return outputResults;
    }
}