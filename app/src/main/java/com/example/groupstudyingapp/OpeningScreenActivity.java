package com.example.groupstudyingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OpeningScreenActivity extends AppCompatActivity {
    AppData appData;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);

        getAppData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // checks if user signed in, if so go to Main else go to login
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        checkLoginStatus(currentUser);
    }

    private void checkLoginStatus(FirebaseUser currentUser) {
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    private void getAppData() {
        appData = (AppData) getApplicationContext();
        firebaseAuth = appData.firebaseAuth;
    }
}
