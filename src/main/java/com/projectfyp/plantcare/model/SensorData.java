package com.projectfyp.plantcare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int moistureLevel;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;

    public SensorData() {
        // Default constructor
    }

    public SensorData(int moistureLevel, LocalDateTime timestamp, Plant plant) {
        this.moistureLevel = moistureLevel;
        this.timestamp = timestamp;
        this.plant = plant;
    }

    public Long getId() {
        return id;
    }

    public int getMoistureLevel() {
        return moistureLevel;
    }

    public void setMoistureLevel(int moistureLevel) {
        this.moistureLevel = moistureLevel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}
