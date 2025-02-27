package com.ejemplo.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.ejemplo.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email, clave;
    private Button registrarse, acceder;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.emailEditText);
        clave = findViewById(R.id.passwordEditText);
        registrarse = findViewById(R.id.signUpButton);
        acceder = findViewById(R.id.logInButton);

        registrarse.setOnClickListener(this::onRegisterClicked);
        acceder.setOnClickListener(this::onLoginClicked);
    }

    private void onRegisterClicked(View view) {
        String emailUser = email.getText().toString().trim();
        String password = clave.getText().toString().trim();

        if (emailUser.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Por favor, ingresa los datos", Toast.LENGTH_SHORT).show();
        } else {
            registerUser(emailUser, password);
        }
    }

    private void onLoginClicked(View view) {
        String emailUser = email.getText().toString().trim();
        String password = clave.getText().toString().trim();

        if (emailUser.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Por favor, ingresa los datos", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(emailUser, password);
        }
    }

    private void registerUser(String emailUser, String password) {
        mAuth.createUserWithEmailAndPassword(emailUser, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        redirectToPlantActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Error al registrar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loginUser(String emailUser, String password) {
        mAuth.signInWithEmailAndPassword(emailUser, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        redirectToPlantActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void redirectToPlantActivity() {
        Intent intent = new Intent(LoginActivity.this, PlantActivity.class);
        startActivity(intent);
        finish();
    }

}
