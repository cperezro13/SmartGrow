package com.ejemplo.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ejemplo.myapplication.R;
import com.ejemplo.myapplication.adapters.PlantAdapter;
import com.ejemplo.myapplication.models.PlantModel;
import com.ejemplo.myapplication.services.PlantService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class  PlantActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlantAdapter plantAdapter;
    private List<PlantModel> plantList;
    private PlantService plantService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant);

        // Inicializar servicio de plantas (PlantService) ANTES de crear el adaptador
        plantService = new PlantService();

        // Configurar RecyclerView y adaptador
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
                plantService.deletePlant(plantId, new PlantService.OperationCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(PlantActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(PlantActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, plantService);  // Ahora ya plantService está inicializado

        recyclerView.setAdapter(plantAdapter);

        // Cargar las plantas del usuario
        loadUserPlants();

        // Usamos el FloatingActionButton existente para agregar planta.
        FloatingActionButton addPlantButton = findViewById(R.id.recognizePlantButton);
        addPlantButton.setOnClickListener(v -> showAddPlantOptions());
    }

    private void loadUserPlants() {
        plantService.loadUserPlants(new PlantService.LoadPlantsCallback() {
            @Override
            public void onLoad(List<PlantModel> plants) {
                plantList.clear();
                plantList.addAll(plants);
                runOnUiThread(() -> plantAdapter.notifyDataSetChanged());
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(PlantActivity.this, error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showEditDialog(PlantModel plant, String plantId) {
        // Aquí implementa el diálogo para editar la planta, si lo deseas.
        Toast.makeText(this, "Editar planta: " + plant.getName(), Toast.LENGTH_SHORT).show();
    }

    private void showAddPlantOptions() {
        String[] options = {"Usar API (Plant.id)", "Usar Modelo TensorFlow"};
        new AlertDialog.Builder(this)
                .setTitle("Agregar planta")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Lanza la actividad de reconocimiento vía API
                        startActivity(new Intent(PlantActivity.this, AdvancedSearch.class));
                    } else {
                        // Lanza la actividad de reconocimiento vía TensorFlow
                        startActivity(new Intent(PlantActivity.this, TensorFlowSearchActivity.class));
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
