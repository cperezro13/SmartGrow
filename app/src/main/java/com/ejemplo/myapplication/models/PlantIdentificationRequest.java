package com.ejemplo.myapplication.models;

import java.util.List;

public class PlantIdentificationRequest {
    private String api_key;
    private List<String> images;

    public PlantIdentificationRequest(String api_key, List<String> images) {
        this.api_key = api_key;
        this.images = images;
    }
}
