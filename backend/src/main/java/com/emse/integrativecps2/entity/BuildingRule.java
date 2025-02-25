package com.emse.integrativecps2.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Entity
@Table(name = "building_rules")
@Data
public class BuildingRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String room; // Room number (e.g., 220)
    private LocalTime triggerTime; // Time at which the setpoint should be changed
    private int setpoint; // Setpoint value (1 to 4)
}
