package com.example.groupstudyingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OpeningScreenActivity extends AppCompatActivity {
    AppData appData;
    FirebaseAuth firebaseAuth;
    private BroadcastReceiver br;
    private boolean registered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);
        getAppData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setBroadcastReceiver();
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

    private void setBroadcastReceiver() {
        if (appData.fireStoreHandler.numOfCourses != null
                && appData.fireStoreHandler.size == appData.fireStoreHandler.numOfCourses.get()) {
            openNextActivity();
        } else {
            br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(FireStoreHandler.LOADING_DATA_SUCCESS)) {
                        openNextActivity();
                    }

                    if (intent.getAction().equals(FireStoreHandler.FAILED_LOADING_DATA)) {
                        setDataLoadingFailureDialog();
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(FireStoreHandler.LOADING_DATA_SUCCESS);
            filter.addAction(FireStoreHandler.FAILED_LOADING_DATA);
            registerReceiver(br, filter);
            registered = true;
        }
    }

    private void openNextActivity() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        checkLoginStatus(currentUser);
        finish();
    }

    private void setDataLoadingFailureDialog() {
        final FlatDialog flatDialog = new FlatDialog(OpeningScreenActivity.this);
        flatDialog.setCancelable(false);
        flatDialog.setTitle("Oops, failure loading data");
        flatDialog.setSubtitle("Please check your internet connection");
        flatDialog.setFirstButtonText("Ok got it");
        flatDialog.withFirstButtonListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        flatDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registered) {
            this.unregisterReceiver(br);
            registered = false;
        }
        appData.fireStoreHandler.sentOnce = false;
    }

    @Override
    public void onBackPressed() {
        // we do not allow back press here
    }
}
