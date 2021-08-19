package com.berryacid.tikcattoe.providers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProvider {
    private FirebaseAuth firebaseAuth;

    public  AuthProvider () {
        firebaseAuth =  FirebaseAuth.getInstance();
    }

    public Task<AuthResult> register(String email, String password){
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password){
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<Void> sendPasswordResetEmail(String email){
        firebaseAuth.setLanguageCode("es");
        return firebaseAuth.sendPasswordResetEmail(email);
    }

    public Task<AuthResult>googleLogin(GoogleSignInAccount googleSignInAccount){
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        return firebaseAuth.signInWithCredential(credential);
    }

    public String getUid(){
        if (firebaseAuth.getCurrentUser() != null){
            return firebaseAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }
    public String getEmail(){
        if (firebaseAuth != null){
            return firebaseAuth.getCurrentUser().getEmail();
        } else {
            return null;
        }
    }

    public FirebaseUser getFirebaseUser(){
        if (firebaseAuth.getCurrentUser() != null){
            return firebaseAuth.getCurrentUser();
        }
        else {
            return null;
        }
    }

    public void logout(){
        if (firebaseAuth != null){
            firebaseAuth.signOut();
        }
    }
}
