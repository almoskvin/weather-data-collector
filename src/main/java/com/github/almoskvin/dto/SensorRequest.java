package com.github.almoskvin.dto;

public record SensorRequest (
        Integer sensorId,
        String country,
        String city) {
}
