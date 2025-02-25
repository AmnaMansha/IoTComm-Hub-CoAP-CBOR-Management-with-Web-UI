package com.emse.integrativecps2.service;

import com.emse.integrativecps2.entity.Rule;
import com.emse.integrativecps2.entity.SensorData;
import com.emse.integrativecps2.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private CoapClientService coapClientService;  // Inject CoAP Service

    public Rule addRule(Rule rule) {
        return ruleRepository.save(rule);
    }

    public void removeRule(Long ruleId) {
        ruleRepository.deleteById(ruleId);
    }

    public List<Rule> getRules() {
        return ruleRepository.findAll();
    }

    public void evaluateRules(SensorData sensorData) {
        List<Rule> rules = ruleRepository.findAll();
        for (Rule rule : rules) {
            if (isConditionMet(rule, sensorData)) {
                executeAction(rule.getAction());
            }
        }
    }

    private boolean isConditionMet(Rule rule, SensorData sensorData) {
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
        if (action.startsWith("LED")) {
            changeLED(action);
        }
    }

    private void changeLED(String action) {
        String payload = getLEDPayload(action);
        if (payload != null) {
            boolean success = coapClientService.put("rgb", payload);
            if (success) {
                System.out.println("✅ LED state updated: " + payload);
            } else {
                System.out.println("❌ Failed to update LED state.");
            }
        }
    }

    private String getLEDPayload(String action) {
        return switch (action) {
            case "LED_BLUE" -> "{r:0,g:0,b:255}";
            case "LED_RED" -> "{r:255,g:0,b:0}";
            case "LED_GREEN" -> "{r:0,g:255,b:0}";
            case "LED_OFF" -> "{r:0,g:0,b:0}";
            default -> null;
        };
    }
}
