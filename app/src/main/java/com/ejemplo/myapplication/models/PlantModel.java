package com.ejemplo.myapplication.models;

import java.util.List;

public class PlantModel {
    private String id;             // ID en Firebase
    private String name;           // Nombre científico
    private String description;    // Descripción (puede ser otro dato o dejarse en blanco)
    private float confidence;      // Probabilidad de reconocimiento (entre 0 y 1)
    private String infoText;       // Extracto de Wikipedia
    private String imageUri;       // URI de la imagen usada para el reconocimiento
    private String customName;     // Nombre personalizado asignado por el usuario

    // Campos para ciclo de riego (opcional)
    private int wateringCycle;     // Días entre riegos
    private long lastWatered;      // Timestamp del último riego

    // Constructor vacío requerido por Firebase
    public PlantModel() { }

    // Constructor para creación manual (sin reconocimiento ni imagen)
    public PlantModel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Constructor completo (con 6 parámetros, sin ciclo de riego)
    public PlantModel(String name, String description, float confidence, String infoText, String imageUri, String customName) {
        this.name = name;
        this.description = description;
        this.confidence = confidence;
        this.infoText = infoText;
        this.imageUri = imageUri;
        this.customName = customName;
    }

    // Constructor completo que incluye también el ciclo de riego
    public PlantModel(String name, String description, float confidence, String infoText, String imageUri, String customName, int wateringCycle, long lastWatered) {
        this.name = name;
        this.description = description;
        this.confidence = confidence;
        this.infoText = infoText;
        this.imageUri = imageUri;
        this.customName = customName;
        this.wateringCycle = wateringCycle;
        this.lastWatered = lastWatered;
    }

    // Getters y setters

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public float getConfidence() {
        return confidence;
    }
    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getInfoText() {
        return infoText;
    }
    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public String getImageUri() {
        return imageUri;
    }
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getCustomName() {
        return customName;
    }
    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public int getWateringCycle() {
        return wateringCycle;
    }
    public void setWateringCycle(int wateringCycle) {
        this.wateringCycle = wateringCycle;
    }

    public long getLastWatered() {
        return lastWatered;
    }
    public void setLastWatered(long lastWatered) {
        this.lastWatered = lastWatered;
    }
}
