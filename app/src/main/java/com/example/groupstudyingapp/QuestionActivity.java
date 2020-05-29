package com.example.groupstudyingapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.hsalf.smileyrating.SmileyRating;

public class QuestionActivity extends AppCompatActivity {

    private boolean hiddenSolution = true;
    private boolean hiddenRate = true;
    private String questionRate = "No rate yet";
    private SmileyRating.Type rateType = null;
    private SmileyRating smileyRating;
    private TextView rateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        updateTitleText();
        initializeUi();
        userRateHandler();
        if (rateType != null) {
            updateRate(smileyRating);
        }

    }

    private void initializeUi() {
        final Button solutionButton = findViewById(R.id.solutionButton);
        final ImageView solutionImage = findViewById(R.id.solutionImage);
        showSolutionHandler(solutionButton, solutionImage);
        smileyRating = findViewById(R.id.smileyRating);
        rateText = findViewById(R.id.questionRate);
        rateText.setText("Question Rate: "+questionRate);
        Button rateButton = findViewById(R.id.rateButton);
        LottieAnimationView studentAnimation = findViewById(R.id.studentAnimation);
        studentAnimation.setProgress(0);
        studentAnimation.playAnimation();


        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenRate) {
                    smileyRating.setVisibility(View.VISIBLE);
                    hiddenRate = false;
                } else {
                    smileyRating.setVisibility(View.INVISIBLE);
                    hiddenRate = true;
                }
            }
        });
    }

    private void userRateHandler() {
        smileyRating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                //TODO - save rate type and rate num to firestore
                rateType = type;
                switch (rateType) {
                    case TERRIBLE:
                        questionRate = "TERRIBLE";
                        break;

                    case BAD:
                        questionRate = "BAD";
                        break;
                    case OKAY:
                        questionRate = "OKAY";
                        break;
                    case GOOD:
                        questionRate = "GOOD";
                        break;
                    case GREAT:
                        questionRate = "GREAT";
                        break;
                }
                rateText.setText("Question Rate: "+questionRate);
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
