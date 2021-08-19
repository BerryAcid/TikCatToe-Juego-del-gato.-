package com.berryacid.tikcattoe.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class JugadaProvider {

    private CollectionReference collection;

    public JugadaProvider(){
        collection = FirebaseFirestore.getInstance().collection("jugadas");
    }

    public Task<Void> updateExitPlayer(String idJugada, String playingLose, String playingWin){
        Map<String, Object> map = new HashMap<>();
        map.put("abandonoId", playingLose);
        map.put("ganadorId", playingWin);
        return collection.document(idJugada).update(map);
    }

}
