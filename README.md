# WeatherService
APIs of a weather service app

## Installation/Setup Instructions

1. Clone this git repo.
2. Open this project in IntelliJ or the editor of your choice. If you are using IntelliJ, click on “open project from existing sources” and select the pom.xml file so that IntelliJ will automatically load it up as a maven project. We recommend that you use Java 19 for the project.
3. The app uses MongoDB as the database. Please install MongoDB locally using one of these links – 
For Mac - https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/
For Windows - https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-windows/
4. After installing MongoDB, please run mongodb locally as per the instructions in the above links.
5. Back in the application code, run Application.java. It uses Spring Boot to spin up a REST web server with 2 URIs. In the logs, you will see a statement like “Started Application in …”
6. The API will be available at http://localhost:8080 and you can use [Postman](https://www.postman.com/) to query the API

## Assumptions:

1. We are provided with a list of valid metrics that the system expects. This list can grow over time by simply adding new metrics to the “Metrics” enum in the code. Since this list is scalable, we will be able to add to this any time new metrics are being introduced into the system.
2. Each sensor will report values for at least 1 metric.
3. I have assumed that the data is at the date level and we’re checking for the date range being inclusive in nature on both sides, i.e. (dateFrom, dateTo) both inclusive.

## URIs

### POST: http://localhost:8080/report-metrics

Body:
```json
{
	sensorId: <required, String datatype>,
	temperate: <optional, Double datatype>,
	wind: <optional, Double datatype>,
	humidity: <optional, Double datatype>,
	AQI: <optional, Double datatype>

}
```

This URI is used for reporting metrics by sensors to the weather api. The app reads the request body, runs it through a bunch of validations, creates a MongoDB document and writes it to a mongo collection called “weather”.

#### Examples :
POST http://localhost:8080/report-metrics
Body:
```json
{
   "sensorId": "101",
   "temperature": 10,
   "wind": 20
}
```


If the request is successful, the response is a json which displays the database record that is inserted along with a success message

```json
{
   "data": "{\"_id\": {\"$oid\": \"63e1be907ae0d674e85a4764\"}, \"date\": \"2023-02-06\", \"temperature\": 10.0, \"sensorId\": \"101\", \"wind\": 20.0}",
   "message": "Successfully reported metrics"
}
```

A bunch of validations are run on the request. If there is a validation failure, the response is a helpful error message. Following validations are run: 1. Request body should be a valid json 2. sensorId has to be provided 3. At least 1 metric should be provided

For e.g. if the request is 

POST http://localhost:8080/report-metrics with body  = {}

Then the response is 
```json
[
   {
       "message": "sensorId is required",
       "status": "FAILURE"
   },
   {
       "message": "Provide at least 1 valid metric from [temperature, humidity, wind, AQI]",
       "status": "FAILURE"
   }
]
```

### GET: http://localhost:8080/retrieve-average-metrics

Query parameters:
```json
dateFrom: <required, String in YYYY-mm-DD format>
dateTo: <required, String in YYYY-mm-DD format>
metric: <required, String - one of temperature, humidity, wind, AQI>
sensorId: <optional, String>
```

This request fetches results as per the query parameters provided. If sensorId is provided along with the metric name, it will return average of the metrics published between provided dates for the given sensor else it returns the average metric for all sensors 

#### Examples:
With sensorId filter:

GET http://localhost:8080/retrieve-average-metrics?metric=temperature&dateFrom=2023-02-04&dateTo=2023-02-05&sensorId=10

Response:
```json
[
   {
       "sensorId": "10",
       "temperatureAverage": 1.6666666666666667
   }
]
```

Without sensorId (all sensors): GET http://localhost:8080/retrieve-average-metrics?metric=wind&dateFrom=2023-02-04&dateTo=2023-02-05

Response:
```json
[
   {
       "sensorId": "2",
       "windAverage": 160.0
   },
   {
       "sensorId": "10",
       "windAverage": 488.6666666666667
   },
   {
       "sensorId": "abcde123",
       "windAverage": 175.0
   }
]
```

The get retrieve average metrics runs some basic validations too like to ensure dateFrom and dateTo are in range. If they are out of range, an error is returned

e.g. GET http://localhost:8080/retrieve-average-metrics?metric=wind&dateFrom=2023-02-06&dateTo=2023-02-05

```json
[
    {
        "message": "Provided dates are out of range",
        "status": "FAILURE"
    }
]
```
