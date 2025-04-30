package com.projectfyp.plantcare.repository;

import com.projectfyp.plantcare.model.WateringEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WateringEventRepository extends JpaRepository<WateringEvent, Long> {
}
