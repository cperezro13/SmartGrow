package com.ejemplo.myapplication.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlantIdentificationResponse {
    private List<Result> suggestions;

    public List<Result> getSuggestions() {
        return suggestions;
    }

    public static class Result {
        private Species species;

        public Species getSpecies() {
            return species;
        }
    }

    public static class Species {
        @SerializedName("scientific_name")
        private String scientificName;

        public String getScientificName() {
            return scientificName;
        }
    }
}