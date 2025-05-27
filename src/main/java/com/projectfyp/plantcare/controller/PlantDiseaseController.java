package com.projectfyp.plantcare.controller;

import java.nio.file.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.projectfyp.plantcare.model.PlantDiseaseResult;
import com.projectfyp.plantcare.service.PlantDiseaseService;

@RestController
@RequestMapping("/api/plant-disease")
public class PlantDiseaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlantDiseaseController.class);
    
    @Autowired
    private PlantDiseaseService plantDiseaseService;
    
    @Value("${file.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;
    
    @PostMapping("/analyze")
    public ResponseEntity<PlantDiseaseResult> analyzePlantDisease(
            @RequestParam("image") MultipartFile imageFile) {
        
        try {
            // Validate file
            if (imageFile.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Save uploaded file temporarily
            String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(filename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Analyze with AI
            PlantDiseaseResult result = plantDiseaseService.analyzePlantDisease(filePath.toString());
            
            // Clean up temporary file
            Files.deleteIfExists(filePath);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error in plant disease analysis", e);
            
            // Return error response with proper JSON structure
            PlantDiseaseResult errorResult = new PlantDiseaseResult();
            errorResult.setSuccess(false);
            errorResult.setError("Analysis failed: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}