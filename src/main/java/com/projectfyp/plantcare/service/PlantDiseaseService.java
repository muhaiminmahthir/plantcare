package com.projectfyp.plantcare.service;

import com.projectfyp.plantcare.model.PlantDiseaseResult;
import java.io.*;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PlantDiseaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(PlantDiseaseService.class);
    
    @Value("${ai.python.script.path:python-ai/plant_disease_recognition.py}")
    private String pythonScriptPath;
    
    @Value("${ai.python.executable:python}")
    private String pythonExecutable;
    
    public PlantDiseaseResult analyzePlantDisease(String imagePath) {
    try {
        // Build command
        List<String> command = Arrays.asList(
            pythonExecutable,
            pythonScriptPath,
            "--image", imagePath,
            "--output", "json"
        );
        
        logger.info("Executing command: {}", String.join(" ", command));
        
        // Execute Python script
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();
        
        // Read output
        String result = readProcessOutput(process);
        
        // Wait for completion
        int exitCode = process.waitFor();
        
        logger.info("Python script exit code: {}", exitCode);
        logger.info("Python script output: {}", result);
        
        if (exitCode != 0) {
            throw new RuntimeException("Python script failed with exit code: " + exitCode + ". Output: " + result);
        }
        
        // Parse JSON result
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(result, PlantDiseaseResult.class);
        
        } catch (Exception e) {
            logger.error("Error analyzing plant disease", e);
            throw new RuntimeException("AI analysis failed: " + e.getMessage());
        }
    }
    private String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        String result = output.toString().trim();
        
        // Extract JSON part (look for the line that starts with {)
        String[] lines = result.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("{")) {
                return line;
            }
        }
        return result;
    }
}