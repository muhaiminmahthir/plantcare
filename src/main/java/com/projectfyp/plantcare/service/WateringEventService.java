package com.projectfyp.plantcare.service;

import com.projectfyp.plantcare.model.WateringEvent;
import com.projectfyp.plantcare.repository.WateringEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WateringEventService {

    @Autowired
    private WateringEventRepository wateringEventRepository;

    public List<WateringEvent> getAllEvents() {
        return wateringEventRepository.findAll();
    }

    public WateringEvent getEventById(Long id) {
        return wateringEventRepository.findById(id).orElse(null);
    }

    public WateringEvent saveEvent(WateringEvent event) {
        return wateringEventRepository.save(event);
    }

    public boolean deleteEvent(Long id) {
        if (wateringEventRepository.existsById(id)) {
            wateringEventRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
