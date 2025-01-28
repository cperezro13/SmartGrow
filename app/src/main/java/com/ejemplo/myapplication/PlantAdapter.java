package com.ejemplo.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.PlantViewHolder> {

    public interface OnItemClickListener {
        void onEditClick(PlantModel plant, String plantId);
        void onDeleteClick(String plantId);
    }

    private List<PlantModel> plantList;
    private OnItemClickListener listener;

    public PlantAdapter(List<PlantModel> plantList, OnItemClickListener listener) {
        this.plantList = plantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        PlantModel plant = plantList.get(position);

        holder.plantNameTextView.setText(plant.getName());
        holder.plantDescriptionTextView.setText(plant.getDescription());

        // Asigna los listeners para editar y eliminar
        holder.editPlantButton.setOnClickListener(v -> listener.onEditClick(plant, plant.getId()));
        holder.deletePlantButton.setOnClickListener(v -> listener.onDeleteClick(plant.getId()));
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView plantNameTextView, plantDescriptionTextView;
        Button editPlantButton, deletePlantButton;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            plantNameTextView = itemView.findViewById(R.id.plantNameTextView);
            plantDescriptionTextView = itemView.findViewById(R.id.plantDescriptionTextView);
            editPlantButton = itemView.findViewById(R.id.editPlantButton);
            deletePlantButton = itemView.findViewById(R.id.deletePlantButton);
        }
    }
}
