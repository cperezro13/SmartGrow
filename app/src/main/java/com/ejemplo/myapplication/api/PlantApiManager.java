package com.ejemplo.myapplication.api;

import com.ejemplo.myapplication.models.PlantSuggestion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlantApiManager {
    private static final String API_KEY = "39wVjmrXbjRE6DJBmZX0shG7z1Bd4J8vq592MDYinjqTPzgGQJ"; // Tu API key
    private OkHttpClient client = new OkHttpClient();

    // Definición del callback para la API
    public interface PlantApiCallback {
        void onSuccess(List<PlantSuggestion> suggestions);
        void onFailure(String errorMessage);
    }

    public void identifyPlant(String imageBase64, PlantApiCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("api_key", API_KEY);

            JSONArray images = new JSONArray();
            images.put(imageBase64);
            json.put("images", images);

            JSONArray modifiers = new JSONArray();
            modifiers.put("crops_fast");
            modifiers.put("similar_images");
            json.put("modifiers", modifiers);

            json.put("plant_language", "es");

            JSONArray plantDetails = new JSONArray();
            plantDetails.put("common_names");
            plantDetails.put("url");
            plantDetails.put("name_authority");
            plantDetails.put("wiki_description");
            plantDetails.put("taxonomy");
            plantDetails.put("synonyms");
            json.put("plant_details", plantDetails);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://api.plant.id/v2/identify")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure("Error: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray suggestionsArray = jsonResponse.optJSONArray("suggestions");

                            if (suggestionsArray != null && suggestionsArray.length() > 0) {
                                List<PlantSuggestion> suggestions = new ArrayList<>();
                                for (int i = 0; i < suggestionsArray.length(); i++) {
                                    JSONObject suggestionObj = suggestionsArray.getJSONObject(i);
                                    String plantName = suggestionObj.optString("plant_name", "Desconocido");
                                    double probability = suggestionObj.optDouble("probability", 0.0);

                                    // Extraer información adicional de wiki_description
                                    String infoText = "";
                                    JSONObject details = suggestionObj.optJSONObject("plant_details");
                                    if (details != null) {
                                        JSONObject wikiDescription = details.optJSONObject("wiki_description");
                                        if (wikiDescription != null) {
                                            infoText = wikiDescription.optString("value", "");
                                        }
                                    }

                                    // Extraer la URL de la imagen de similar_images
                                    String imageUrl = "";
                                    JSONArray similarImagesArray = suggestionObj.optJSONArray("similar_images");
                                    if (similarImagesArray != null && similarImagesArray.length() > 0) {
                                        JSONObject firstImage = similarImagesArray.optJSONObject(0);
                                        if (firstImage != null) {
                                            imageUrl = firstImage.optString("url", "");
                                        }
                                    }

                                    // Se crea la sugerencia con la imagen extraída
                                    suggestions.add(new PlantSuggestion(plantName, probability, infoText, imageUrl));
                                }
                                callback.onSuccess(suggestions);
                            } else {
                                callback.onFailure("No se encontraron sugerencias.");
                            }
                        } catch (JSONException e) {
                            callback.onFailure("Error al procesar la respuesta: " + e.getMessage());
                        }
                    } else {
                        callback.onFailure("Error en la respuesta de la API: " + response.code());
                    }
                }
            });
        } catch (JSONException e) {
            callback.onFailure("Error al crear la solicitud JSON: " + e.getMessage());
        }
    }
}

