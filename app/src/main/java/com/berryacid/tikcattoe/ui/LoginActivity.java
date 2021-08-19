package com.berryacid.tikcattoe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.berryacid.tikcattoe.R;
import com.berryacid.tikcattoe.providers.AuthProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoRegistrar;
    private ScrollView formLogin;
    private ProgressBar pbLogin;
    private FirebaseAuth firebaseAuth;
    private String email, password;
    boolean tryLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonRegistro);
        formLogin = findViewById(R.id.formRegistro);
        pbLogin = findViewById(R.id.progressBar);
        btnGoRegistrar = findViewById(R.id.buttonGoRegistrar);


        firebaseAuth = FirebaseAuth.getInstance();

        changeLoginFormVisibility(true);
        eventos();
    }

    private void eventos() {
        btnLogin.setOnClickListener(v -> {
             email = etEmail.getText().toString();
             password = etPassword.getText().toString();

            if  (email.isEmpty()){
                etEmail.setError("El e-mail es obligatorio.");
            } else if (password.isEmpty()) {
                etPassword.setError("La contraseña es obligatoria.");
            } else {
                //TODO: realizar login en Firebase Auth
                loginUser();
            }

        });

        btnGoRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });


    }



    private void loginUser() {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        tryLogin = true;
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w("TAG", "signInError: ", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void changeLoginFormVisibility(boolean showForm) {
        pbLogin.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formLogin.setVisibility(showForm ? View.VISIBLE : View.GONE);

    }

    private void updateUI(FirebaseUser user) {
        if (user != null){
            //Almacenar la información del usuario en FireStore
            //TODO

            //Navegar hacia la siguiente pantalla de la aplicación
            Intent intent = new Intent(LoginActivity.this, FindGameActivity.class);
            startActivity(intent);
        } else {
            changeLoginFormVisibility(true);
            if (tryLogin) {
                etPassword.setError("Nombre, Email y/o contraseña incorrectos.");
                etPassword.requestFocus();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Comprobamos si previamneten el susuario ya ha inciado sesión en
        // este dispositivo.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }
}