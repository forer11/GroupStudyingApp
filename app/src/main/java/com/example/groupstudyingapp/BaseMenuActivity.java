package com.example.groupstudyingapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ndroid.nadim.sahel.CoolToast;

import java.util.Objects;

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
                showSignOutDialog();
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

    /**
     * show the sign out dialog on screen
     */
    public void showSignOutDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder
                (BaseMenuActivity.this);
        View view = getLayoutInflater().inflate(R.layout.sign_out_dialog, null);
        setDialogView(dialogBuilder, view);
        final AlertDialog alertdialog = dialogBuilder.create();
        onClickDialog(view, alertdialog);
        Objects.requireNonNull(alertdialog.getWindow()).setBackgroundDrawable
                (new ColorDrawable(Color.TRANSPARENT));
        alertdialog.show();
    }

    /**
     * set the actions on every user selection on the dialog
     *
     * @param view        the view of the dialog
     * @param alertDialog the dialog
     */
    private void onClickDialog(View view, final AlertDialog alertDialog) {
        Button signOut = view.findViewById(R.id.signout_button);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                AppData appData = (AppData) getApplicationContext();
                GoogleSignInClient googleSignInClient = appData.googleSignInClient;
                if (googleSignInClient != null) {
                    googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CoolToast coolToast = new CoolToast(BaseMenuActivity.this);
                            coolToast.make("Signed out", CoolToast.SUCCESS);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CoolToast coolToast = new CoolToast(BaseMenuActivity.this);
                            coolToast.make("Failed to log out", CoolToast.SUCCESS);
                        }
                    });
                }

                Intent intent = new Intent(BaseMenuActivity.this.getBaseContext(), LoginActivity.class);
                BaseMenuActivity.this.startActivity(intent);
                BaseMenuActivity.this.finish();
            }
        });

        Button cancelSignOut = view.findViewById(R.id.cancel_signout);
        cancelSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view12) {
                alertDialog.cancel();

            }
        });
    }

    /**
     * set the layout of the dialog
     *  @param dialogBuilder the builder of the dialog
     * @param view     the view of the layout to be set in the dialog
     */
    private void setDialogView(AlertDialog.Builder dialogBuilder, View view) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            ImageView profile = view.findViewById(R.id.profile_image);
            Uri profileUri = currentUser.getPhotoUrl();

            if (profileUri != null) {
                try {
                    Glide
                            .with(profile)
                            .load(profileUri.toString())
                            .into(profile);
                } catch (Exception e) {
                    // in this case we stay with the default profile photo
                }
            }
            String username = currentUser.getDisplayName();
            if (username != null) {
                TextView user_info = view.findViewById(R.id.user_details);
                if (!username.equals("")) {
                    String userInfoText = username + "\n" + currentUser.getEmail();
                    user_info.setText(userInfoText);
                } else {
                    String number = currentUser.getPhoneNumber();
                    user_info.setText(number);
                }
            }
        }
        dialogBuilder.setView(view);
    }


}
