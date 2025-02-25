package com.emse.integrativecps2.repository;

import com.emse.integrativecps2.entity.BuildingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface BuildingRuleRepository extends JpaRepository<BuildingRule, Long> {
    List<BuildingRule> findByTriggerTime(LocalTime triggerTime);
}
