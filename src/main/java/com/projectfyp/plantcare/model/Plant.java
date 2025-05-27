package com.projectfyp.plantcare.model;

import jakarta.persistence.*;

@Entity // This annotation marks the class as a JPA entity
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer moistureThreshold;
    private Boolean isWateringEnabled;
    private Integer moistureLevel;

    // Getters and setters
    public Long getId() { return id;}
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getMoistureThreshold() { return moistureThreshold; }
    public void setMoistureThreshold(Integer moistureThreshold) { this.moistureThreshold = moistureThreshold; }

    public Boolean getIsWateringEnabled() { return isWateringEnabled; }
    public void setIsWateringEnabled(Boolean isWateringEnabled) { this.isWateringEnabled = isWateringEnabled; }

    public Integer getMoistureLevel() { return moistureLevel; }
    public void setMoistureLevel(Integer moistureLevel) { this.moistureLevel = moistureLevel; }
}
