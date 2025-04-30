package com.projectfyp.plantcare.controller;

import com.projectfyp.plantcare.model.Plant;
import com.projectfyp.plantcare.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    @Autowired
    private PlantService plantService;

    // Get all plants
    @GetMapping
    public List<Plant> getAllPlants() {
        return plantService.getAllPlants();
    }

    // Get plant by ID
    @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlantById(@PathVariable Long id) {
        Plant plant = plantService.getPlantById(id);
        return plant != null ? ResponseEntity.ok(plant) : ResponseEntity.notFound().build();
    }

    // Create new plant
    @PostMapping
    public Plant createPlant(@RequestBody Plant plant) {
        return plantService.savePlant(plant);
    }

    // Update existing plant
    @PutMapping("/{id}")
    public ResponseEntity<Plant> updatePlant(@PathVariable Long id, @RequestBody Plant updatedPlant) {
        Plant plant = plantService.updatePlant(id, updatedPlant);
        return plant != null ? ResponseEntity.ok(plant) : ResponseEntity.notFound().build();
    }

    // Delete plant
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        boolean deleted = plantService.deletePlant(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
