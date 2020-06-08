package com.example.groupstudyingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class BaseMenuActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_bar:
                // implement search call here
                return true;

            case R.id.profile:
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FlatDialog flatDialog = new FlatDialog(BaseMenuActivity.this);
                setDialogData(flatDialog);
                setDialogListeners(firebaseAuth, flatDialog);
                flatDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDialogListeners(final FirebaseAuth firebaseAuth, final FlatDialog flatDialog) {
        flatDialog.withFirstButtonListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                AppData appData = (AppData) getApplicationContext();
                GoogleSignInClient googleSignInClient = appData.googleSignInClient;
                if (googleSignInClient != null) {
                    googleSignInClient.signOut()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(BaseMenuActivity.this,
                                            "Signed out",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(BaseMenuActivity.this,
                                            "Failed to log out",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                Intent intent = new Intent(BaseMenuActivity
                        .this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        flatDialog.withSecondButtonListner(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flatDialog.dismiss();
            }
        });
    }

    private void setDialogData(FlatDialog flatDialog) {
        flatDialog.setCancelable(true);
        flatDialog.setIcon(R.drawable.large_profile_icon);
        flatDialog.setBackgroundColor(Color.parseColor("#F5FFFA"));
        flatDialog.setTitleColor(Color.parseColor("#87CEEB"));
        flatDialog.setSubtitleColor(Color.parseColor("#87CEFA"));
        flatDialog.setTitle("Logout");
        flatDialog.setSubtitle("Sure you want to logout?");
        flatDialog.setFirstButtonText("Yes");
        flatDialog.setSecondButtonText("No");
    }

}
