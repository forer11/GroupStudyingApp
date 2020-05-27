package com.example.groupstudyingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuestionActivity extends AppCompatActivity {

    private boolean hiddenSolution=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        updateTitleText();
        final Button solutionButton = findViewById(R.id.solutionButton);
        final ImageView solutionImage = findViewById(R.id.solutionImage);
        showSolutionHandler(solutionButton, solutionImage);
    }

    private void updateTitleText() {
        String questionTitle = getIntent().getStringExtra("EXTRA_SESSION_ID"); //change to question id num in firestore
        TextView questionTextView = findViewById(R.id.questionTitle);
        questionTextView.setText("Question " + questionTitle);
    }

    private void showSolutionHandler(final Button solutionButton, final ImageView solutionImage) {
        solutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenSolution){
                    solutionImage.setVisibility(View.VISIBLE);
                    solutionButton.setText("Show solution");
                    hiddenSolution = false;
                } else {
                    solutionImage.setVisibility(View.INVISIBLE);
                    solutionButton.setText("Hide solution");
                    hiddenSolution = true;
                }
            }
        });
    }
}
