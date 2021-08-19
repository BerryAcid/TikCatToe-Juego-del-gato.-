package com.berryacid.tikcattoe.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.berryacid.tikcattoe.model.User;
import com.berryacid.tikcattoe.R;
import com.berryacid.tikcattoe.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;


public class RankingAdapter extends FirestoreRecyclerAdapter<User, RankingAdapter.ViewHolder> {

    Context context;
    UsersProvider userProvider;
    ListenerRegistration mListener;

    public RankingAdapter(FirestoreRecyclerOptions<User> options, Context context){
        super(options);
        this.context = context;
        userProvider = new UsersProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull User user) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        final String userId = documentSnapshot.getId();
        final String positionNumber = String.valueOf(position + 1);
        holder.textViewPositionNumber.setText(positionNumber);

        getRanking(userId, holder);
    }

    private void getRanking(String idDocument, final RankingAdapter.ViewHolder holder) {
        mListener = userProvider.getUser(idDocument).addSnapshotListener((documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("name")) {
                        String username = documentSnapshot.getString("name");
                        holder.textViewNamePlayer.setText(username);
                    }
                    if (documentSnapshot.contains("point")) {
                        String point = String.valueOf(documentSnapshot.getLong("point"));
                        holder.textViewPointsPlayer.setText(point);
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_ranking, parent, false);
        return new ViewHolder(view);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNamePlayer;
        TextView textViewPointsPlayer;
        TextView textViewPositionNumber;
        View viewHolder;


        public ViewHolder(View view) {
            super(view);
            textViewNamePlayer = view.findViewById(R.id.textViewNameRanking);
            textViewPointsPlayer = view.findViewById(R.id.textViewPointsRanking);
            textViewPositionNumber = view.findViewById(R.id.textviewNumberPosition);
            viewHolder = view;

        }

    }

}

