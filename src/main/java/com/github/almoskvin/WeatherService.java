package com.github.almoskvin;

import com.github.almoskvin.model.Metrics;
import com.github.almoskvin.model.Sensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.ToIntFunction;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    private final SensorRepository sensorRepository;
    private final MetricsRepository metricsRepository;

    public OperationResult<Integer> registerSensor(Integer sensorId, String country, String city) {
        if (sensorId == null) {
            return OperationResult.error("Sensor ID is null");
        }
        if (sensorRepository.findOneBySensorId(sensorId) != null) {
            return OperationResult.error("Sensor has already been registered");
        }
        Sensor sensor = Sensor.builder()
                .sensorId(sensorId)
                .country(country)
                .city(city)
                .build();
        sensorRepository.save(sensor);
        return OperationResult.ok(sensor.getSensorId());
    }

    public OperationResult<Integer> updateSensor(Integer sensorId, String country, String city) {
        if (sensorId == null) {
            return OperationResult.error("Sensor ID is null");
        }
        Sensor sensor = sensorRepository.findOneBySensorId(sensorId);
        if (sensor == null) {
            return OperationResult.error("Sensor not found");
        }
        sensor.setCountry(country);
        sensor.setCity(city);
        sensorRepository.save(sensor);
        return OperationResult.ok(sensor.getSensorId());
    }

    public OperationResult<Sensor> getSensor(Integer sensorId) {
        if (sensorId == null) {
            return OperationResult.error("Sensor ID is null");
        }
        Sensor sensor = sensorRepository.findOneBySensorId(sensorId);
        if (sensor == null) {
            return OperationResult.error("Sensor not found");
        }
        return OperationResult.ok(sensor);
    }

    public OperationResult<Integer> registerMetrics(Integer sensorId, Integer temperature, Integer humidity,
                                                    Integer windSpeed, Integer windDirection, Integer pressure) {
        if (sensorId == null) {
            return OperationResult.error("Sensor ID is null");
        }
        Sensor sensor = sensorRepository.findOneBySensorId(sensorId);
        if (sensor == null) {
            return OperationResult.error("Sensor not found");
        }
        Metrics metrics = Metrics.builder()
                .sensor(sensor)
                .temperature(temperature)
                .humidity(humidity)
                .windSpeed(windSpeed)
                .windDirection(windDirection)
                .pressure(pressure)
                .build();
        metricsRepository.save(metrics);
        return OperationResult.ok(metrics.getSensor().getSensorId());
    }

    public OperationResult<HashMap<String, Double>> getMetrics(Set<Integer> sensors, Set<String> metrics, LocalDateTime startDate,
                                                               LocalDateTime endDate) {
        boolean allSensors = false;
        if (sensors == null || sensors.isEmpty()) {
            log.info("Data for all sensors requested");
            allSensors = true;
        }
        if (metrics == null || metrics.isEmpty()) {
            log.info("All metrics requested");
            metrics = Set.of("TEMPERATURE",
                    "PRESSURE",
                    "WINDSPEED",
                    "WINDDIRECTION",
                    "HUMIDITY");
        }
        boolean latestDate = false;
        if (startDate == null && endDate == null) {
            log.info("The latest data requested");
            latestDate = true;
        } else if (startDate == null) {
            return OperationResult.error("Wrong date range. Only end date specified");
        } else if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        // requesting raw data
        List<Metrics> rawData = allSensors && latestDate ? metricsRepository.findAllLatest() :
                allSensors ? metricsRepository.findAllByCreatedAtBetween(startDate, endDate) :
                        latestDate ? metricsRepository.findAllLatestBySensorIdIn(sensors) :
                                metricsRepository.findAllBySensorIdInAndCreatedAtBetween(sensors, startDate, endDate);
        HashMap<String, Double> result = new HashMap<>();
        metrics.forEach(m -> rawData.stream()
                .mapToInt(getFunction(m))
                .average()
                .ifPresent(a -> result.put(m, a))
        );
        return OperationResult.ok(result);
    }

    private ToIntFunction<Metrics> getFunction(String m) {
        return switch (m.toUpperCase(Locale.ROOT)) {
            case "TEMPERATURE" -> Metrics::getTemperature;
            case "PRESSURE" -> Metrics::getPressure;
            case "WINDSPEED" -> Metrics::getWindSpeed;
            case "WINDDIRECTION" -> Metrics::getWindDirection;
            case "HUMIDITY" -> Metrics::getHumidity;
            default -> throw new RuntimeException("Unexpected metrics type");
        };
    }
}
