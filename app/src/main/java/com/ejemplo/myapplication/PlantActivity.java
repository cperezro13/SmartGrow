package com.ejemplo.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ejemplo.myapplication.api.PlantIdApi;
import com.ejemplo.myapplication.models.PlantIdentificationRequest;
import com.ejemplo.myapplication.models.PlantIdentificationResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private TextView resultText;
    private Uri imageUri;
    private Button identifyPlantButton;
    private DatabaseReference userPlantsRef;
    private FirebaseAuth mAuth;
    private static final String API_KEY = BuildConfig.PLANT_ID_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        imageView = findViewById(R.id.imageView);
        resultText = findViewById(R.id.resultText);
        identifyPlantButton = findViewById(R.id.identifyPlantButton);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button viewPlantsButton = findViewById(R.id.viewPlantsButton);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userPlantsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("plants");

        selectImageButton.setOnClickListener(view -> openGallery());
        identifyPlantButton.setOnClickListener(view -> identifyPlant());
        viewPlantsButton.setOnClickListener(view -> startActivity(new Intent(this, PlantListActivity.class)));
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                // Convertir la URI de la imagen a un Bitmap y cargarla en el ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void identifyPlant() {
        if (imageUri == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            String base64Image = encodeImageToBase64(bitmap);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.plant.id/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PlantIdApi plantIdApi = retrofit.create(PlantIdApi.class);
            PlantIdentificationRequest request = new PlantIdentificationRequest(API_KEY, Collections.singletonList(base64Image));

            Call<PlantIdentificationResponse> call = plantIdApi.identifyPlant(request);
            call.enqueue(new Callback<PlantIdentificationResponse>() {
                @Override
                public void onResponse(Call<PlantIdentificationResponse> call, Response<PlantIdentificationResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        PlantIdentificationResponse apiResponse = response.body();

                        if (apiResponse.getSuggestions() != null && !apiResponse.getSuggestions().isEmpty()) {
                            PlantIdentificationResponse.Result firstResult = apiResponse.getSuggestions().get(0);
                            if (firstResult != null && firstResult.getSpecies() != null) {
                                String plantName = firstResult.getSpecies().getScientificName();
                                resultText.setText("Planta: " + plantName);
                                savePlantToFirebase(new PlantModel(plantName, "Descripción predeterminada"));
                            }
                        } else {
                            Toast.makeText(PlantActivity.this, "No se encontraron coincidencias", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(PlantActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PlantIdentificationResponse> call, Throwable t) {
                    Toast.makeText(PlantActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    Log.e("API Error", t.getMessage());
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Error al procesar imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void savePlantToFirebase(PlantModel plant) {
        String plantId = userPlantsRef.push().getKey();
        if (plantId != null) {
            plant.setId(plantId);
            userPlantsRef.child(plantId).setValue(plant)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PlantActivity.this, "Planta guardada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PlantActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}