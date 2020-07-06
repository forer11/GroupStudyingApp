package com.example.groupstudyingapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.hsalf.smileyrating.SmileyRating;
import com.ndroid.nadim.sahel.CoolToast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

public class QuestionActivity extends AppCompatActivity implements ReportDialogFragment.SingleChoiceListener {


    public static final String FINISHED_UPLOAD_ANSWER_IMG = "finished upload answers' image";
    public static final String FAILED_TO_UPLOAD_ANSWER_IMG = "failed to upload answers' image";
    public static final String NO_ANSWER_MSG = "No answer yet";
    public static final String ONE_ANS_MSG = "There is only one answer.";
    public static final String SHOW_SOLUTION_MSG = "Scroll down";

    public static final String QUESTION_ID = "question_id";
    public static final String TITLE = "title";
    public static final String COURSE_ID = "courseId";
    public static final String UPDATED_URL = "UPDATED URL";
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1;
    public static final String NULL_PATH = "null_path";
    private String questionRate = "No rate yet";

    private static boolean hasClickedSolution = false;
    private boolean hiddenSolution = true;
    private boolean hiddenRate = true;
    private boolean hasAnswer = false;
    private int currentAnswer = 0;
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

    /**
     * The broadcast receiver of the activity
     **/
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

        fireStoreHandler.setCurrentImagePath(question.getImagePath()); //todo needed?

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();

        Glide.with(this).load(Uri.parse(question.getLink())).placeholder(circularProgressDrawable).into(questionImageView);
        findViewById(R.id.loadingPanelQuestion).setVisibility(View.GONE);

        if (question.getAnswers().size() > 0) {
            Answer answer = question.getAnswers().get(0);
            solutionImage.setImageURI(Uri.parse(answer.getImagePath()));
            TextView answerRateText = findViewById(R.id.solutionRateText);
            answerRateText.setText(Integer.toString((int) answer.getRating()));
            // todo show all answers
        }
        else{
            //there are no answers so nothing needs to be loaded so  hide the progress bar :
            findViewById(R.id.loadingPanelAnswer).setVisibility(View.GONE);
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
        rateText.setText("Question Rating: " + questionRate);
        Button rateButton = findViewById(R.id.rateButton);

        setStudentAnimation();
        setRateButtonListener(rateButton);

        setAnswerButtonListener(addAnswerButton);
        questionImageView = findViewById(R.id.questionImage);
        setAnswerAndShow(solutionButton);
        Button shareButton = findViewById(R.id.shareButton);
        setShareButtonListener(shareButton);
    }

    private void setStudentAnimation() {
        LottieAnimationView studentAnimation = findViewById(R.id.studentAnimation);
        studentAnimation.setProgress(0);
        studentAnimation.playAnimation();
    }

