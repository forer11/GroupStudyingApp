package com.example.groupstudyingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.hsalf.smileyrating.SmileyRating;

public class QuestionActivity extends AppCompatActivity {


    public static final String FINISHED_UPLOAD_ANSWER_IMG = "finished upload answers' image";
    public static final String FAILED_TO_UPLOAD_ANSWER_IMG = "failed to upload answers' image";
    public static final String UPLOAD_ANSWER = "start uploading answer";
    public static final String NO_ANSWER_MSG = "No answer yet";

    public static final String QUESTION_ID = "question_id";
    public static final String TITLE = "title";
    public static final String COURSE_ID = "courseId";
    public static final String UPDATED_URL = "UPDATED URL";
    private String questionRate = "No rate yet";


    private boolean hiddenSolution = true;
    private boolean hiddenRate = true;
    private boolean hasAnswer = false;
    private boolean answerRated = false;
    private SmileyRating.Type rateType = null;
    private SmileyRating smileyRating;
    private TextView rateText;
    private ImageView questionImageView;
    private ImageView solutionImage;
    private Question question;
    private Course course;
    AppData appData;
    FireStoreHandler fireStoreHandler;

    /** The broadcast receiver of the activity **/
    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getAppData();
        loadQuestionAndCourse();
        if (question.getRating() > 0) {
            questionRate = Float.toString(question.getRating());
        }
        initializeUi();
        TextView questionTextView = findViewById(R.id.questionTitle);
        questionTextView.setText(question.getTitle());

//        getAppData();
        fireStoreHandler.setCurrentImagePath(question.getImagePath()); //todo needed?

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();

        Glide.with(this).load(Uri.parse(question.getLink())).placeholder(circularProgressDrawable).into(questionImageView);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);


        if (question.getAnswers().size() > 0) {
            Answer answer = question.getAnswers().get(0);
            solutionImage.setImageURI(Uri.parse(answer.getImagePath()));
            TextView answerRateText = findViewById(R.id.solutionRateText);
            answerRateText.setText(Integer.toString((int)answer.getRating()));
            // todo show all answers
        }
        userRateHandler();
        //TODO - get rate from firestore
        if (rateType != null) {
            updateRate(smileyRating);
        }

        setupBroadcastReceiver();

    }

    private void setupBroadcastReceiver() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(FINISHED_UPLOAD_ANSWER_IMG)) {
                    String answerUrl = intent.getStringExtra(UPDATED_URL);
                    String title = intent.getStringExtra(TITLE);
                    fireStoreHandler.addNewAnswer(question.getId(), title, answerUrl);
//                    addNewQuestion(title, questionUrl);
//                    fireStoreHandler.updateCourse(course.getId());
                }
            }
        };
        IntentFilter filter = new IntentFilter(FINISHED_UPLOAD_ANSWER_IMG);
        this.registerReceiver(br, filter);
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
                intent.putExtra(QUESTION_ID, question.getId());
                intent.putExtra(COURSE_ID, course.getId());
                startActivity(intent);
            }
        });

        questionImageView = findViewById(R.id.questionImage);
        setAnswerAndShow(solutionButton);

    }

    private void setAnswerAndShow(final Button solutionButton) {
        solutionImage = findViewById(R.id.solutionImage);
        if (question.getAnswers().size() > 0) {
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
            circularProgressDrawable.setStrokeWidth(10f);
            circularProgressDrawable.setCenterRadius(60f);
            circularProgressDrawable.start();
            Glide.with(this).load(Uri.parse(question.getAnswers().get(0).getImagePath())).placeholder(circularProgressDrawable).into(solutionImage);
            hasAnswer = true;
        }
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

    private void loadQuestionAndCourse() {
        Intent intent = this.getIntent();
        String questionId =  intent.getStringExtra(QUESTION_ID);
        question = fireStoreHandler.getQuestionById(questionId);
        String courseId = intent.getStringExtra(COURSE_ID);
        course = fireStoreHandler.getCourseById(courseId);
    }

    private void showSolutionHandler(final Button solutionButton, final ImageView solutionImage) {
        final Button answerLikeButton = findViewById(R.id.solutionLikeButton);
        final LinearLayout answerBox = findViewById(R.id.solutionRate);
        final TextView answerRateText = findViewById(R.id.solutionRateText);

        answerLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (question.getAnswers().size() > 0 && !answerRated) {
                    Answer answer = question.getAnswers().get(0);
                    answer.setRating(answer.getRating() + 1);
                    answerRateText.setText(Integer.toString((int)answer.getRating()));
                    answerLikeButton.setBackground(getResources().getDrawable(R.drawable.like2));
                    fireStoreHandler.updateQuestion(question);
                    answerRated = true;
                }
            }
        });
        solutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenSolution) {
                    if (hasAnswer) {
                        solutionImage.setVisibility(View.VISIBLE);
                        answerBox.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(QuestionActivity.this, NO_ANSWER_MSG,
                                Toast.LENGTH_LONG).show();
                    }
                    solutionButton.setText("Hide solution");
                    hiddenSolution = false;
                } else {
                    if (hasAnswer) {
                        solutionImage.setVisibility(View.INVISIBLE);
                        answerBox.setVisibility(View.INVISIBLE);
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(br);
    }
}
