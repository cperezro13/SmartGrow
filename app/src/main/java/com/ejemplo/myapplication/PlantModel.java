package com.ejemplo.myapplication;

public class PlantModel {
    private String name;
    private String description;

    // Constructor vacío requerido por Firebase
    public PlantModel() {
    }

    // Constructor con parámetros
    public PlantModel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters y setters
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
}
