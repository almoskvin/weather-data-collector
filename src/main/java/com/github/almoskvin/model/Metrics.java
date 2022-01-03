package com.github.almoskvin.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Metrics {

    @Id
    @SequenceGenerator(
            name = "sensor_id_sequence",
            sequenceName = "sensor_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sensor_id_sequence"
    )
    private Integer id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    //degrees
    private Integer temperature;

    //percent
    private Integer humidity;

    //degrees
    private Integer windDirection;

    //km/h
    private Integer windSpeed;

    //hPa
    private Integer pressure;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Metrics metrics = (Metrics) o;
        return id != null && Objects.equals(id, metrics.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
