package com.emse.integrativecps2.controller;

import com.emse.integrativecps2.entity.BuildingRule;
import com.emse.integrativecps2.repository.BuildingRuleRepository;
import com.emse.integrativecps2.service.BuildingAutomationService;
import com.emse.integrativecps2.service.MqttPublisherService;
import com.emse.integrativecps2.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/building/rules")
public class BuildingRuleController {

    @Autowired
    private BuildingRuleRepository ruleRepository;

    @Autowired
    private MqttPublisherService mqttPublisherService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BuildingAutomationService automationService;

    @PostMapping
    public BuildingRule createRule(@RequestBody BuildingRule rule) {
        return ruleRepository.save(rule);
    }

    @GetMapping
    public List<BuildingRule> getAllRules() {
        return ruleRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public String deleteRule(@PathVariable Long id) {
        ruleRepository.deleteById(id);
        return "Rule deleted successfully!";
    }

    @PostMapping("/setpoint")
    public ResponseEntity<String> setRoomTemperature(@RequestParam String room, @RequestParam int setpoint) {
        String topic = "emse/fayol/2ET/" + room + "/temperature/setpoint";
        mqttPublisherService.publishMessage(topic, String.valueOf(setpoint));
        return ResponseEntity.ok("✅ Setpoint command sent to Room: " + room + " -> " + setpoint);
    }
//    @PostMapping("/setpoint")
//    public ResponseEntity<String> setRoomTemperature(@RequestParam String room,
//                                                     @RequestParam int setpoint,
//                                                     @RequestParam String email) {
//        // Publish MQTT message
//        String topic = "emse/fayol/2ET/" + room + "/temperature/setpoint";
//        mqttPublisherService.publishMessage(topic, String.valueOf(setpoint));
//
//        // Send Email Notification
//        String subject = "Setpoint Updated for Room " + room;
//        String message = "The temperature setpoint for Room " + room + " has been updated to " + setpoint + ".";
//        notificationService.sendEmail(email, subject, message);
//
//        return ResponseEntity.ok("✅ Setpoint command sent to Room: " + room + " -> " + setpoint + ". Email sent to " + email);
//    }

}
