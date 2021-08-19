package com.berryacid.tikcattoe.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class UsersProvider {
    private CollectionReference collection;

    public UsersProvider(){
        collection = FirebaseFirestore.getInstance().collection("users");
    }

    public DocumentReference getUser(String id){
        return collection.document(id);
    }
    public Task<DocumentSnapshot> getAllUser(){
        return collection.document().get();
    }

    public Query getAllPointsUsers(){
        return collection.orderBy("point", Query.Direction.DESCENDING);
    }

    public Task<Void> updatePrivateAccount(String iduser, boolean status){
        Map<String, Object> map = new HashMap<>();
        map.put("privateAccount", status);
        return collection.document(iduser).update(map);
    }
}
