package com.ejemplo.myapplication.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.widget.ListView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.ejemplo.myapplication.R;
import com.ejemplo.myapplication.adapters.SuggestionAdapter;
import com.ejemplo.myapplication.api.PlantApiManager;
import com.ejemplo.myapplication.models.PlantModel;
import com.ejemplo.myapplication.models.PlantSuggestion;
import com.ejemplo.myapplication.services.PlantService;
import com.ejemplo.myapplication.utils.ImageUtils;
import java.util.ArrayList;
import java.util.List;

public class AdvancedSearch extends AppCompatActivity {
    private ImageView imageView;
    private Button selectImageButton;
    private Button identifyPlantButton;
    private Button takePhotoButton;
    private Uri selectedImageUri;
    private Uri capturedImageUri;
    private PlantApiManager plantApiManager;
    private PlantService plantService;

    private final ActivityResultLauncher<Intent> getContent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imageView.setImageURI(selectedImageUri);
                }
            }
    );

    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) { // Si se tomó la foto correctamente
                    imageView.setImageURI(capturedImageUri);
                    selectedImageUri = capturedImageUri;
                } else {
                    Toast.makeText(this, "No se capturó la imagen", Toast.LENGTH_SHORT).show();
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
        takePhotoButton = findViewById(R.id.takePhotoButton);

        // Inicializamos los gestores de API y de Firebase
        plantApiManager = new PlantApiManager();
        plantService = new PlantService();

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getContent.launch(intent);
        });

        takePhotoButton.setOnClickListener(v -> {
            // Crea un archivo temporal y obtén su URI usando FileProvider
            capturedImageUri = ImageUtils.createImageFileUri(this);
            if(capturedImageUri != null) {
                takePictureLauncher.launch(capturedImageUri);
            } else {
                Toast.makeText(this, "Error al crear el archivo para la imagen", Toast.LENGTH_SHORT).show();
            }
        });


        identifyPlantButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                String imageBase64 = ImageUtils.uriToBase64(getContentResolver(), selectedImageUri);
                plantApiManager.identifyPlant(imageBase64, new PlantApiManager.PlantApiCallback() {
                    @Override
                    public void onSuccess(List<PlantSuggestion> suggestions) {
                        runOnUiThread(() -> showSuggestionsDialog(suggestions));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> Toast.makeText(AdvancedSearch.this, errorMessage, Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                Toast.makeText(AdvancedSearch.this, "Por favor, selecciona una imagen primero.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Muestra un diálogo con las sugerencias para que el usuario seleccione
    private void showSuggestionsDialog(List<PlantSuggestion> suggestions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_suggestions, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.suggestionListView);
        SuggestionAdapter adapter = new SuggestionAdapter(this, suggestions);
        listView.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            PlantSuggestion selectedSuggestion = suggestions.get(position);
            addPlantToUserCollection(selectedSuggestion);
            dialog.dismiss();
        });
        dialog.show();
    }


    // Agrega la planta seleccionada a la colección del usuario
    private void addPlantToUserCollection(PlantSuggestion suggestion) {
        String imageUriString = (selectedImageUri != null) ? selectedImageUri.toString() : "";
        PlantModel newPlant = new PlantModel(
                suggestion.getPlantName(),
                "Agregado automáticamente",
                (float) suggestion.getProbability(),
                suggestion.getInfoText(),
                imageUriString,
                ""
        );

        plantService.savePlant(newPlant, new PlantService.OperationCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(AdvancedSearch.this, message, Toast.LENGTH_SHORT).show();
                    finish(); // Finaliza AdvancedSearch para volver a la pantalla anterior
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(AdvancedSearch.this, error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
