package com.emse.integrativecps2.service;

import com.emse.integrativecps2.entity.BuildingRule;
import com.emse.integrativecps2.repository.BuildingRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BuildingRuleService {

    @Autowired
    private BuildingRuleRepository buildingRuleRepository;

    public void addBuildingRule(BuildingRule rule) {
        buildingRuleRepository.save(rule);
    }

    public List<BuildingRule> getAllBuildingRules() {
        return buildingRuleRepository.findAll();
    }

    public Optional<BuildingRule> getBuildingRuleById(Long id) {
        return buildingRuleRepository.findById(id);
    }

    public boolean deleteBuildingRule(Long id) {
        if (buildingRuleRepository.existsById(id)) {
            buildingRuleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
