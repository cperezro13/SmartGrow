package com.ejemplo.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button addPlantButton = findViewById(R.id.addPlantButton);

        addPlantButton.setOnClickListener(v -> showOptionsDialog());
    }

    private void showOptionsDialog() {
        // Crear un diálogo con dos opciones
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción")
                .setItems(new String[]{"Plantas Comunes", "Búsqueda Avanzada"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Opción 1: Plantas Comunes
                            Intent plantIntent = new Intent(HomeActivity.this, PlantActivity.class);
                            startActivity(plantIntent);
                            break;
                        case 1:
                            // Opción 2: Búsqueda Avanzada
                            Intent advancedSearchIntent = new Intent(HomeActivity.this, AdvancedSearch.class);
                            startActivity(advancedSearchIntent);
                            break;
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}