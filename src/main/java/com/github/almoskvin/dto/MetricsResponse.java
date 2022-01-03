package com.github.almoskvin.dto;

import lombok.*;

import java.util.HashMap;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsResponse extends BaseResponse {
    private HashMap<String, Double> metrics;
}
