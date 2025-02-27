package com.ejemplo.myapplication.services;

import com.ejemplo.myapplication.models.PlantModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class PlantService {

    private DatabaseReference userPlantsRef;

    public PlantService() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userPlantsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("plants");
    }

    // Callback para operaciones de guardado, eliminación, etc.
    public interface OperationCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    // Callback para cargar la lista de plantas
    public interface LoadPlantsCallback {
        void onLoad(List<PlantModel> plants);
        void onError(String error);
    }

    // Guarda una nueva planta en Firebase
    public void savePlant(PlantModel plant, OperationCallback callback) {
        String plantId = userPlantsRef.push().getKey();
        if (plantId == null) {
            callback.onError("Error al generar el ID de la planta.");
            return;
        }
        userPlantsRef.child(plantId).setValue(plant)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Planta guardada con éxito");
                    } else {
                        callback.onError("Error al guardar la planta");
                    }
                });
    }

    // Elimina una planta de Firebase
    public void deletePlant(String plantId, OperationCallback callback) {
        userPlantsRef.child(plantId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess("Planta eliminada");
            } else {
                callback.onError("Error al eliminar la planta");
            }
        });
    }

    // Carga todas las plantas del usuario desde Firebase
    public void loadUserPlants(LoadPlantsCallback callback) {
        userPlantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<PlantModel> plants = new ArrayList<>();
                for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                    PlantModel plant = plantSnapshot.getValue(PlantModel.class);
                    if (plant != null) {
                        plant.setId(plantSnapshot.getKey());
                        plants.add(plant);
                    }
                }
                callback.onLoad(plants);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onError("Error al cargar plantas: " + error.getMessage());
            }
        });
    }

    public void updatePlantCustomName(String plantId, String customName, OperationCallback callback) {
        userPlantsRef.child(plantId).child("customName").setValue(customName)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Nombre actualizado correctamente");
                    } else {
                        callback.onError("Error al actualizar el nombre");
                    }
                });
    }

    public void updatePlantWateringInfo(String plantId, int wateringCycle, long lastWatered, OperationCallback callback) {
        userPlantsRef.child(plantId).child("wateringCycle").setValue(wateringCycle)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userPlantsRef.child(plantId).child("lastWatered").setValue(lastWatered)
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        callback.onSuccess("Información de riego actualizada correctamente");
                                    } else {
                                        callback.onError("Error al actualizar la fecha del último riego");
                                    }
                                });
                    } else {
                        callback.onError("Error al actualizar el ciclo de riego");
                    }
                });
    }



}
