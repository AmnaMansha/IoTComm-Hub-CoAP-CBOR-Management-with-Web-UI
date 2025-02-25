package com.emse.integrativecps2.controller;

import com.emse.integrativecps2.entity.Rule;
import com.emse.integrativecps2.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
        import java.util.List;

@RestController
@RequestMapping("/rules")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @PostMapping
    public Rule addRule(@RequestBody Rule rule) {
        return ruleService.addRule(rule);
    }

    @DeleteMapping("/{id}")
    public String removeRule(@PathVariable Long id) {
        ruleService.removeRule(id);
        return "Rule removed successfully!";
    }

    @GetMapping
    public List<Rule> getRules() {
        return ruleService.getRules();
    }
}
