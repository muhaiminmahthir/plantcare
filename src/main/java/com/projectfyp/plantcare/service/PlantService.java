package com.projectfyp.plantcare.service;

import com.projectfyp.plantcare.model.Plant;
import com.projectfyp.plantcare.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    public Optional<Plant> getPlantById(Long id) {
        return plantRepository.findById(id);
    }

    public void savePlant(Plant plant) {
        plantRepository.save(plant);
    }

    // Renamed from triggerManualWatering to acknowledge alert
    public void acknowledgeAlert(Long plantId) {
        Plant plant = plantRepository.findById(plantId).orElseThrow();
        // Just mark as acknowledged - no moisture level change
        System.out.println("Alert acknowledged for plant: " + plant.getName());
        // You could add a timestamp field here if needed
        plantRepository.save(plant);
    }

    public void adjustMoistureThreshold(Long plantId, int newThreshold) {
        Plant plant = plantRepository.findById(plantId).orElseThrow();
        plant.setMoistureThreshold(newThreshold);
        plantRepository.save(plant);
    }

    // Method for ESP32 integration
    public void updateMoistureLevel(Long plantId, int moistureLevel) {
        Plant plant = plantRepository.findById(plantId).orElseThrow();
        plant.setMoistureLevel(moistureLevel);
        plantRepository.save(plant);
    }

    // Toggle auto alerts (reusing the isWateringEnabled field)
    public void toggleAutoAlerts(Long plantId, boolean enabled) {
        Plant plant = plantRepository.findById(plantId).orElseThrow();
        plant.setIsWateringEnabled(enabled); // Reuse this field for auto alerts
        plantRepository.save(plant);
    }
}