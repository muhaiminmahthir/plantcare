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

    public Plant getPlantById(Long id) {
        return plantRepository.findById(id).orElse(null);
    }

    public Plant savePlant(Plant plant) {
        return plantRepository.save(plant);
    }

    public Plant updatePlant(Long id, Plant updatedPlant) {
        Optional<Plant> optionalPlant = plantRepository.findById(id);
        if (optionalPlant.isPresent()) {
            Plant existingPlant = optionalPlant.get();
            existingPlant.setName(updatedPlant.getName());
            existingPlant.setSpecies(updatedPlant.getSpecies());
            existingPlant.setDescription(updatedPlant.getDescription());
            return plantRepository.save(existingPlant);
        }
        return null;
    }

    public boolean deletePlant(Long id) {
        if (plantRepository.existsById(id)) {
            plantRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
