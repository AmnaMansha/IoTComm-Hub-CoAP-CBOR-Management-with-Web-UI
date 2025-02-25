package com.emse.integrativecps2.service;

import com.emse.integrativecps2.entity.SensorRule;
import com.emse.integrativecps2.entity.SensorData;
import com.emse.integrativecps2.repository.SensorRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SensorRuleService {

    @Autowired
    private SensorRuleRepository sensorRuleRepository;

    @Autowired
    private CoapClientService coapClientService; // Inject CoAP Service

    public SensorRule addSensorRule(SensorRule rule) {
        return sensorRuleRepository.save(rule);
    }

    public List<SensorRule> getAllSensorRules() {
        return sensorRuleRepository.findAll();
    }

    // Delete a sensor rule by ID
    public void deleteSensorRule(Long id) {
        sensorRuleRepository.deleteById(id);
    }

    // Update an existing sensor rule
    public SensorRule updateSensorRule(Long id, SensorRule updatedRule) {
        return sensorRuleRepository.findById(id).map(rule -> {
            rule.setTriggerType(updatedRule.getTriggerType());
            rule.setCondition(updatedRule.getCondition());
            rule.setThreshold(updatedRule.getThreshold());
            rule.setEmail(updatedRule.getEmail());
            return sensorRuleRepository.save(rule);
        }).orElseThrow(() -> new RuntimeException("Sensor rule not found with id " + id));
    }
    public void evaluateSensorData(SensorData sensorData) {
        List<SensorRule> rules = sensorRuleRepository.findAll();
        for (SensorRule rule : rules) {
            if (isConditionMet(rule, sensorData)) {
                executeAction("LED_BLUE");  // Change LED to blue if rule triggers
            }
        }
    }

    private boolean isConditionMet(SensorRule rule, SensorData sensorData) {
        double value = switch (rule.getTriggerType()) {
            case "temperature" -> sensorData.getTemperature();
            case "humidity" -> sensorData.getHumidity();
            default -> -1;
        };

        return switch (rule.getCondition()) {
            case ">" -> value > rule.getThreshold();
            case "<" -> value < rule.getThreshold();
            case "==" -> value == rule.getThreshold();
            default -> false;
        };
    }

    private void executeAction(String action) {
        System.out.println("Executing Action: " + action);
        coapClientService.put("rgb", "{r:0,g:0,b:255}");  // Change LED color to blue
    }
}

