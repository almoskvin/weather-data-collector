package com.github.almoskvin;

import com.github.almoskvin.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("api/v1/weather/metrics")
@RequiredArgsConstructor
@Slf4j
public class MetricsController {
    private final WeatherService weatherService;

    @PostMapping
    public ResponseEntity<String> registerMetrics(@RequestBody MetricsRegistrationRequest request) {
        log.info("New metrics registration request {}", request);
        OperationResult<Integer> registrationResult = weatherService.registerMetrics(
                request.sensorId(),
                request.temperature(),
                request.humidity(),
                request.windSpeed(),
                request.windDirection(),
                request.pressure()
        );
        if (registrationResult.getStatus() == OperationResult.Status.Error) {
            log.error(registrationResult.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationResult.getError());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("get")
    public ResponseEntity<? extends BaseResponse> getMetrics(@RequestBody MetricsRequest request) {
        log.info("New metrics information request {}", request);
        OperationResult<HashMap<String, Double>> operationResult = weatherService.getMetrics(
                request.sensors(),
                request.metrics(),
                request.startDate(),
                request.endDate()
        );
        if (operationResult.getStatus() == OperationResult.Status.Error) {
            log.error(operationResult.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(operationResult.getError()));
        }
        MetricsResponse metricsResponse = new MetricsResponse(operationResult.getValue());
        return ResponseEntity.ok().body(metricsResponse);
    }
}
