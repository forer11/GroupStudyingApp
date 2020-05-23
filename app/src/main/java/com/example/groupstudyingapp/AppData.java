package com.example.groupstudyingapp;

import android.app.Application;

public class AppData extends Application {
    FireStoreHandler fireStoreHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        fireStoreHandler = new FireStoreHandler();
    }
}
