package com.emse.integrativecps2.controller;

import com.emse.integrativecps2.entity.SensorRule;
import com.emse.integrativecps2.service.SensorRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensor/rules")
public class SensorRuleController {

    @Autowired
    private SensorRuleService sensorRuleService;

    @PostMapping
    public String addSensorRule(@RequestBody SensorRule rule) {
        sensorRuleService.addSensorRule(rule);
        return "Sensor rule saved successfully!";
    }

    @GetMapping
    public List<SensorRule> getAllSensorRules() {
        return sensorRuleService.getAllSensorRules();
    }

    // Delete a sensor rule by ID
    @DeleteMapping("/{id}")
    public String deleteSensorRule(@PathVariable Long id) {
        sensorRuleService.deleteSensorRule(id);
        return "Sensor rule deleted successfully!";
    }

    // Update an existing sensor rule
    @PutMapping("/{id}")
    public SensorRule updateSensorRule(@PathVariable Long id, @RequestBody SensorRule updatedRule) {
        return sensorRuleService.updateSensorRule(id, updatedRule);
    }


}
