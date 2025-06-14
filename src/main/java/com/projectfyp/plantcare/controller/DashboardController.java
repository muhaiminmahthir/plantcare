package com.projectfyp.plantcare.controller;

import com.projectfyp.plantcare.model.Plant;
import com.projectfyp.plantcare.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    @Autowired
    private PlantService plantService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Plant> plants = plantService.getAllPlants();
        
        // Create sample plant if none exist
        if (plants == null || plants.isEmpty()) {
            Plant samplePlant = new Plant();
            samplePlant.setName("Sample Plant");
            samplePlant.setMoistureLevel(0);  // Will be updated by ESP32
            samplePlant.setMoistureThreshold(30);
            samplePlant.setIsWateringEnabled(true); // Auto alerts enabled
            plantService.savePlant(samplePlant);
            
            plants = plantService.getAllPlants();
        }
        
        model.addAttribute("plants", plants);
        return "dashboard"; // Render the dashboard.html view
    }

    // Renamed from water to acknowledge alert
    @PostMapping("/acknowledge/{plantId}")
    public String acknowledgeAlert(@PathVariable Long plantId, RedirectAttributes redirectAttributes) {
        plantService.acknowledgeAlert(plantId);
        redirectAttributes.addFlashAttribute("message", "Alert acknowledged for plant " + plantId);
        return "redirect:/dashboard";
    }

    @PostMapping("/schedule/{plantId}")
    public String setMonitoringSchedule(@PathVariable Long plantId, @RequestParam String time, RedirectAttributes redirectAttributes) {
        System.out.println("Setting monitoring schedule for plant ID: " + plantId + " at " + time);
        // Logic to set monitoring schedule (if needed)
        redirectAttributes.addFlashAttribute("message", "Monitoring schedule for plant " + plantId + " set to " + time);
        return "redirect:/dashboard";
    }

    @PostMapping("/threshold/{plantId}")
    public String adjustMoistureThreshold(@PathVariable Long plantId, @RequestParam("threshold") int threshold) {
        plantService.adjustMoistureThreshold(plantId, threshold);
        return "redirect:/dashboard";
    }

    // New endpoint to toggle auto alerts
    @PostMapping("/toggle-alerts/{plantId}")
    public String toggleAutoAlerts(@PathVariable Long plantId, RedirectAttributes redirectAttributes) {
        Plant plant = plantService.getPlantById(plantId).orElseThrow();
        boolean newState = !plant.getIsWateringEnabled();
        plantService.toggleAutoAlerts(plantId, newState);
        
        String status = newState ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "Auto alerts " + status + " for " + plant.getName());
        return "redirect:/dashboard";
    }
}