package com.ejemplo.myapplication.models;

import java.util.List;

public class PlantIdentificationResponse {
    private String status;
    private List<Result> suggestions;

    public String getStatus() {
        return status;
    }

    public List<Result> getSuggestions() {
        return suggestions;
    }

    public static class Result {
        private Plant species;

        public Plant getSpecies() {
            return species;
        }
    }

    public static class Plant {
        private String scientificName;

        public String getScientificName() {
            return scientificName;
        }
    }
}
