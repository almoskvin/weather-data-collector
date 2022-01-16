# Weather Data Collector service
Dockerized Spring Boot microservice aimed to collect, store and share weather data from various sensors

Persistence layer represented by Postgres.

Application is built as REST API service using MVC pattern. Application runs on port 8080.

## To do
- Contract testing (PDC)
- Controller exception handler

## Build and run

1) `mvn clean package` : build jar
2) `docker compose up --build -d` : build project docker image and containers and run containers.
    `--build` required only for the first execution.
3) `docker compose down` : completely stop the dockerized services

Application is configured to run on the port 8080. If the service is deployed locally, endpoints will be available at http://localhost:8080/

## Testing [WIP]
Unit tests are in place.
For development purposes, an Intellij HTTP requests file is available in the project at `requests/weather-collector-requests.http`


## Endpoints
### Sensors

#### Register new sensor
`POST /api/v1/weather/sensors`

**Payload**
```json
{
  "sensorId": "4",
  "country": "Russia",
  "city": "Moscow"
}
```
`sensorId` required, others are optional.

**Response**
- Success: 200 OK
- Validation Failure: 400 + error message

---
#### Update sensor
`PUT /api/v1/weather/sensors`

**Payload**
```json
{
  "sensorId": "4",
  "country": "Russia",
  "city": "Moscow"
}
```
`sensorId` required, others are optional.

**Response**
- Success: 200 OK
- Validation Failure: 400 + error message

---
#### Get sensor
`GET /api/v1/weather/sensors/{sensorId}`

**Response**
```json
{
  "sensorId": "4",
  "country": "Russia",
  "city": "Moscow"
}
```

### Metrics

#### Register metrics
`POST /api/v1/weather/metrics`

**Payload**
```json
{
  "sensorId": "1",
  "temperature": "35",
  "humidity": "55",
  "windSpeed": "12",
  "windDirection": "124",
  "pressure": "1014"
}
```
`sensorId` required, others are optional.

**Response**
- Success: 200 OK
- Validation Failure: 400 + error message

---
#### Get metrics
`POST /api/v1/weather/metrics/get`

**Payload**
```json
{
  "sensors": [
    1,
    2
  ],
  "metrics": [
    "TEMPERATURE",
    "PRESSURE"
  ],
  "startDate": "2022-01-03T21:40:00",
  "endDate": "2022-01-03T21:42:30"
}
```
All parameters are optional.

_Note_: if `endDate` is specified without `startDate`, request will return 400. 
Empty `sensors` are considered as request to all sensors. 
Empty `metrics` are considered as request to all metrics.
Empty dates are considered as request to the latest single record for specified sensor.

Response consists of average data for each requested metric.

**Response**
- Success: 200 OK
```json
{
  "metrics": {
    "WINDDIRECTION": 96.0,
    "PRESSURE": 1014.0,
    "WINDSPEED": 10.0,
    "HUMIDITY": 55.0,
    "TEMPERATURE": 29.0
  }
}
```
- Validation Failure: 400 + error message
