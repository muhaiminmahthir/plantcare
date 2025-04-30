package com.projectfyp.plantcare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "Aloe Vera"

    private int moistureLevel; // current sensor reading

    private boolean watered; // was it watered recently?

    private String healthStatus; // e.g., "healthy", "dry", "diseased"

    private LocalDateTime lastWateredTime;

    public Plant() {}

    public Plant(String name, int moistureLevel, boolean watered, String healthStatus, LocalDateTime lastWateredTime) {
        this.name = name;
        this.moistureLevel = moistureLevel;
        this.watered = watered;
        this.healthStatus = healthStatus;
        this.lastWateredTime = lastWateredTime;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoistureLevel() {
        return moistureLevel;
    }

    public void setMoistureLevel(int moistureLevel) {
        this.moistureLevel = moistureLevel;
    }

    public boolean isWatered() {
        return watered;
    }

    public void setWatered(boolean watered) {
        this.watered = watered;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public LocalDateTime getLastWateredTime() {
        return lastWateredTime;
    }

    public void setLastWateredTime(LocalDateTime lastWateredTime) {
        this.lastWateredTime = lastWateredTime;
    }
}
