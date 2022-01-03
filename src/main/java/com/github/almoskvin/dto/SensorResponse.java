package com.github.almoskvin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class SensorResponse extends BaseResponse {
    private Integer sensorId;
    private String country;
    private String city;
}
