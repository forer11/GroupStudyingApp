package com.example.groupstudyingapp;

import android.content.Intent;
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
    private ImageView questionImage;
    private ImageView solutionImage;
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        initializeUi();
        loadQuestion();
        TextView questionTextView = findViewById(R.id.questionTitle);
        questionTextView.setText("Question " + question.getTitle());
        //TODO - get image uri from firestore (question)
//        questionImage.setImageURI("");
        //TODO - get image uri from firestore (solution)
//        solutionImage.setImageURI("");
        userRateHandler();
        //TODO - get rate from firestore
        if (rateType != null) {
            updateRate(smileyRating);
        }

    }

    private void initializeUi() {
        final Button solutionButton = findViewById(R.id.solutionButton);
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
        questionImage = findViewById(R.id.questionImage);
        solutionImage = findViewById(R.id.solutionImage);
        showSolutionHandler(solutionButton, solutionImage);
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
        }, 500); // we need the delay so the rate will be updated
    }

    private void loadQuestion() {
        Intent intent = this.getIntent();
        question = (Question) intent.getSerializableExtra("EXTRA_SESSION_ID");
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
