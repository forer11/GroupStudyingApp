package com.example.groupstudyingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import com.bumptech.glide.Glide;
import com.example.flatdialoglibrary.dialog.FlatDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseMenuActivity extends AppCompatActivity {
    CircleImageView profileImage;
    private FrameLayout profileLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.profile);
        View view = menuItem.getActionView();
        profileImage = view.findViewById(R.id.toolbar_profile_image);
        profileLayout = view.findViewById(R.id.profile_image_layout);
        setProfileImageListener();

        return true;
    }

    private void setProfileImageListener() {
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FlatDialog flatDialog = new FlatDialog(BaseMenuActivity.this);
                setDialogData(flatDialog);
                setDialogListeners(firebaseAuth, flatDialog);
                flatDialog.show();
            }
        });
    }

    protected void setProfile() {
        AppData appData = (AppData) getApplicationContext();
        appData.isAnonymous = true;
        if (appData.user != null
                && appData.user.getEmail() != null
                && !appData.user.getEmail().equals("")) {
            appData.isAnonymous = false;
            setProfileImageWithUrl(appData);
            appData.fireStoreHandler.createUserIfNotExists(appData.user);
        }
    }

    private void setProfileImageWithUrl(AppData appData) {
        Uri uri = appData.user.getPhotoUrl();
        if (uri != null) {
            Glide
                    .with(profileImage)
                    .load(uri.toString())
                    .into(profileImage);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_bar:
                // implement search call here
                return true;

            case R.id.profile:
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
