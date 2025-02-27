package com.ejemplo.myapplication.adapters;

import android.app.DatePickerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ejemplo.myapplication.R;
import com.ejemplo.myapplication.models.PlantModel;
import com.ejemplo.myapplication.services.PlantService;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;
import android.net.Uri;

import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {
    public interface OnItemClickListener {
        void onEditClick(PlantModel plant, String plantId);
        void onDeleteClick(String plantId);
    }
    private List<PlantModel> plantList;
    private OnItemClickListener listener;
    private PlantService plantService;
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
                        }
                        @Override
                        public void onError(String error) {
                        }
                    });
                }
            }
        });
        // Muestra la información extra (Wiki) en el TextView
        holder.wikiInfoTextView.setText(plant.getInfoText());
        // Cargar la imagen usando Glide
        String uriString = plant.getImageUri();
        if (uriString != null && !uriString.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(uriString))
                    .placeholder(R.drawable.default_plant_image)
                    .into(holder.plantImageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.default_plant_image)
                    .into(holder.plantImageView);
        }

        // Calcular y actualizar el mensaje de riego
        long currentTime = System.currentTimeMillis();
        long nextWaterTime = plant.getLastWatered() + plant.getWateringCycle() * 24L * 60 * 60 * 1000;
        int diasRestantes = (int) ((nextWaterTime - currentTime) / (24L * 60 * 60 * 1000));
        diasRestantes = Math.max(diasRestantes, 0);
        holder.wateringInfoTextView.setText("Faltan " + diasRestantes + " días para regarla!");

        holder.editPlantButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_watering_cycle, null);

            EditText cycleEditText = dialogView.findViewById(R.id.cycleEditText);
            TextView lastWateredTextView = dialogView.findViewById(R.id.lastWateredTextView);
            Button selectDateButton = dialogView.findViewById(R.id.selectDateButton);

            final long[] selectedTimestamp = {0};

            // Configurar el botón para elegir fecha
            selectDateButton.setOnClickListener(v1 -> {
                // Usamos un DatePickerDialog para seleccionar la fecha
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year, month, dayOfMonth) -> {
                    java.util.Calendar selectedDate = java.util.Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    // Actualizamos el TextView con la fecha seleccionada
                    lastWateredTextView.setText(android.text.format.DateFormat.getDateFormat(v.getContext()).format(selectedDate.getTime()));
                    // Guardamos el timestamp
                    selectedTimestamp[0] = selectedDate.getTimeInMillis();
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            });

            builder.setTitle("Configurar riego")
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String cycleStr = cycleEditText.getText().toString().trim();
                        if (cycleStr.isEmpty()) {
                            Toast.makeText(v.getContext(), "Ingresa el número de días", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int cycleDays = Integer.parseInt(cycleStr);
                        // Si el usuario no seleccionó una fecha, puedes usar la fecha actual
                        long lastWateredTime = selectedTimestamp[0] != 0 ? selectedTimestamp[0] : System.currentTimeMillis();

                        // Actualizar la planta y guardar ambos valores en Firebase
                        plant.setWateringCycle(cycleDays);
                        plant.setLastWatered(lastWateredTime);
                        plantService.updatePlantWateringInfo(plant.getId(), cycleDays, lastWateredTime, new PlantService.OperationCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(v.getContext(), "Riego actualizado", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onError(String error) {
                                Toast.makeText(v.getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
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
        TextView wateringInfoTextView; // Agregado para mostrar días restantes
        ImageButton editPlantButton, deletePlantButton;
        ImageView plantImageView;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            scientificNameTextView = itemView.findViewById(R.id.scientificNameTextView);
            customNameEditText = itemView.findViewById(R.id.customNameEditText);
            wikiInfoTextView = itemView.findViewById(R.id.wikiInfoTextView);
            wateringInfoTextView = itemView.findViewById(R.id.wateringInfoTextView);
            editPlantButton = itemView.findViewById(R.id.editPlantButton);
            deletePlantButton = itemView.findViewById(R.id.deletePlantButton);
            plantImageView = itemView.findViewById(R.id.plantImageView);
        }
    }
}
