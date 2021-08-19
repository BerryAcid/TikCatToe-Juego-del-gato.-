package com.berryacid.tikcattoe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.berryacid.tikcattoe.R;
import com.berryacid.tikcattoe.adapters.RankingAdapter;
import com.berryacid.tikcattoe.model.User;
import com.berryacid.tikcattoe.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class RankinActivity extends AppCompatActivity {

    RecyclerView recyclerViewRankingPlayers;
    RankingAdapter rankingAdapter;
    UsersProvider usersProvider;
    private LinearLayoutManager linearLayoutManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rankin);

        recyclerViewRankingPlayers = findViewById(R.id.recyclerViewRanking);
        usersProvider = new UsersProvider();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Rankin de jugadores");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RankinActivity.this);
        recyclerViewRankingPlayers.setLayoutManager(linearLayoutManager);


    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = usersProvider.getAllPointsUsers();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        rankingAdapter = new RankingAdapter(options, RankinActivity.this);
        recyclerViewRankingPlayers.setAdapter(rankingAdapter);
        rankingAdapter.notifyDataSetChanged();
        rankingAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        rankingAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}