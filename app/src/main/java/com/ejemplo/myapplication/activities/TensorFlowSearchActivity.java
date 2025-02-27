package com.ejemplo.myapplication.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.ejemplo.myapplication.R;
import com.ejemplo.myapplication.models.PlantModel;
import com.ejemplo.myapplication.services.PlantService;
import com.ejemplo.myapplication.services.RecognitionService;
import com.ejemplo.myapplication.utils.ImageUtils;
import java.io.IOException;

public class TensorFlowSearchActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button selectImageButton;
    private Button recognizeButton;
    private Button takePhotoButton;
    private Uri selectedImageUri;
    private Uri capturedImageUri;
    private RecognitionService recognitionService;
    private PlantService plantService;

    private final ActivityResultLauncher<Intent> getContentLauncher = registerForActivityResult(
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
                if (result) {
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
        setContentView(R.layout.activity_tensorflow_search);

        imageView = findViewById(R.id.imageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        recognizeButton = findViewById(R.id.recognizeButton);
        takePhotoButton = findViewById(R.id.takePhotoButton);

        // Inicializamos el servicio de reconocimiento y el servicio de plantas
        plantService = new PlantService();
        try {
            recognitionService = new RecognitionService(this);
        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar reconocimiento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Botón para seleccionar una imagen de la galería
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getContentLauncher.launch(intent);
        });

        // Tomar foto directamente
        takePhotoButton.setOnClickListener(v -> {
            // Crea un archivo temporal y obtén su URI usando FileProvider
            capturedImageUri = ImageUtils.createImageFileUri(this);
            if(capturedImageUri != null) {
                takePictureLauncher.launch(capturedImageUri);
            } else {
                Toast.makeText(this, "Error al crear el archivo para la imagen", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para reconocer la planta usando TensorFlow
        recognizeButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    recognitionService.recognizePlant(bitmap, new RecognitionService.RecognitionCallback() {
                        @Override
                        public void onResult(String plantName, float confidence) {
                            runOnUiThread(() -> {
                                Toast.makeText(TensorFlowSearchActivity.this, "Reconocido: " + plantName + " (" + (confidence * 100) + "%)", Toast.LENGTH_LONG).show();

                                // Asigna la información extra manualmente según la planta reconocida
                                String infoText;
                                switch (plantName) {
                                    case "Haworthiopsis fasciata":
                                        infoText = "Haworthiopsis fasciata, conocida como Zebra Haworthia, es una suculenta originaria de Sudáfrica, con hojas en roseta marcadas por rayas blancas. Es resistente y de bajo mantenimiento, ideal para interiores.";
                                        break;
                                    case "Aloe rauhii":
                                        infoText = "Aloe rauhii es una suculenta robusta caracterizada por sus hojas carnosas de tono verde azulado, adaptada a ambientes áridos y apreciada por su aspecto exótico y posibles propiedades medicinales.";
                                        break;
                                    case "Echinocactus platyacanthus":
                                        infoText = "Echinocactus platyacanthus, conocido como el cactus barril gigante, destaca por su imponente forma cilíndrica y espinas prominentes. Es típico de regiones desérticas y simboliza la resistencia de los ambientes áridos.";
                                        break;
                                    case "Crassula Pyramidalis":
                                        infoText = "Crassula Pyramidalis es una suculenta de crecimiento compacto con forma piramidal. Sus hojas carnosas y su adaptabilidad a condiciones secas la hacen perfecta para jardines rocosos y de bajo mantenimiento.";
                                        break;
                                    case "Mammillaria plumosa":
                                        infoText = "Mammillaria plumosa es un pequeño cactus ornamental reconocido por la disposición de sus tubérculos y espinas finas, que le confieren un aspecto delicado y casi 'plumoso'. Ideal para macetas y jardines secos.";
                                        break;
                                    case "Aeonium arboreum Zwartkop":
                                        infoText = "Aeonium arboreum 'Zwartkop' es una suculenta impactante por sus rosetas de color oscuro, casi negro, que contrastan con sus hojas carnosas. Es muy apreciada en jardines modernos por su estética única y facilidad de cuidado.";
                                        break;
                                    default:
                                        infoText = "Información adicional no disponible.";
                                        break;
                                }


                                // Crea el objeto PlantModel usando un constructor básico y luego asigna infoText e imagen
                                PlantModel newPlant = new PlantModel(plantName, "Agregado automáticamente (TensorFlow)");
                                newPlant.setInfoText(infoText);
                                if (selectedImageUri != null) {
                                    newPlant.setImageUri(selectedImageUri.toString());
                                }


                                plantService.savePlant(newPlant, new PlantService.OperationCallback() {
                                    @Override
                                    public void onSuccess(String message) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(TensorFlowSearchActivity.this, message, Toast.LENGTH_SHORT).show();
                                            finish(); // Regresa a PlantActivity
                                        });
                                    }
                                    @Override
                                    public void onError(String error) {
                                        runOnUiThread(() -> Toast.makeText(TensorFlowSearchActivity.this, error, Toast.LENGTH_SHORT).show());
                                    }
                                });
                            });
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TensorFlowSearchActivity.this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TensorFlowSearchActivity.this, "Por favor, selecciona una imagen primero.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
