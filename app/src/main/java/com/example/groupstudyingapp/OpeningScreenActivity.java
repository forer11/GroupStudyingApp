package com.example.groupstudyingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OpeningScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
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

    /**
     * Tune and start the activity according to cls.
     * @param cls
     */
    private void tuneActivity(Class<?> cls){
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
    }

    private void checkLoginStatus(FirebaseUser currentUser) {
        if (currentUser != null) {
            tuneActivity(MainActivity.class);
        } else {
            tuneActivity(LoginActivity.class);
        }
    }

    private void getAppData() {
        appData = (AppData) getApplicationContext();
        firebaseAuth = appData.firebaseAuth;
    }
}
