package com.projectfyp.plantcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class PlantDiseaseResult {

    private String plantType;
    private String diseaseStatus;
    private String diseaseName;
    private double confidence;
    private String confidenceLevel;
    
    @JsonProperty("isHealthy")
    private boolean healthy;
    
    @JsonProperty("isConfident")
    private boolean confident;
    
    @JsonProperty("isUncertain")
    private boolean uncertain;
    
    private String recommendation;
    private List<TopPrediction> topPredictions;
    private boolean success = true;
    private String error;
    private String analysisMethod;
    private String warning;

    // Getters and Setters
    public String getPlantType() { return plantType; }
    public void setPlantType(String plantType) { this.plantType = plantType; }

    public String getDiseaseStatus() { return diseaseStatus; }    
    public void setDiseaseStatus(String diseaseStatus) { this.diseaseStatus = diseaseStatus; }

    public String getDiseaseName() { return diseaseName; }
    public void setDiseaseName(String diseaseName) { this.diseaseName = diseaseName; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }

    public boolean isHealthy() { return healthy; }   
    public void setHealthy(boolean healthy) { this.healthy = healthy; }

    public boolean isConfident() { return confident; }
    public void setConfident(boolean confident) { this.confident = confident; }

    public boolean isUncertain() { return uncertain; }
    public void setUncertain(boolean uncertain) { this.uncertain = uncertain; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public List<TopPrediction> getTopPredictions() { return topPredictions; }
    public void setTopPredictions(List<TopPrediction> topPredictions) { this.topPredictions = topPredictions; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getAnalysisMethod() { return analysisMethod; }
    public void setAnalysisMethod(String analysisMethod) { this.analysisMethod = analysisMethod; }

    public String getWarning() { return warning; }
    public void setWarning(String warning) { this.warning = warning; }

    public static class TopPrediction {
        private String className;
        private String plantType;
        private String disease;
        private double probability;

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public String getPlantType() { return plantType; }
        public void setPlantType(String plantType) { this.plantType = plantType; }

        public String getDisease() { return disease; }
        public void setDisease(String disease) { this.disease = disease; }

        public double getProbability(){ return probability; }
        public void setProbability(double probability){ this.probability = probability; }
    }
}