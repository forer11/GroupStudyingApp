package com.example.groupstudyingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.nadim.sahel.CoolToast;

public class SendEmailActivity extends AppCompatActivity {

    public static final String EMAIL_TITLE = "emailTitle";
    public static final String EMAIL_BODY = "email_body";
    private EditText titleEditText;
    private EditText messageEditText;
    private String[] recipients = new String[]{"iporat08@gmail.com"}; //todo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        messageEditText = (EditText) findViewById(R.id.emailContentEditText);
        final Button sendEmailButton = findViewById(R.id.sendEmailButton);
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");

                String title = titleEditText.getText().toString();
                String body = messageEditText.getText().toString();

                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                emailIntent.putExtra(Intent.EXTRA_TEXT, body);

                Intent chooser = Intent.createChooser(emailIntent, "Send mail...");
                 try{
                     startActivity(chooser);
                     finish();
                 }catch (android.content.ActivityNotFoundException ex) {
                     CoolToast coolToast = new CoolToast(getApplicationContext());
                     coolToast.make("There is no email client installed.", CoolToast.WARNING);
                 }

            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMAIL_TITLE, titleEditText.getText().toString());
        outState.putString(EMAIL_BODY, messageEditText.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String title = savedInstanceState.getString(EMAIL_TITLE);
        String emailBody = savedInstanceState.getString(EMAIL_BODY);
        titleEditText.setText(title);
        messageEditText.setText(emailBody);
    }
}