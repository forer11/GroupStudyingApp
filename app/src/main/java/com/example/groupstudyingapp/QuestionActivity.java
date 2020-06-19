package com.example.groupstudyingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.hsalf.smileyrating.SmileyRating;

public class QuestionActivity extends AppCompatActivity {

    private static final String QUESTION = "question";
    private boolean hiddenSolution = true;
    private boolean hiddenRate = true;
    private String questionRate = "No rate yet";
    private SmileyRating.Type rateType = null;
    private SmileyRating smileyRating;
    private TextView rateText;
    private ImageView questionImageView;
    private ImageView solutionImage;
    private Question question;
    AppData appData;
    FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        loadQuestion();
        if (question.getRating() > 0) {
            questionRate = Float.toString(question.getRating());
        }
        initializeUi();
        TextView questionTextView = findViewById(R.id.questionTitle);
        questionTextView.setText(question.getTitle());

        getAppData();
        fireStoreHandler.setCurrentImagePath(question.getImagePath());

//        Uri questionImage = Uri.parse(question.getImagePath());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();

        Glide.with(this).load(Uri.parse(question.getLink())).placeholder(circularProgressDrawable).into(questionImageView);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

//        questionImageView.setImageURI(questionImage);
        //TODO - get image uri from firestore (solution)
        if (question.getAnswers().size() > 0) {
            solutionImage.setImageURI(Uri.parse(question.getAnswers().get(0).getLink()));
            // todo show all answers
        }
        userRateHandler();
        //TODO - get rate from firestore
        if (rateType != null) {
            updateRate(smileyRating);
        }

    }

    private void initializeUi() {
        final Button solutionButton = findViewById(R.id.solutionButton);
        final Button addAnswerButton = findViewById(R.id.addAnswerButton);
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
        addAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddAnswerActivity.class);
                intent.putExtra(QUESTION, question);
                startActivity(intent);
            }
        });
        questionImageView = findViewById(R.id.questionImage);
        solutionImage = findViewById(R.id.solutionImage);
        showSolutionHandler(solutionButton, solutionImage);
    }

    private void userRateHandler() {
        smileyRating.setSmileySelectedListener(new SmileyRating.OnSmileySelectedListener() {
            @Override
            public void onSmileySelected(SmileyRating.Type type) {
                //TODO - save rate type and rate num to firestore
                rateType = type;
                int numOfRates;
                switch (rateType) {
                    case TERRIBLE:
                        questionRate = "TERRIBLE";
                        numOfRates = question.getNumOfRates();
                        question.setRating((numOfRates * question.getRating() + 1) /
                                            (numOfRates + 1));
                        question.setNumOfRates(numOfRates + 1);
                        break;
                    case BAD:
                        questionRate = "BAD";
                        numOfRates = question.getNumOfRates();
                        question.setRating((numOfRates * question.getRating() + 2) /
                                (numOfRates + 1));
                        question.setNumOfRates(numOfRates + 1);
                        break;
                    case OKAY:
                        questionRate = "OKAY";
                        numOfRates = question.getNumOfRates();
                        question.setRating((numOfRates * question.getRating() + 3) /
                                (numOfRates + 1));
                        question.setNumOfRates(numOfRates + 1);
                        break;
                    case GOOD:
                        questionRate = "GOOD";
                        numOfRates = question.getNumOfRates();
                        question.setRating((numOfRates * question.getRating() + 4) /
                                (numOfRates + 1));
                        question.setNumOfRates(numOfRates + 1);
                        break;
                    case GREAT:
                        questionRate = "GREAT";
                        numOfRates = question.getNumOfRates();
                        question.setRating((numOfRates * question.getRating() + 5) /
                                (numOfRates + 1));
                        question.setNumOfRates(numOfRates + 1);
                        break;
                }
                rateText.setText("Question Rate: "+ question.getRating());
                fireStoreHandler.updateQuestion(question);
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

    private void getAppData() {
        appData = (AppData) getApplicationContext();
        fireStoreHandler = appData.fireStoreHandler;
    }
}
