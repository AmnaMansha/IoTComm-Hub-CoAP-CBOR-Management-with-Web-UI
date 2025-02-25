package com.emse.integrativecps2.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rules")
@Data
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String triggerType;  // e.g., "temperature"
    private String condition;    // e.g., ">", "<"
    private double threshold;    // Value to compare against
    private String action;
    private String actionType;   // e.g., "LED_CHANGE"
    private String actionPayload; // JSON string, e.g., "{r:0,g:0,b:255}"
}

