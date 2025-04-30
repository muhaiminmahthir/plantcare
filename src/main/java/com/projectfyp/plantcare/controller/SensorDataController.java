package com.projectfyp.plantcare.controller;

import com.projectfyp.plantcare.model.SensorData;
import com.projectfyp.plantcare.service.SensorDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensor-data")
@CrossOrigin(origins = "*")  // Enable CORS if accessing from frontend or ESP32
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @PostMapping
    public ResponseEntity<SensorData> receiveSensorData(@RequestBody SensorData sensorData) {
        SensorData savedData = sensorDataService.saveSensorData(sensorData);
        return ResponseEntity.ok(savedData);
    }

    @GetMapping
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        return ResponseEntity.ok(sensorDataService.getAllSensorData());
    }

    @GetMapping("/latest")
    public ResponseEntity<SensorData> getLatestSensorData() {
        SensorData latestData = sensorDataService.getLatestSensorData();
        return ResponseEntity.ok(latestData);
    }
}
