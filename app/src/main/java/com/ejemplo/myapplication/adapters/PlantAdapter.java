package com.ejemplo.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ejemplo.myapplication.R;
import com.ejemplo.myapplication.models.PlantModel;
import com.ejemplo.myapplication.services.PlantService;
import androidx.appcompat.app.AlertDialog;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    public interface OnItemClickListener {
        void onEditClick(PlantModel plant, String plantId);
        void onDeleteClick(String plantId);
    }

    private List<PlantModel> plantList;
    private OnItemClickListener listener;
    private PlantService plantService; // Referencia a PlantService para actualizar Firebase

    public PlantAdapter(List<PlantModel> plantList, OnItemClickListener listener, PlantService plantService) {
        this.plantList = plantList;
        this.listener = listener;
        this.plantService = plantService;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        PlantModel plant = plantList.get(position);
        // Muestra el nombre científico (inmutable)
        holder.scientificNameTextView.setText(plant.getName());
        // Muestra el nombre personalizado en el EditText
        holder.customNameEditText.setText(plant.getCustomName());
        // Actualiza Firebase cuando se pierde el foco
        holder.customNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String newCustomName = holder.customNameEditText.getText().toString();
                plant.setCustomName(newCustomName);
                if (plant.getId() != null) {
                    plantService.updatePlantCustomName(plant.getId(), newCustomName, new PlantService.OperationCallback() {
                        @Override
                        public void onSuccess(String message) {
                            // Opcional: puedes mostrar un Toast o actualizar la UI
                        }
                        @Override
                        public void onError(String error) {
                            // Opcional: manejar el error, por ejemplo, mostrar un mensaje
                        }
                    });
                }
            }
        });
        // Muestra la información extra (Wiki) en el TextView
        holder.wikiInfoTextView.setText(plant.getInfoText());
        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.getContext())
                .load(plant.getImageUri())
                .placeholder(R.drawable.default_plant_image)
                .into(holder.plantImageView);
        // Asigna los listeners para editar y eliminar
        holder.editPlantButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_watering_cycle, null);
            // Obtener el Spinner del diálogo
            Spinner spinner = dialogView.findViewById(R.id.wateringCycleSpinner);
            // Opciones de ciclos en días, por ejemplo: 1, 3, 7 y 14 días
            String[] cycles = {"1", "3", "7", "14"};
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(v.getContext(), android.R.layout.simple_spinner_item, cycles);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);

            builder.setTitle("Selecciona ciclo de riego (días)")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, whichButton) -> {
                        String selectedCycleStr = spinner.getSelectedItem().toString();
                        int selectedCycle = Integer.parseInt(selectedCycleStr);
                        // Actualizar el objeto local y en Firebase
                        plant.setWateringCycle(selectedCycle);
                        // Llamar a la actualización en Firebase usando PlantService
                        plantService.updatePlantWateringCycle(plant.getId(), selectedCycle, new PlantService.OperationCallback() {
                            @Override
                            public void onSuccess(String message) {
                                // Opcional: Notificar éxito
                            }
                            @Override
                            public void onError(String error) {
                                Toast.makeText(v.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
        holder.deletePlantButton.setOnClickListener(v -> listener.onDeleteClick(plant.getId()));
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView scientificNameTextView;
        EditText customNameEditText;
        TextView wikiInfoTextView;
        Button editPlantButton, deletePlantButton;
        ImageView plantImageView;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            scientificNameTextView = itemView.findViewById(R.id.scientificNameTextView);
            customNameEditText = itemView.findViewById(R.id.customNameEditText);
            wikiInfoTextView = itemView.findViewById(R.id.wikiInfoTextView);
            editPlantButton = itemView.findViewById(R.id.editPlantButton);
            deletePlantButton = itemView.findViewById(R.id.deletePlantButton);
            plantImageView = itemView.findViewById(R.id.plantImageView);
        }
    }
}

