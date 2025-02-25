package com.emse.integrativecps2.controller;

import com.emse.integrativecps2.entity.SensorData;
import com.emse.integrativecps2.service.SensorDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sensor-data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SensorDataController {
    
    private final SensorDataService sensorDataService;


    
    @PostMapping
    public ResponseEntity<SensorData> createSensorData(@RequestBody SensorData sensorData) {
        return ResponseEntity.ok(sensorDataService.saveSensorData(sensorData));
    }
    
    @GetMapping
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        return ResponseEntity.ok(sensorDataService.getAllSensorData());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SensorData> getSensorDataById(@PathVariable Long id) {
        return ResponseEntity.ok(sensorDataService.getSensorDataById(id));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<SensorData>> getSensorDataByType(@PathVariable String type) {
        return ResponseEntity.ok(sensorDataService.getSensorDataByType(type));
    }
    
    @GetMapping("/location/{location}")
    public ResponseEntity<List<SensorData>> getSensorDataByLocation(@PathVariable String location) {
        return ResponseEntity.ok(sensorDataService.getSensorDataByLocation(location));
    }
    
    @GetMapping("/time-range")
    public ResponseEntity<List<SensorData>> getSensorDataByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(sensorDataService.getSensorDataByTimeRange(start, end));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensorData(@PathVariable Long id) {
        sensorDataService.deleteSensorData(id);
        return ResponseEntity.ok().build();
    }
}