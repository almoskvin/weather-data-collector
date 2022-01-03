package com.github.almoskvin;

import com.github.almoskvin.dto.BaseResponse;
import com.github.almoskvin.dto.ErrorResponse;
import com.github.almoskvin.dto.SensorRequest;
import com.github.almoskvin.dto.SensorResponse;
import com.github.almoskvin.model.Sensor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/weather/sensors")
@RequiredArgsConstructor
@Slf4j
public class SensorController {
    private final WeatherService weatherService;

    @PostMapping
    public ResponseEntity<String> registerSensor(@RequestBody SensorRequest request) {
        log.info("New sensor registration request {}", request);
        OperationResult<Integer> registrationResult = weatherService.registerSensor(request.sensorId(), request.country(), request.city());
        if (registrationResult.getStatus() == OperationResult.Status.Error) {
            log.error(registrationResult.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationResult.getError());
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<String> updateSensor(@RequestBody SensorRequest request) {
        log.info("New sensor update request {}", request);
        OperationResult<Integer> registrationResult = weatherService.updateSensor(request.sensorId(), request.country(), request.city());
        if (registrationResult.getStatus() == OperationResult.Status.Error) {
            log.error(registrationResult.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(registrationResult.getError());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("{sensorId}")
    public ResponseEntity<? extends BaseResponse> getSensor(@PathVariable Integer sensorId) {
        log.info("New sensor information request {}", sensorId);
        OperationResult<Sensor> operationResult = weatherService.getSensor(sensorId);
        if (operationResult.getStatus() == OperationResult.Status.Error) {
            log.error(operationResult.getError());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(operationResult.getError()));
        }
        SensorResponse sensorResponse = SensorResponse.builder()
                .sensorId(operationResult.getValue().getSensorId())
                .country(operationResult.getValue().getCountry())
                .city(operationResult.getValue().getCity())
                .build();
        return ResponseEntity.ok().body(sensorResponse);
    }
}
