package com.projectfyp.plantcare.controller;

import com.projectfyp.plantcare.model.WateringEvent;
import com.projectfyp.plantcare.service.WateringEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watering-events")
public class WateringEventController {

    @Autowired
    private WateringEventService wateringEventService;

    @GetMapping
    public List<WateringEvent> getAllEvents() {
        return wateringEventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public WateringEvent getEventById(@PathVariable Long id) {
        return wateringEventService.getEventById(id);
    }

    @PostMapping
    public WateringEvent createEvent(@RequestBody WateringEvent event) {
        return wateringEventService.saveEvent(event);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        wateringEventService.deleteEvent(id);
    }
}
