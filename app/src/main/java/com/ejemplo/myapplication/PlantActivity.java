package com.ejemplo.myapplication;

import android.os.Bundle;
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
import com.ejemplo.myapplication.PlantModel;

import java.util.ArrayList;
import java.util.List;

public class PlantActivity extends AppCompatActivity {

    private EditText plantNameEditText, plantDescriptionEditText;
    private Button savePlantButton;
    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private List<PlantModel> plantList;
    private DatabaseReference userPlantsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        plantNameEditText = findViewById(R.id.plantNameEditText);
        plantDescriptionEditText = findViewById(R.id.plantDescriptionEditText);
        savePlantButton = findViewById(R.id.savePlantButton);
        recyclerView = findViewById(R.id.recyclerView);

        // Configuración del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        plantList = new ArrayList<>();
        plantAdapter = new PlantAdapter(plantList);
        recyclerView.setAdapter(plantAdapter);

        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userPlantsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("plants");

        // Cargar plantas guardadas
        loadUserPlants();

        savePlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plantName = plantNameEditText.getText().toString().trim();
                String plantDescription = plantDescriptionEditText.getText().toString().trim();

                if (!plantName.isEmpty() && !plantDescription.isEmpty()) {
                    String plantId = userPlantsRef.push().getKey();
                    PlantModel newPlant = new PlantModel(plantName, plantDescription);
                    userPlantsRef.child(plantId).setValue(newPlant)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PlantActivity.this, "Planta guardada con éxito", Toast.LENGTH_SHORT).show();
                                        // Limpiar los campos de texto después de guardar
                                        plantNameEditText.setText("");
                                        plantDescriptionEditText.setText("");
                                    } else {
                                        Toast.makeText(PlantActivity.this, "Error al guardar la planta", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(PlantActivity.this, "Por favor, ingresa todos los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserPlants() {
        userPlantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plantList.clear();
                for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                    PlantModel plant = plantSnapshot.getValue(PlantModel.class);
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
}
