package com.ejemplo.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdvancedSearch extends AppCompatActivity {

    private ImageView imageView;
    private Button selectImageButton;
    private Button identifyPlantButton;
    private TextView resultTextView;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> getContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imageView.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        imageView = findViewById(R.id.imageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        identifyPlantButton = findViewById(R.id.identifyPlantButton);
        resultTextView = findViewById(R.id.resultTextView);

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getContent.launch(intent);
        });

        identifyPlantButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                String imageBase64 = uriToBase64(selectedImageUri);
                identifyPlant(imageBase64);
            } else {
                resultTextView.setText("Por favor, selecciona una imagen primero.");
            }
        });
    }

    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT); // Usar android.util.Base64
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void identifyPlant(String imageBase64) {
        String apiKey = "39wVjmrXbjRE6DJBmZX0shG7z1Bd4J8vq592MDYinjqTPzgGQJ"; // Reemplaza con tu API key de Plant.id

        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject json = new JSONObject();
            json.put("api_key", apiKey);

            JSONArray images = new JSONArray();
            images.put(imageBase64);
            json.put("images", images);

            JSONArray modifiers = new JSONArray()
                    .put("crops_fast")
                    .put("similar_images");
            json.put("modifiers", modifiers);

            json.put("plant_language", "es"); // Idioma en español

            JSONArray plantDetails = new JSONArray()
                    .put("common_names")
                    .put("url")
                    .put("name_authority")
                    .put("wiki_description")
                    .put("taxonomy")
                    .put("synonyms");
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
                    runOnUiThread(() -> resultTextView.setText("Error: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        System.out.println("Respuesta de la API: " + responseBody); // Imprimir la respuesta para depuración

                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray suggestions = jsonResponse.optJSONArray("suggestions");

                            if (suggestions != null && suggestions.length() > 0) {
                                StringBuilder resultText = new StringBuilder();
                                JSONObject firstSuggestion = suggestions.getJSONObject(0);

                                // Nombre de la planta
                                String plantName = firstSuggestion.optString("plant_name", "Desconocido");
                                resultText.append("Planta identificada: ").append(plantName).append("\n\n");

                                // Probabilidad
                                double probability = firstSuggestion.optDouble("probability", 0.0) * 100;
                                resultText.append("Probabilidad: ").append(String.format("%.2f", probability)).append("%\n\n");

                                // Nombres comunes
                                JSONObject plantDetails = firstSuggestion.optJSONObject("plant_details");
                                if (plantDetails != null) {
                                    JSONArray commonNames = plantDetails.optJSONArray("common_names");
                                    if (commonNames != null && commonNames.length() > 0) {
                                        resultText.append("Nombres comunes:\n");
                                        for (int i = 0; i < commonNames.length(); i++) {
                                            resultText.append("- ").append(commonNames.getString(i)).append("\n");
                                        }
                                    }

                                    // Wiki description en español
                                    JSONObject wikiDescription = plantDetails.optJSONObject("wiki_description");
                                    if (wikiDescription != null) {
                                        String description = wikiDescription.optString("value", "No hay descripción disponible.");
                                        resultText.append("\nDescripción:\n").append(description).append("\n");
                                    }
                                }

                                // Sugerencias
                                resultText.append("\nSugerencias:\n");
                                for (int i = 0; i < suggestions.length(); i++) {
                                    JSONObject suggestion = suggestions.getJSONObject(i);
                                    String suggestionName = suggestion.optString("plant_name", "Desconocido");
                                    double suggestionProbability = suggestion.optDouble("probability", 0.0) * 100;
                                    resultText.append("- ").append(suggestionName)
                                            .append(" (Probabilidad: ").append(String.format("%.2f", suggestionProbability)).append("%)\n");
                                }

                                // Mostrar el texto en el TextView
                                runOnUiThread(() -> resultTextView.setText(resultText.toString()));

                                // Mostrar las imágenes de las sugerencias
                                runOnUiThread(() -> {
                                    LinearLayout suggestionsImageLayout = findViewById(R.id.suggestionsImageLayout);
                                    suggestionsImageLayout.removeAllViews(); // Limpiar imágenes anteriores

                                    for (int i = 0; i < suggestions.length(); i++) {
                                        try {
                                            JSONObject suggestion = suggestions.getJSONObject(i);
                                            JSONArray similarImages = suggestion.optJSONArray("similar_images");

                                            if (similarImages != null && similarImages.length() > 0) {
                                                // Tomar la primera imagen similar
                                                JSONObject similarImage = similarImages.getJSONObject(0);
                                                String imageUrl = similarImage.optString("url");

                                                // Crear un ImageView para la imagen
                                                ImageView imageView = new ImageView(AdvancedSearch.this);
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        400 // Altura fija para las imágenes
                                                );
                                                layoutParams.setMargins(0, 16, 0, 16); // Márgenes entre imágenes
                                                imageView.setLayoutParams(layoutParams);
                                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                                // Cargar la imagen con Glide
                                                Glide.with(AdvancedSearch.this)
                                                        .load(imageUrl)
                                                        .into(imageView);

                                                // Agregar el ImageView al LinearLayout
                                                suggestionsImageLayout.addView(imageView);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            // Crear una copia final de la variable para usarla en la lambda
                                            final int finalI = i;
                                            runOnUiThread(() -> resultTextView.append("\nError al procesar la sugerencia " + finalI));
                                        }
                                    }
                                });
                            } else {
                                runOnUiThread(() -> resultTextView.setText("No se encontraron sugerencias."));
                            }
                        } catch (JSONException e) {
                            runOnUiThread(() -> resultTextView.setText("Error al procesar la respuesta: " + e.getMessage()));
                        }
                    } else {
                        // Manejar errores HTTP (por ejemplo, 401, 404, 500, etc.)
                        String errorMessage = "Error en la respuesta de la API: " + response.code();
                        if (response.code() == 401) {
                            errorMessage += " (No autorizado. Verifica tu clave de API.)";
                        } else if (response.code() == 404) {
                            errorMessage += " (Recurso no encontrado.)";
                        } else if (response.code() == 500) {
                            errorMessage += " (Error interno del servidor.)";
                        }

                        // Crear una copia final de la variable para usarla en la lambda
                        final String finalErrorMessage = errorMessage;
                        runOnUiThread(() -> resultTextView.setText(finalErrorMessage));
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            resultTextView.setText("Error al crear la solicitud JSON: " + e.getMessage());
        }
    }
}