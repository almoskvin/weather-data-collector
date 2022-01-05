package com.github.almoskvin;

import com.github.almoskvin.model.Metrics;
import com.github.almoskvin.model.Sensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class WeatherServiceTest {

    @Mock
    SensorRepository sensorRepository;
    @Mock
    MetricsRepository metricsRepository;
    @InjectMocks
    WeatherService weatherService;
    @Captor
    ArgumentCaptor<Sensor> sensorCaptor;
    @Captor
    ArgumentCaptor<Metrics> metricsCaptor;

    WeatherServiceTest() {
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void registerSensorOk() {
        OperationResult<Integer> result = weatherService.registerSensor(1, "testCountry", "testCity");
        Mockito.verify(sensorRepository).save(sensorCaptor.capture());
        Sensor sensorToSave = sensorCaptor.getValue();

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals(OperationResult.Status.OK, result.getStatus());

        assertAll("Sensor saving assertion",
                () -> assertEquals(1, sensorToSave.getSensorId()),
                () -> assertEquals("testCountry", sensorToSave.getCountry()),
                () -> assertEquals("testCity", sensorToSave.getCity())
        );
    }

    @Test
    void registerSensorExists() {
        Mockito.doReturn(new Sensor()).when(sensorRepository).findOneBySensorId(1);

        OperationResult<Integer> result = weatherService.registerSensor(1, "testCountry", "testCity");

        assertNotNull(result);
        assertAll("Sensor exists error assertion",
                () -> assertEquals(OperationResult.Status.Error, result.getStatus()),
                () -> assertEquals("Sensor has already been registered", result.getError())
        );
    }

    @Test
    void registerSensorIdNull() {
        OperationResult<Integer> result = weatherService.registerSensor(null, "testCountry", "testCity");

        assertNotNull(result);
        assertAll("Sensor id is null error assertion",
                () -> assertEquals(OperationResult.Status.Error, result.getStatus()),
                () -> assertEquals("Sensor ID is null", result.getError())
        );
    }

    @Test
    void updateSensor() {
        //GIVEN
        Sensor sensorToUpdate = Sensor.builder()
                .sensorId(1)
                .country("country1")
                .city("city1")
                .build();
        Mockito.doReturn(sensorToUpdate).when(sensorRepository).findOneBySensorId(1);
        //WHEN
        OperationResult<Integer> result = weatherService.updateSensor(1, "country2", "city2");
        Mockito.verify(sensorRepository).save(sensorCaptor.capture());
        //THEN
        Sensor updatedSensor = sensorCaptor.getValue();
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals(OperationResult.Status.OK, result.getStatus());

        assertAll("Sensor updating assertion",
                () -> assertEquals(1, updatedSensor.getSensorId()),
                () -> assertEquals("country2", updatedSensor.getCountry()),
                () -> assertEquals("city2", updatedSensor.getCity())
        );
    }

    @Test
    void updateSensorIdNull() {
        OperationResult<Integer> result = weatherService.updateSensor(null, "testCountry", "testCity");

        assertNotNull(result);
        assertAll("Sensor id is null error assertion",
                () -> assertEquals(OperationResult.Status.Error, result.getStatus()),
                () -> assertEquals("Sensor ID is null", result.getError())
        );
    }

    @Test
    void updateSensorDoesNotExist() {
        Mockito.doReturn(null).when(sensorRepository).findOneBySensorId(1);

        OperationResult<Integer> result = weatherService.updateSensor(1, "testCountry", "testCity");

        assertNotNull(result);
        assertAll("Sensor exists error assertion",
                () -> assertEquals(OperationResult.Status.Error, result.getStatus()),
                () -> assertEquals("Sensor not found", result.getError())
        );
    }


    @Test
    void getSensor() {
        //GIVEN
        Sensor testSensor = Sensor.builder()
                .sensorId(1)
                .country("testCountry")
                .city("testCity")
                .build();
        Mockito.doReturn(testSensor).when(sensorRepository).findOneBySensorId(1);
        //WHEN
        OperationResult<Sensor> result = weatherService.getSensor(1);
        //THEN
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals(OperationResult.Status.OK, result.getStatus());

        assertAll("Sensor updating assertion",
                () -> assertEquals(1, result.getValue().getSensorId()),
                () -> assertEquals("testCountry", result.getValue().getCountry()),
                () -> assertEquals("testCity", result.getValue().getCity())
        );
    }

    @Test
    void getSensorIdNull() {
        OperationResult<Sensor> result = weatherService.getSensor(null);

        assertNotNull(result);
        assertAll("Sensor id is null error assertion",
                () -> assertEquals(OperationResult.Status.Error, result.getStatus()),
                () -> assertEquals("Sensor ID is null", result.getError())
        );
    }

    @Test
    void getSensorDoesNotExist() {
        Mockito.doReturn(null).when(sensorRepository).findOneBySensorId(1);

        OperationResult<Sensor> result = weatherService.getSensor(1);

        assertNotNull(result);
        assertAll("Sensor exists error assertion",
                () -> assertEquals(OperationResult.Status.Error, result.getStatus()),
                () -> assertEquals("Sensor not found", result.getError())
        );
    }

    @Test
    void registerMetrics() {
        //GIVEN
        Sensor existingSensor = Sensor.builder()
                .sensorId(1)
                .build();
        Mockito.doReturn(existingSensor).when(sensorRepository).findOneBySensorId(1);
        //WHEN
        OperationResult<Integer> result = weatherService.registerMetrics(1, 1, 1, 1, 1, 1);
        Mockito.verify(metricsRepository).save(metricsCaptor.capture());
        //THEN
        Metrics registeredMetrics = metricsCaptor.getValue();
        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals(OperationResult.Status.OK, result.getStatus());

        assertAll("Metrics saving assertion",
                () -> assertEquals(1, registeredMetrics.getHumidity()),
                () -> assertEquals(1, registeredMetrics.getTemperature()),
                () -> assertEquals(1, registeredMetrics.getPressure()),
                () -> assertNotNull(registeredMetrics.getSensor()),
                () -> assertEquals(1, registeredMetrics.getSensor().getSensorId()),
                () -> assertEquals(1, registeredMetrics.getPressure()),
                () -> assertEquals(1, registeredMetrics.getWindDirection()),
                () -> assertEquals(1, registeredMetrics.getWindSpeed())
        );
    }

    @Test
    void registerMetricsSensorIdNull() {
        OperationResult<Integer> result = weatherService.registerMetrics(null, 1, 1, 1, 1, 1);

        assertNotNull(result);
        assertAll("Sensor id is null error assertion",
                () -> assertEquals(OperationResult.Status.Error, result.getStatus()),
                () -> assertEquals("Sensor ID is null", result.getError())
        );
    }

    @Test
    void registerMetricsSensorDoesNotExist() {
        Mockito.doReturn(null).when(sensorRepository).findOneBySensorId(1);

        OperationResult<Integer> result = weatherService.registerMetrics(1, 1, 1, 1, 1, 1);

        assertNotNull(result);
        assertAll("Sensor exists error assertion",
                () -> assertEquals(OperationResult.Status.Error, result.getStatus()),
                () -> assertEquals("Sensor not found", result.getError())
        );
    }

    @Test
    void getMetricsAllSensorsAndLatestDate() {
        //GIVEN
        List<Metrics> testMetrics = List.of(
                Metrics.builder()
                        .id(1)
                        .temperature(4)
                        .humidity(4)
                        .windDirection(4)
                        .windSpeed(4)
                        .pressure(4)
                        .build(),
                Metrics.builder()
                        .id(2)
                        .temperature(2)
                        .humidity(2)
                        .windDirection(2)
                        .windSpeed(2)
                        .pressure(2)
                        .build()
        );
        Mockito.doReturn(testMetrics).when(metricsRepository).findAllLatest();
        //WHEN
        OperationResult<HashMap<String, Double>> result = weatherService.getMetrics(null, null, null, null);
        //THEN
        Mockito.verify(metricsRepository, Mockito.times(1)).findAllLatest();
        assertNotNull(result);
        HashMap<String, Double> resultMap = result.getValue();
        assertNotNull(resultMap);
        assertEquals(OperationResult.Status.OK, result.getStatus());
        assertEquals(5, resultMap.size());
        assertAll("Metrics avg result assertion",
                () -> assertNotNull(resultMap.get("TEMPERATURE")),
                () -> assertEquals(3, resultMap.get("TEMPERATURE")),
                () -> assertNotNull(resultMap.get("PRESSURE")),
                () -> assertEquals(3, resultMap.get("PRESSURE")),
                () -> assertNotNull(resultMap.get("WINDSPEED")),
                () -> assertEquals(3, resultMap.get("WINDSPEED")),
                () -> assertNotNull(resultMap.get("WINDDIRECTION")),
                () -> assertEquals(3, resultMap.get("WINDDIRECTION")),
                () -> assertNotNull(resultMap.get("HUMIDITY")),
                () -> assertEquals(3, resultMap.get("HUMIDITY"))

        );
    }

    @Test
    void getMetricsAllSensors() {
        //GIVEN
        List<Metrics> testMetrics = List.of(
                Metrics.builder()
                        .id(1)
                        .temperature(4)
                        .build(),
                Metrics.builder()
                        .id(2)
                        .temperature(2)
                        .build()
        );
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, 1, 5, 10, 0);
        Mockito.doReturn(testMetrics).when(metricsRepository).findAllByCreatedAtBetween(startDate, endDate);
        //WHEN
        OperationResult<HashMap<String, Double>> result = weatherService.getMetrics(null, Set.of("TEMPERATURE"), startDate, endDate);
        //THEN
        Mockito.verify(metricsRepository, Mockito.times(1)).findAllByCreatedAtBetween(startDate, endDate);
        assertNotNull(result);
        HashMap<String, Double> resultMap = result.getValue();
        assertNotNull(resultMap);
        assertEquals(OperationResult.Status.OK, result.getStatus());
        assertEquals(1, resultMap.size());
        assertAll("Metrics avg result assertion",
                () -> assertNotNull(resultMap.get("TEMPERATURE")),
                () -> assertEquals(3, resultMap.get("TEMPERATURE"))
        );
    }

    @Test
    void getMetricsLatestDate() {
        //GIVEN
        List<Metrics> testMetrics = List.of(
                Metrics.builder()
                        .id(1)
                        .humidity(4)
                        .build(),
                Metrics.builder()
                        .id(2)
                        .humidity(2)
                        .build()
        );
        Set<Integer> testSensors = Set.of(1, 2);
        Mockito.doReturn(testMetrics).when(metricsRepository).findAllLatestBySensorIdIn(testSensors);
        //WHEN
        OperationResult<HashMap<String, Double>> result = weatherService.getMetrics(testSensors, Set.of("HUMIDITY"), null, null);
        //THEN
        Mockito.verify(metricsRepository, Mockito.times(1)).findAllLatestBySensorIdIn(testSensors);
        assertNotNull(result);
        HashMap<String, Double> resultMap = result.getValue();
        assertNotNull(resultMap);
        assertEquals(OperationResult.Status.OK, result.getStatus());
        assertEquals(1, resultMap.size());
        assertAll("Metrics avg result assertion",
                () -> assertNotNull(resultMap.get("HUMIDITY")),
                () -> assertEquals(3, resultMap.get("HUMIDITY"))
        );
    }

    @Test
    void getMetrics() {
        //GIVEN
        List<Metrics> testMetrics = List.of(
                Metrics.builder()
                        .id(1)
                        .pressure(4)
                        .build(),
                Metrics.builder()
                        .id(2)
                        .pressure(2)
                        .build()
        );
        Set<Integer> testSensors = Set.of(1, 2);
        LocalDateTime startDate = LocalDateTime.of(2022, 1, 1, 10, 0);
        LocalDateTime endDate = LocalDateTime.of(2022, 1, 5, 10, 0);
        Mockito.doReturn(testMetrics).when(metricsRepository).findAllBySensorIdInAndCreatedAtBetween(testSensors, startDate, endDate);
        //WHEN
        OperationResult<HashMap<String, Double>> result = weatherService.getMetrics(testSensors, Set.of("PRESSURE"), startDate, endDate);
        //THEN
        Mockito.verify(metricsRepository, Mockito.times(1)).findAllBySensorIdInAndCreatedAtBetween(testSensors, startDate, endDate);
        assertNotNull(result);
        HashMap<String, Double> resultMap = result.getValue();
        assertNotNull(resultMap);
        assertEquals(OperationResult.Status.OK, result.getStatus());
        assertEquals(1, resultMap.size());
        assertAll("Metrics avg result assertion",
                () -> assertNotNull(resultMap.get("PRESSURE")),
                () -> assertEquals(3, resultMap.get("PRESSURE"))
        );
    }
}