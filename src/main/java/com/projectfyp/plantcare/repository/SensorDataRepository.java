package com.projectfyp.plantcare.repository;

import com.projectfyp.plantcare.model.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    SensorData findTopByOrderByTimestampDesc();  // Get the latest reading
}
