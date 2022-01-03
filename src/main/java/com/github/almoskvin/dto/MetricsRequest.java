package com.github.almoskvin.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record MetricsRequest(
        Set<Integer> sensors,
        Set<String> metrics,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
