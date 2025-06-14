package com.projectfyp.plantcare.controller;

import com.projectfyp.plantcare.model.Plant;
import com.projectfyp.plantcare.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/plants")
public class PlantApiController {

    @Autowired
    private PlantService plantService;

    // ESP32 sends moisture readings here
    @PostMapping("/{id}/moisture")
    public ResponseEntity<Map<String, Object>> updateMoisture(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> data) {
        
        try {
            Plant plant = plantService.getPlantById(id).orElseThrow();
            
            // Update moisture level from ESP32
            Integer moistureLevel = (Integer) data.get("moistureLevel");
            plant.setMoistureLevel(moistureLevel);
            plantService.savePlant(plant);
            
            // Send back current configuration
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("moistureThreshold", plant.getMoistureThreshold());
            response.put("autoAlertsEnabled", plant.getIsWateringEnabled()); // Reuse this field for alerts
            response.put("message", "Moisture level updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ESP32 reports alert events here (instead of watering)
    @PostMapping("/{id}/alert")
    public ResponseEntity<Map<String, Object>> reportAlert(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {
        
        try {
            Plant plant = plantService.getPlantById(id).orElseThrow();
            
            // Log alert event
            String reason = (String) data.get("reason");
            System.out.println("Plant " + id + " alert triggered: " + reason);
            
            // Update moisture level if provided
            if (data.containsKey("moistureLevel")) {
                Integer moistureLevel = (Integer) data.get("moistureLevel");
                plant.setMoistureLevel(moistureLevel);
                plantService.savePlant(plant);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Alert event recorded");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get plant status (for ESP32 or web interface)
    @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlant(@PathVariable Long id) {
        try {
            Plant plant = plantService.getPlantById(id).orElseThrow();
            return ResponseEntity.ok(plant);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update plant configuration
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlant(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        
        try {
            Plant plant = plantService.getPlantById(id).orElseThrow();
            
            if (updates.containsKey("moistureThreshold")) {
                plant.setMoistureThreshold((Integer) updates.get("moistureThreshold"));
            }
            
            if (updates.containsKey("isWateringEnabled")) {
                // Reuse this field for auto alerts enable/disable
                plant.setIsWateringEnabled((Boolean) updates.get("isWateringEnabled"));
            }
            
            plantService.savePlant(plant);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Plant configuration updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get all plants (for dashboard)
    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getAllPlants() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("plants", plantService.getAllPlants());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}