package com.example.groupstudyingapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hsalf.smileyrating.SmileyRating;

public class QuestionActivity extends AppCompatActivity {

    private boolean hiddenSolution = true;
    private int rating=0;
    private SmileyRating.Type rateType=null;
    private SmileyRating smileyRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        updateTitleText();
        final Button solutionButton = findViewById(R.id.solutionButton);
        final ImageView solutionImage = findViewById(R.id.solutionImage);
        showSolutionHandler(solutionButton, solutionImage);
        smileyRating = findViewById(R.id.smileyRating);
        Button rateButton = findViewById(R.id.rateButton);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smileyRating.setVisibility(View.VISIBLE);
            }
        });
        userRateHandler();
        if (rateType != null) {
            updateRate(smileyRating);
        }

    }

    private void userRateHandler() {
        smileyRating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                //TODO - save rate type and rate num to firestore
                rateType = type;
                rating = type.getRating();
            }
        });
    }

    private void updateRate(final SmileyRating smileyRating) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                smileyRating.setRating(rateType, true);
            }
        }, 500);
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
                if (hiddenSolution) {
                    solutionImage.setVisibility(View.VISIBLE);
                    solutionButton.setText("Hide solution");
                    hiddenSolution = false;
                } else {
                    solutionImage.setVisibility(View.INVISIBLE);
                    solutionButton.setText("Show solution");
                    hiddenSolution = true;
                }
            }
        });
    }
}
