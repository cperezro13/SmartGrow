package com.ejemplo.myapplication;

public class PlantModel {
    private String id; // Nuevo atributo para el ID
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
}
