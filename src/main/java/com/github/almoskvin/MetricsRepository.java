package com.github.almoskvin;

import com.github.almoskvin.model.Metrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface MetricsRepository extends JpaRepository<Metrics, Integer> {

    List<Metrics> findAllByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "select distinct on (sensor_id) * " +
            "from metrics " +
            "order by sensor_id, created_at DESC", nativeQuery = true)
    List<Metrics> findAllLatest();

    @Query(value = "select distinct on (s.sensor_id) m.id, s.sensor_id, m.created_at, humidity, temperature, wind_speed, wind_direction, pressure " +
            "from metrics m left join sensor s on m.sensor_id = s.id " +
            "where s.sensor_id in (:ids) " +
            "order by s.sensor_id, m.created_at DESC", nativeQuery = true)
    List<Metrics> findAllLatestBySensorIdIn(@Param("ids") Set<Integer> ids);

    @Query(value = "select m.id, s.sensor_id, m.created_at, humidity, temperature, wind_speed, wind_direction, pressure " +
            "from metrics m left join sensor s on m.sensor_id = s.id " +
            "where s.sensor_id in (:ids) " +
            "and m.created_at between :startDate and :endDate", nativeQuery = true)
    List<Metrics> findAllBySensorIdInAndCreatedAtBetween(@Param("ids") Set<Integer> ids,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);

}
