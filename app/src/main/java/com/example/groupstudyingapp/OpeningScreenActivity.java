package com.example.groupstudyingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OpeningScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000עןא;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // checks if user signed in, if so go to Main else go to login
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                checkLoginStatus(currentUser);
                finish();
            }
        }, SPLASH_TIME_OUT);
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
