package com.github.almoskvin.dto;

public record MetricsRegistrationRequest(
        Integer sensorId,
        Integer temperature,
        Integer humidity,
        Integer windSpeed,
        Integer windDirection,
        Integer pressure) {
}
