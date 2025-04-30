package com.projectfyp.plantcare.controller;

import com.projectfyp.plantcare.model.SensorData;
import com.projectfyp.plantcare.model.WateringEvent;
import com.projectfyp.plantcare.service.SensorDataService;
import com.projectfyp.plantcare.service.WateringEventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DashboardController {

    private final SensorDataService sensorDataService;
    private final WateringEventService wateringEventService;

    public DashboardController(SensorDataService sensorDataService, WateringEventService wateringEventService) {
        this.sensorDataService = sensorDataService;
        this.wateringEventService = wateringEventService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        SensorData sensorData = sensorDataService.getLatestSensorData();
        List<WateringEvent> events = wateringEventService.getAllEvents();

        model.addAttribute("sensorData", sensorData);
        model.addAttribute("wateringEvents", events);

        return "index"; // Loads index.html
    }
    @PostMapping("/api/watering-events")
    public String logManualWatering() {
        WateringEvent event = new WateringEvent(LocalDateTime.now(), false, null); // null for plant if not used
        wateringEventService.saveEvent(event);
        return "redirect:/";
    }
}
