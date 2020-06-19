package com.example.groupstudyingapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.SigningInfo;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class AppData extends Application {
    FireStoreHandler fireStoreHandler;
    FirebaseAuth firebaseAuth;
    FirebaseUser user = null;
    GoogleSignInClient googleSignInClient;


    @Override
    public void onCreate() {
        super.onCreate();

        fireStoreHandler = new FireStoreHandler(this.getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();
        configureGoogleSignIn();
        userStateListener();
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("90698286473-jka6c86v9q58sk519gno7uqjc245tfa0.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void userStateListener() {
        final FirebaseAuth.IdTokenListener userListener = new FirebaseAuth.IdTokenListener() {
            @Override
            public void onIdTokenChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    if(user.getEmail()!=null){

                    }

                }
            }
        };
        firebaseAuth.addIdTokenListener(userListener);
    }

    //TODO:Lior find suitable location for this function
    @SuppressLint("DefaultLocale")
    public static String getRandomId() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
