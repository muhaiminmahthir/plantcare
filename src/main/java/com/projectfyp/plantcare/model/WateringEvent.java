package com.projectfyp.plantcare.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class WateringEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    private boolean automatic;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;

    public WateringEvent() {
        // Default constructor
    }

    public WateringEvent(LocalDateTime timestamp, boolean automatic, Plant plant) {
        this.timestamp = timestamp;
        this.automatic = automatic;
        this.plant = plant;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}
