package com.ejemplo.myapplication.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.ejemplo.myapplication.R;
import com.ejemplo.myapplication.models.PlantModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class WateringWorker extends Worker {

    public static final String CHANNEL_ID = "watering_notifications";

    public WateringWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Consulta a Firebase para obtener las plantas del usuario y verificar si deben regarse.
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId).child("plants");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PlantModel> plantsToWater = new ArrayList<>();
                long currentTime = System.currentTimeMillis();
                for (DataSnapshot plantSnapshot : snapshot.getChildren()) {
                    PlantModel plant = plantSnapshot.getValue(PlantModel.class);
                    if (plant != null && plant.getWateringCycle() > 0) {
                        long nextWaterTime = plant.getLastWatered() + plant.getWateringCycle() * 24L * 60 * 60 * 1000;
                        if (currentTime >= nextWaterTime) {
                            plantsToWater.add(plant);
                        }
                    }
                }
                if (!plantsToWater.isEmpty()) {
                    // Crear notificación
                    createNotification("Riego de plantas", "Tienes " + plantsToWater.size() + " planta(s) que necesitan ser regadas.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error
            }
        });

        return Result.success();
    }

    private void createNotification(String title, String content) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Crear canal de notificación para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Riego de plantas";
            String description = "Notificaciones para riego de plantas";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_watering) // Asegúrate de tener este recurso
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(1, builder.build());
    }
}
