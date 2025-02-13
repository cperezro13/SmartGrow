package com.ejemplo.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PlantActivity extends AppCompatActivity {

    private EditText plantNameEditText, plantDescriptionEditText;
    private Button savePlantButton, recognizePlantButton;
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private List<PlantModel> plantList;
    private DatabaseReference userPlantsRef;
    private FirebaseAuth mAuth;
    private Uri photoUri;
    private String currentPhotoPath;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private static final int REQUEST_CAMERA_PERMISSION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        plantNameEditText = findViewById(R.id.plantNameEditText);
        plantDescriptionEditText = findViewById(R.id.plantDescriptionEditText);
        savePlantButton = findViewById(R.id.savePlantButton);
        recognizePlantButton = findViewById(R.id.recognizePlantButton);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapter(plantList, new PlantAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(PlantModel plant, String plantId) {
                showEditDialog(plant, plantId);
            }

            @Override
            public void onDeleteClick(String plantId) {
                deletePlant(plantId);
            }
        });
        recyclerView.setAdapter(plantAdapter);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userPlantsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("plants");

        loadUserPlants();

        savePlantButton.setOnClickListener(v -> savePlant());
        recognizePlantButton.setOnClickListener(v -> showImageSourceDialog());

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

    }

    private void savePlant() {
        String plantName = plantNameEditText.getText().toString().trim();
        String plantDescription = plantDescriptionEditText.getText().toString().trim();

        if (!plantName.isEmpty() && !plantDescription.isEmpty()) {
            String plantId = userPlantsRef.push().getKey();
            PlantModel newPlant = new PlantModel(plantName, plantDescription);
            userPlantsRef.child(plantId).setValue(newPlant)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PlantActivity.this, "Planta guardada con éxito", Toast.LENGTH_SHORT).show();
                            plantNameEditText.setText("");
                            plantDescriptionEditText.setText("");
                        } else {
                            Toast.makeText(PlantActivity.this, "Error al guardar la planta", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(PlantActivity.this, "Por favor, ingresa todos los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                recognizePlant(imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                recognizePlant(photoUri);
            }
        }
    }



    private void recognizePlant(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            float[][][][] input = preprocessImage(resizedBitmap);

            Classifier classifier = new Classifier(getAssets(), "model_unquant.tflite");
            float[][] results = classifier.predict(input);

            int bestMatch = -1;
            float bestConfidence = 0;
            String[] labels = {"Haworthiopsis fasciata", "Aloe rauhii", "Echinocactus platyacanthus", "Crassula Pyramidalis", "Mammillaria plumosa", "Aeonium arboreum Zwartkop"};

            for (int i = 0; i < results[0].length; i++) {
                if (results[0][i] > bestConfidence) {
                    bestConfidence = results[0][i];
                    bestMatch = i;
                }
            }

            String plantName = labels[bestMatch];
            Toast.makeText(this, "Resultado: " + plantName + " (" + bestConfidence * 100 + "% de confianza)", Toast.LENGTH_LONG).show();
            classifier.close();

            addRecognizedPlantToDatabase(plantName, "Planta agregada automáticamente");

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reconocer la planta", Toast.LENGTH_SHORT).show();
        }
    }


    private float[][][][] preprocessImage(Bitmap bitmap) {
        int width = 224, height = 224;
        float[][][][] input = new float[1][width][height][3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                input[0][y][x][0] = ((pixel >> 16) & 0xFF) / 255.0f; // Rojo
                input[0][y][x][1] = ((pixel >> 8) & 0xFF) / 255.0f;  // Verde
                input[0][y][x][2] = (pixel & 0xFF) / 255.0f;         // Azul
            }
        }
        return input;
    }


    private void loadUserPlants() {
        userPlantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plantList.clear();
                for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                    PlantModel plant = plantSnapshot.getValue(PlantModel.class);
                    plant.setId(plantSnapshot.getKey());
                    plantList.add(plant);
                }
                plantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlantActivity.this, "Error al cargar plantas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog(PlantModel plant, String plantId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar planta");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_plant, null);
        EditText editName = view.findViewById(R.id.editPlantName);
        EditText editDescription = view.findViewById(R.id.editPlantDescription);

        editName.setText(plant.getName());
        editDescription.setText(plant.getDescription());

        builder.setView(view);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = editName.getText().toString().trim();
            String newDescription = editDescription.getText().toString().trim();

            if (!newName.isEmpty() && !newDescription.isEmpty()) {
                plant.setName(newName);
                plant.setDescription(newDescription);
                userPlantsRef.child(plantId).setValue(plant)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(PlantActivity.this, "Planta actualizada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PlantActivity.this, "Error al actualizar planta", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(PlantActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void deletePlant(String plantId) {
        userPlantsRef.child(plantId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PlantActivity.this, "Planta eliminada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlantActivity.this, "Error al eliminar planta", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción");
        builder.setItems(new CharSequence[]{"Tomar Foto", "Seleccionar de Galería"}, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openFileChooser();
            }
        });
        builder.show();
    }

    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.ejemplo.myapplication.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addRecognizedPlantToDatabase(String name, String description) {
        String plantId = userPlantsRef.push().getKey();
        PlantModel newPlant = new PlantModel(name, description);

        userPlantsRef.child(plantId).setValue(newPlant)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PlantActivity.this, "Planta agregada a tu colección", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlantActivity.this, "Error al guardar la planta", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
