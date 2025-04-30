package com.projectfyp.plantcare.service;

import com.projectfyp.plantcare.model.SensorData;
import com.projectfyp.plantcare.repository.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorDataService {

    private final SensorDataRepository sensorDataRepository;

    public SensorDataService(SensorDataRepository sensorDataRepository) {
        this.sensorDataRepository = sensorDataRepository;
    }

    public SensorData saveSensorData(SensorData sensorData) {
        return sensorDataRepository.save(sensorData);
    }

    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }

    public SensorData getLatestSensorData() {
        return sensorDataRepository.findTopByOrderByTimestampDesc();
    }
}