    private void setRateButtonListener(final Button rateButton) {
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenRate) {
                    smileyRating.setVisibility(View.VISIBLE);
                    rateButton.setBackground(getResources().getDrawable(R.drawable.smile_color));
                    hiddenRate = false;
                } else {
                    smileyRating.setVisibility(View.INVISIBLE);
                    rateButton.setBackground(getResources().getDrawable(R.drawable.smile));
                    hiddenRate = true;
                }
            }
        });
    }

    private void setAnswerButtonListener(Button addAnswerButton) {
        addAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddAnswerActivity.class);
                intent.putExtra(QUESTION_ID, question.getId());
                intent.putExtra(COURSE_ID, course.getId());
                startActivity(intent);
            }
        });
    }

    private void setShareButtonListener(Button shareButton) {
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissions = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissions != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQUEST);
                }
                startSharingImageViaWhatsapp();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == WRITE_EXTERNAL_STORAGE_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startSharingImageViaWhatsapp();
            }
            else{
                showPermissionsRationalDialog();
            }
        }
    }

    private void showPermissionsRationalDialog() {
        Context context = getApplicationContext();
        CharSequence text = "If you don't grant access you won't be able to share via WhatsApp!";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void startSharingImageViaWhatsapp() {
        Picasso.get().load(question.getLink()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                sendPhotoViaWhatsapp(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.v("Failed to load bitmap", e.toString());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    private void sendPhotoViaWhatsapp(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                bitmap,
                "Title",
                null);

        if(path != null) {
            Uri imageUri = Uri.parse(path);

            setWhatsappIntent(imageUri);
        }
        else{
            Log.e(NULL_PATH, "image path is null!");
        }

    }

    private void setWhatsappIntent(Uri imageUri) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        whatsappIntent.setType("image/jpeg");
        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            CoolToast coolToast = new CoolToast(QuestionActivity.this);
            coolToast.make("Whatsapp not installed", CoolToast.DANGER);
        }
    }

    private void setAnswerAndShow(final Button solutionButton) {
        if (question.getAnswers().size() > 0) {
            updateSolution();
            hasAnswer = true;
        }
        showSolutionHandler(solutionButton);
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
                rateText.setText("Question Rating: " + question.getRating());
                fireStoreHandler.updateQuestion(question);
            }
        });
    }

    private void updateSolution() {
        final TextView answerRateText = findViewById(R.id.solutionRateText);
        final TextView solutionTitle = findViewById(R.id.solutionTitle);

        solutionImage = findViewById(R.id.solutionImage);


        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();


        Answer answer = question.getAnswers().get(currentAnswer);

        Glide.with(this).load(Uri.parse(answer.getImagePath())).placeholder(circularProgressDrawable).into(solutionImage);
        findViewById(R.id.loadingPanelAnswer).setVisibility(View.GONE);

        String title = answer.getTitle();
        if (title == null || title.equals(""))
        {
            title = "No title";
        }
        solutionTitle.setText(title);
        answerRateText.setText(Integer.toString((int) answer.getRating()));
        updateNumberOfAnswers();
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
        String questionId = intent.getStringExtra(QUESTION_ID);
        question = fireStoreHandler.getQuestionById(questionId);
        String courseId = intent.getStringExtra(COURSE_ID);
        course = fireStoreHandler.getCourseById(courseId);
    }

    private void showSolutionHandler(final Button solutionButton) {
        final ImageView solutionImage = findViewById(R.id.solutionImage);
        final ImageButton nextAnswerButton = findViewById(R.id.nextAnswerButton);
        final ImageButton previousAnswerButton = findViewById(R.id.previousAnswerButton);
        final Button answerLikeButton = findViewById(R.id.solutionLikeButton);
        final LinearLayout answerBox = findViewById(R.id.solutionRate);
        final TextView answerRateText = findViewById(R.id.solutionRateText);
        final TextView numberOfAnswers = findViewById(R.id.numberOfAnswer);
        final TextView showSolutionText = findViewById(R.id.showSolutionText);
        final TextView solutionTitle = findViewById(R.id.solutionTitle);


        answerLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasAnswer) {
                    if (!answerRated) {
                        Answer answer = question.getAnswers().get(currentAnswer);
                        answer.setRating(answer.getRating() + 1);
                        answerRateText.setText(Integer.toString((int) answer.getRating()));
                        answerLikeButton.setBackground(getResources().getDrawable(R.drawable.like2));
                        fireStoreHandler.updateQuestion(question);
                        answerRated = true;
                    } else {
                        Answer answer = question.getAnswers().get(currentAnswer);
                        answer.setRating(answer.getRating() - 1);
                        answerRateText.setText(Integer.toString((int) answer.getRating()));
                        answerLikeButton.setBackground(getResources().getDrawable(R.drawable.like1));
                        fireStoreHandler.updateQuestion(question);
                        answerRated = false;
                    }
                }
            }
        });
        solutionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenSolution) {
                    if (hasAnswer) {
                        if (!hasClickedSolution){
                            hasClickedSolution = true;
                            CoolToast coolToast = new CoolToast(QuestionActivity.this);
                            coolToast.make(SHOW_SOLUTION_MSG, CoolToast.INFO, CoolToast.CENTER);
                        }
                        solutionImage.setVisibility(View.VISIBLE);
                        numberOfAnswers.setVisibility(View.VISIBLE);
                        updateNumberOfAnswers();
                        nextAnswerButton.setVisibility(View.VISIBLE);
                        previousAnswerButton.setVisibility(View.VISIBLE);
                        answerBox.setVisibility(View.VISIBLE);
                        solutionTitle.setVisibility(View.VISIBLE);
//                        solutionButton.setText("Hide solution");
                        showSolutionText.setText("Hide solution");
                        solutionButton.setBackground(getResources().getDrawable(R.drawable.eye_color));
                        hiddenSolution = false;
                    } else {
                        CoolToast coolToast = new CoolToast(QuestionActivity.this);
                        coolToast.make(NO_ANSWER_MSG, CoolToast.DANGER);
                    }
                } else {
                    if (hasAnswer) {
                        solutionImage.setVisibility(View.INVISIBLE);
                        numberOfAnswers.setVisibility(View.INVISIBLE);
                        nextAnswerButton.setVisibility(View.INVISIBLE);
                        previousAnswerButton.setVisibility(View.INVISIBLE);
                        answerBox.setVisibility(View.INVISIBLE);
                        solutionTitle.setVisibility(View.INVISIBLE);
                    }
//                    solutionButton.setText("Show solution");
                    showSolutionText.setText("Show solution");
                    solutionButton.setBackground(getResources().getDrawable(R.drawable.eye));
                    hiddenSolution = true;
                }
            }
        });

        nextAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset_answer(1);
            }
        });

        previousAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset_answer(-1);
            }
        });

    }

    private void updateNumberOfAnswers() {
        final TextView numberOfAnswers = findViewById(R.id.numberOfAnswer);
        numberOfAnswers.setText(Integer.toString(currentAnswer + 1) + " / " +
                Integer.toString(question.getAnswers().size()));
    }

    private void offset_answer(int offset) {
        final TextView answerRateText = findViewById(R.id.solutionRateText);
        final TextView solutionTitle = findViewById(R.id.solutionTitle);

        if (question.getAnswers().size() == 1) {
            CoolToast coolToast = new CoolToast(QuestionActivity.this);
            coolToast.make(ONE_ANS_MSG, CoolToast.INFO);
        } else {
            currentAnswer = (currentAnswer + offset + question.getAnswers().size()) % question.getAnswers().size();
            updateSolution();
        }
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

    /**
     * implementation for report button click
     * @param view
     */
    public void reportButtonOnclick(View view){
        DialogFragment reportDialog = new ReportDialogFragment();
        reportDialog.setCancelable(false);
        reportDialog.show(getSupportFragmentManager(), "Single Choice Dialog");

    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        String[] recipients = new String[]{"iporat08@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");

        String title = "report image for StudyGroupApp";
        String body = "The reason for reporting: " + list[position];

        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        Intent chooser = Intent.createChooser(emailIntent, "Send report by mail...");
        try{
            startActivity(chooser);
            finish();
        }catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}


