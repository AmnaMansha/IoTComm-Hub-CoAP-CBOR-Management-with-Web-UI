package com.emse.integrativecps2.repository;

import com.emse.integrativecps2.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findBySensorType(String sensorType);
    List<SensorData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<SensorData> findByLocation(String location);
}