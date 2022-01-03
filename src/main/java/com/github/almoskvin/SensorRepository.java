package com.github.almoskvin;

import com.github.almoskvin.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Sensor findOneBySensorId(Integer id);
}
