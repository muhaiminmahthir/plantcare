package com.projectfyp.plantcare.controller;

import com.projectfyp.plantcare.model.Plant;
import com.projectfyp.plantcare.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private PlantService plantService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Plant> plants = plantService.getAllPlants();
        if (plants == null || plants.isEmpty()) {
            // Handle the case where no plants are available
            
        }
        model.addAttribute("plants", plants);
        return "dashboard"; // Render the dashboard.html view
    }

    @PostMapping("/water/{plantId}")
    public String triggerManualWatering(@PathVariable Long plantId) {
        plantService.triggerManualWatering(plantId);
        return "redirect:/dashboard"; // Redirect to the dashboard page after watering
    }

    @PostMapping("/schedule/{plantId}")
    public String setWateringSchedule(@PathVariable Long plantId, @RequestParam("time") String time) {
        // Logic to set watering schedule
        return "redirect:/dashboard";
    }

    @PostMapping("/threshold/{plantId}")
    public String adjustMoistureThreshold(@PathVariable Long plantId, @RequestParam("threshold") int threshold) {
        plantService.adjustMoistureThreshold(plantId, threshold);
        return "redirect:/dashboard"; // Redirect to the dashboard after adjusting threshold
    }
}
