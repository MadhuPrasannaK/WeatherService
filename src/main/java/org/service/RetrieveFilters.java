package org.service;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class RetrieveFilters {
    public static List<Bson> getFilters(
            String sensorId,
            String dateFrom,
            String dateTo
    ) {
        // The list of filters - Data range + Metric type + (optional) SensorId filters to be added
        // before aggregating and returning the data
        List<Bson> filters = new ArrayList<>();

        if (!sensorId.equals("all")) {
            // If a specific sensorId is provided, we filter on that sensorId
            filters.add(Filters.eq("sensorId", sensorId));
        }
        filters.add(Filters.gte("date", dateFrom));
        filters.add(Filters.lte("date", dateTo));
        return filters;
    }
}
