package com.projectfyp.plantcare.service;

import com.projectfyp.plantcare.model.Plant;
import com.projectfyp.plantcare.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    public List<Plant> getAllPlants() {
        // Returning mock data for testing purposes
        Plant plant1 = new Plant();
        plant1.setId(1L);
        plant1.setName("Rose");
        plant1.setMoistureLevel(60);
        plant1.setMoistureThreshold(40);
        plant1.setIsWateringEnabled(true);

        Plant plant2 = new Plant();
        plant2.setId(2L);
        plant2.setName("Tulip");
        plant2.setMoistureLevel(75);
        plant2.setMoistureThreshold(50);
        plant2.setIsWateringEnabled(false);

        return Arrays.asList(plant1, plant2);  // Return a list of mock plants
        
        //return plantRepository.findAll(); commented for testing purposes
    }

    public Optional<Plant> getPlantById(Long id) {
        return plantRepository.findById(id);
    }

    public void savePlant(Plant plant) {
        plantRepository.save(plant);
    }

    public void triggerManualWatering(Long plantId) {
        // Logic for triggering watering
        // This is where you send a request to the ESP32 to trigger the watering
        Plant plant = plantRepository.findById(plantId).orElseThrow();
        plant.setMoistureLevel(100);  // Assume watering brings moisture level to 100%
        plantRepository.save(plant);  // Save updated plant status after watering
    }

    public void adjustMoistureThreshold(Long plantId, int newThreshold) {
        Plant plant = plantRepository.findById(plantId).orElseThrow();
        plant.setMoistureThreshold(newThreshold);
        plantRepository.save(plant);
    }
}
