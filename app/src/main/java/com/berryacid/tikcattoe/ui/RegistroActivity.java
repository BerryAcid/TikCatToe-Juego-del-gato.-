package com.berryacid.tikcattoe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.berryacid.tikcattoe.R;
import com.berryacid.tikcattoe.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroActivity extends AppCompatActivity {
    EditText etEmail, etPassword, etName;
    TextView tvGoLogin;
    Button btnRegistar;
    ScrollView formRegistro;
    ProgressBar pbRegistrar;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etName = findViewById(R.id.editTextName);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnRegistar = findViewById(R.id.buttonRegistro);
        formRegistro = findViewById(R.id.formRegistro);
        pbRegistrar = findViewById(R.id.progressBarRegistro);
        tvGoLogin = findViewById(R.id.textViewGoLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        changeRegistroFormVisibility(true);
        eventos();
    }

    private void eventos() {
        btnRegistar.setOnClickListener(v -> {
            name = etName.getText().toString();
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();

            if (name.isEmpty()){
                etName.setError("El nombre es obligatorio.");
            } else if (email.isEmpty()){
                etEmail.setError("El e-mail es obligatorio.");
            } else if (password.isEmpty()) {
                etPassword.setError("La contraseña es obligatoria.");
            } else {
                //TODO: realizar registro en Firebase Auth
                createUser();
            }
        });
        tvGoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void createUser() {
        changeRegistroFormVisibility(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistroActivity.this, "Error en el registro.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            //Almacenar la información del usuario en FireStore
            User nuevoUsuario = new User(name, 0, 0);

            db.collection("users")
                    .document(user.getUid())
                    .set(nuevoUsuario)
                    .addOnSuccessListener(aVoid -> {
                        //Navegar hacia la siguiente pantalla de la aplicación
                        finish();
                        Intent intent = new Intent(RegistroActivity.this, FindGameActivity.class);
                        startActivity(intent);
                    });
        } else {
            changeRegistroFormVisibility(true);
            etPassword.setError("Nombre, Email y/o contraseña incorrectos.");
            etPassword.requestFocus();
        }
    }

    private void changeRegistroFormVisibility(boolean showForm) {
        pbRegistrar.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formRegistro.setVisibility(showForm ? View.VISIBLE : View.GONE);

    }
}