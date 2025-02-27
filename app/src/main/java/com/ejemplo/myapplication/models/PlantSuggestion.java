package com.ejemplo.myapplication.models;

public class PlantSuggestion {
    private String plantName;
    private double probability; // Valor entre 0 y 1
    private String infoText;    // Información adicional (extracto de Wikipedia)
    private String imageUrl;    // URL de una imagen similar

    public PlantSuggestion(String plantName, double probability, String infoText, String imageUrl) {
        this.plantName = plantName;
        this.probability = probability;
        this.infoText = infoText;
        this.imageUrl = imageUrl;
    }


    public String getPlantName() {
        return plantName;
    }

    public double getProbability() {
        return probability;
    }

    public String getInfoText() {
        return infoText;
    }
    public String getImageUrl() {
        return imageUrl;
    }


    @Override
    public String toString() {
        // Se muestra el nombre y la probabilidad en porcentaje
        return plantName + " (" + String.format("%.2f", probability * 100) + "%)";
    }
}
