package com.projectfyp.plantcare.repository;

import com.projectfyp.plantcare.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    // Custom queries if necessary
}
