package com.ejemplo.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText email, clave;
    Button registrarse, acceder;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailEditText);
        clave = findViewById(R.id.passwordEditText);
        registrarse = findViewById(R.id.signUpButton);
        acceder = findViewById(R.id.logInButton);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View x) {
                String emailUser = email.getText().toString().trim();
                String password = clave.getText().toString().trim();

                if (emailUser.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, ingresa los datos", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(emailUser, password);
                }
            }
        });

        acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser = email.getText().toString().trim();
                String password = clave.getText().toString().trim();

                if (emailUser.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, ingresa los datos", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(emailUser, password);
                }
            }
        });
    }

    private void registerUser(String emailUser, String password) {
        mAuth.createUserWithEmailAndPassword(emailUser, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            redirectToPlantActivity();
                        } else {
                            Toast.makeText(MainActivity.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al registrar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String emailUser, String password) {
        mAuth.signInWithEmailAndPassword(emailUser, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            redirectToPlantActivity();
                        } else {
                            Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToPlantActivity() {
        Intent intent = new Intent(MainActivity.this, PlantActivity.class);
        startActivity(intent);
        finish(); // Finaliza esta actividad para que no se pueda volver a ella con el botón "Atrás"
    }
}
