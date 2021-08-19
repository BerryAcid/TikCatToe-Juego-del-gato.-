package com.berryacid.tikcattoe.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.berryacid.tikcattoe.R;
import com.berryacid.tikcattoe.app.Constantes;
import com.berryacid.tikcattoe.model.Jugada;
import com.berryacid.tikcattoe.providers.AuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class FindGameActivity extends AppCompatActivity {
    private TextView tvLoadingMessage;
    private ProgressBar progressBar;
    private ScrollView layoutProgressbar, layoutMenuJuego;
    private Button btnJugar, btnRankin;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private String uId, jugadaId;
    private ListenerRegistration listenerRegistration = null;
    private LottieAnimationView animationView;
    private ImageView imageViewLogOut;
    private AuthProvider authProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_game);


        layoutProgressbar = findViewById(R.id.layaoutProgresBar);
        layoutMenuJuego = findViewById(R.id.menuJuego);
        btnJugar = findViewById(R.id.buttonPlayGameOnline);
        btnRankin = findViewById(R.id.buttonShowRankin);
        imageViewLogOut = findViewById(R.id.imageviewExitApp);
        authProvider = new AuthProvider();

        imageViewLogOut.setOnClickListener(v ->{
            logoutUser();
        });

        initProgressBar();
        initFireBase();
        eventos();
    }


    private void logoutUser() {
        authProvider.logout();
        Intent intent = new Intent(FindGameActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initFireBase() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uId = firebaseUser.getUid();
    }

    private void eventos() {
        btnJugar.setOnClickListener(v -> {
            changeMenuVisibility(false);
            buscarJugadaLibre();
        });
        btnRankin.setOnClickListener(v -> {
            //Toast.makeText(this, "En construcción.", Toast.LENGTH_SHORT).show();
            goToRanking();
        });
    }

    private void goToRanking(){
        if (listenerRegistration != null){
            listenerRegistration.remove();
        }
        Intent intent = new Intent(FindGameActivity.this, RankinActivity.class);
        //intent.putExtra(Constantes.EXTRA_JUGADA_ID, jugadaId);
        startActivity(intent);
    }

    private void buscarJugadaLibre() {
        tvLoadingMessage.setText("Buscando jugada libre.");
        animationView.playAnimation();
        imageViewLogOut.setVisibility(View.GONE);

        db.collection("jugadas")
                .whereEqualTo("jugadorDosId", "")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().size() == 0) {
                        // No existen partidas libres, crear una nueva
                        crearNuevaJugada();
                    } else {
                        boolean encontrado = false;

                        for ( DocumentSnapshot docJugada : task.getResult().getDocuments()){
                            if (!docJugada.get("jugadorUnoId").equals(uId)) {
                                encontrado = true;
                                jugadaId = docJugada.getId();
                                Jugada jugada = docJugada.toObject(Jugada.class); //Se parcea el objeto para deposiar la info en la app desde FireStore
                                jugada.setJugadorDosId(uId);

                                db.collection("jugadas")
                                        .document(jugadaId)
                                        .set(jugada)
                                        .addOnSuccessListener(aVoid -> {
                                            tvLoadingMessage.setText("!Jugada libre encontrada! Conmienza ahora.");
                                            animationView.setRepeatCount(0);
                                            animationView.setAnimation("checked_animation.json");
                                            animationView.playAnimation();

                                            final Handler handler = new Handler();
                                            final Runnable runnable = new Runnable() {
                                                @Override
                                                public void run() {
                                                    startGame();
                                                }
                                            };
                                            handler.postDelayed(runnable, 1500);

                                        }).addOnFailureListener(e -> {
                                    changeMenuVisibility(true);
                                    imageViewLogOut.setVisibility(View.VISIBLE);
                                    Toast.makeText(this, "Hubo algún error an entrar a la jugada.", Toast.LENGTH_SHORT).show();
                                });
                                break;
                            }
                            if (!encontrado) crearNuevaJugada();
                        }


                    }
                });

    }

    private void crearNuevaJugada() {
        tvLoadingMessage.setText("Creando una jugada nueva.");
        Jugada nuevaJugada = new Jugada(uId);

        db.collection("jugadas")
                .add(nuevaJugada)
                .addOnSuccessListener(documentReference -> {
                    jugadaId = documentReference.getId();
                    // Tenemos creada la jugada, debemos esperar a otro jugador.
                    esperarJugador();
                }).addOnFailureListener(e ->{
            changeMenuVisibility(true);
            Toast.makeText(this, "Error al crear la nueva jugada.", Toast.LENGTH_SHORT).show();
        });
    }

    private void esperarJugador() {
        if (!jugadaId.equals("")) {
            tvLoadingMessage.setText("Esperando contrincante.");
            listenerRegistration = db.collection("jugadas")
                    .document(jugadaId)
                    .addSnapshotListener((value, error) -> {
                        if (!value.get("jugadorDosId").equals("")) {
                            tvLoadingMessage.setText("!Ya ha llegado un jugador¡ Comineza la partida.");
                            animationView.setRepeatCount(0);
                            animationView.setAnimation("checked_animation.json");
                            animationView.playAnimation();

                            final Handler handler = new Handler();
                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    startGame();
                                }
                            };
                            handler.postDelayed(runnable, 1500);
                        }
                    });
        }
    }

    private void startGame() {
        if (listenerRegistration != null){
            listenerRegistration.remove();
        }
        Intent intent = new Intent(FindGameActivity.this, GameActivity.class);
        intent.putExtra(Constantes.EXTRA_JUGADA_ID, jugadaId);
        startActivity(intent);
        jugadaId = "";
    }

    private void initProgressBar() {
        animationView = findViewById(R.id.animation_view);
        tvLoadingMessage = findViewById(R.id.textViewLoading);
        progressBar = findViewById(R.id.progressBarLoading);
        
        progressBar.setIndeterminate(true);
        tvLoadingMessage.setText("Cargando...");
        changeMenuVisibility(true);
    }

    private void changeMenuVisibility(boolean showMenu) {
        layoutProgressbar.setVisibility(showMenu ? View.GONE : View.VISIBLE);
        layoutMenuJuego.setVisibility(showMenu ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(jugadaId != null) {
            changeMenuVisibility(false);
            esperarJugador();
        } else {
            changeMenuVisibility(true);
        }

    }



    @Override
    protected void onStop() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        if (jugadaId != null ) {
            if (!jugadaId.equals("")){
                db.collection("jugadas")
                        .document(jugadaId)
                        .delete()
                        .addOnCompleteListener(task -> {
                            jugadaId = "";
                        });
            }
        }
        super.onStop();
    }
}