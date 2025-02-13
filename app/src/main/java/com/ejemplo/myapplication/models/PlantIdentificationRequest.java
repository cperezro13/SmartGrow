package com.ejemplo.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlantIdentificationRequest {

    @SerializedName("api_key")
    private final String apiKey; // Usamos camelCase y anotación para el nombre del campo JSON

    @SerializedName("images")
    private final List<String> images;

    public PlantIdentificationRequest(String apiKey, List<String> images) {
        this.apiKey = apiKey;
        this.images = images;
    }

    // Getter methods
    public String getApiKey() {
        return apiKey;
    }

    public List<String> getImages() {
        return images;
    }
}