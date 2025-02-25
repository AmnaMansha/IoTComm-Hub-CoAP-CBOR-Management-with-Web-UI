package com.emse.integrativecps2.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class SensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String sensorType;
    private Double sensor_value;
    private String unit;
    private String location;
    private String status;
    private Double temperature;
    private Double humidity;
//    private double airQuality;
}