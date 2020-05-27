package com.example.groupstudyingapp;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

public class AppData extends Application {
    FireStoreHandler fireStoreHandler;
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        fireStoreHandler = new FireStoreHandler();
        firebaseAuth = FirebaseAuth.getInstance();
    }
}
