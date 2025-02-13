package com.ejemplo.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlantListActivity extends AppCompatActivity implements PlantAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PlantAdapter adapter;
    private DatabaseReference userPlantsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);

        recyclerView = findViewById(R.id.plantsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userPlantsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("plants");

        loadPlants();
    }

    private void loadPlants() {
        userPlantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PlantModel> plants = new ArrayList<>();
                for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                    PlantModel plant = plantSnapshot.getValue(PlantModel.class);
                    plant.setId(plantSnapshot.getKey());
                    plants.add(plant);
                }
                adapter = new PlantAdapter(plants, PlantListActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PlantListActivity.this, "Error al cargar plantas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(PlantModel plant, String plantId) {
        // Implementar edición
    }

    @Override
    public void onDeleteClick(String plantId) {
        userPlantsRef.child(plantId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Planta eliminada", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}