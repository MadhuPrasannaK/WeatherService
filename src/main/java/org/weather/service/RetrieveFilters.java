package org.weather.service;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to define how to create filters for mongo db's query
 */
public class RetrieveFilters {
    /**
     * Gets a list of Bson objects that represent filters in mongo db query
     * @param sensorId sensor id
     * @param dateFrom date from
     * @param dateTo date to
     * @return list of bson objects that define the filters to apply for the query
     */
    public static List<Bson> getFilters(
            String sensorId,
            String dateFrom,
            String dateTo
    ) {
        List<Bson> filters = new ArrayList<>();

        if (!sensorId.equals("all")) {
            // If a specific sensorId is provided, filter on that sensorId
            filters.add(Filters.eq("sensorId", sensorId));
        }
        filters.add(Filters.gte("date", dateFrom));
        filters.add(Filters.lte("date", dateTo));
        return filters;
    }
}