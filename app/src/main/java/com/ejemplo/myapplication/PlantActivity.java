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
    private static final String API_KEY = "1tGtdmzn7KENu09cshz4CYRdnk138pjDRQ4nt2aBuzvwKKEMK0"; // 🔹 Reemplaza con tu API Key de Plant.id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        imageView = findViewById(R.id.imageView);
        resultText = findViewById(R.id.resultText);
        identifyPlantButton = findViewById(R.id.identifyPlantButton);
        Button selectImageButton = findViewById(R.id.selectImageButton);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userPlantsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("plants");

        selectImageButton.setOnClickListener(view -> openGallery());
        identifyPlantButton.setOnClickListener(view -> identifyPlant());
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
            imageView.setImageURI(imageUri);
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
                        String plantName = response.body().getSuggestions().get(0).getSpecies().getScientificName();
                        resultText.setText("Planta: " + plantName);
                        savePlantToFirebase(plantName);
                    } else {
                        Toast.makeText(PlantActivity.this, "No se pudo identificar la planta", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PlantIdentificationResponse> call, Throwable t) {
                    Toast.makeText(PlantActivity.this, "Error en la API", Toast.LENGTH_SHORT).show();
                    Log.e("Plant ID Error", t.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void savePlantToFirebase(String plantName) {
        String plantId = userPlantsRef.push().getKey();
        if (plantId != null) {
            userPlantsRef.child(plantId).setValue(plantName)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PlantActivity.this, "Planta guardada en Firebase", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PlantActivity.this, "Error al guardar en Firebase", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
