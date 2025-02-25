package com.emse.integrativecps2.controller;

import com.emse.integrativecps2.entity.WeatherRule;
import com.emse.integrativecps2.service.WeatherRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/weather/rules")
public class WeatherRuleController {

    @Autowired
    private WeatherRuleService weatherRuleService;

    @PostMapping
    public String addWeatherRule(@RequestBody WeatherRule rule) {
        weatherRuleService.addWeatherRule(rule);
        return "Weather rule saved successfully!";
    }

    @GetMapping
    public List<WeatherRule> getAllWeatherRules() {
        return weatherRuleService.getAllWeatherRules();
    }

    // ✅ Edit (Update) Weather Rule
    @PutMapping("/{id}")
    public String updateWeatherRule(@PathVariable Long id, @RequestBody WeatherRule updatedRule) {
        Optional<WeatherRule> existingRule = weatherRuleService.getWeatherRuleById(id);
        if (existingRule.isPresent()) {
            WeatherRule rule = existingRule.get();
            rule.setEmail(updatedRule.getEmail());
            rule.setTriggerType(updatedRule.getTriggerType());
            rule.setCondition(updatedRule.getCondition());
            rule.setThreshold(updatedRule.getThreshold());
            weatherRuleService.addWeatherRule(rule);
            return "✅ Weather rule updated successfully!";
        } else {
            return "❌ Weather rule not found!";
        }
    }

    // ✅ Delete Weather Rule
    @DeleteMapping("/{id}")
    public String deleteWeatherRule(@PathVariable Long id) {
        if (weatherRuleService.deleteWeatherRule(id)) {
            return "✅ Weather rule deleted successfully!";
        } else {
            return "❌ Weather rule not found!";
        }
    }
}
