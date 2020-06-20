package com.example.groupstudyingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CoursePageActivity extends BaseMenuActivity implements CoursePageAdapter.ItemClickListener {
    public static final String IMAGE_UPLOADED = "image_uploaded";
    public static final String ANSWER_IMAGE_UPLOADED = "answer_image_uploaded";
    public static final String TITLE = "title";

    CoursePageAdapter adapter;
    private ArrayList<Question> questions;
    AppData appData;
    FireStoreHandler fireStoreHandler;
    private Course course;


    /**
     * The broadcast receiver of the activity
     **/
    private BroadcastReceiver br;

    /**
     * The local path of the last image taken by the camera
     **/
    private String currentPhotoPath; //todo - needed?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);
        getAppData();
        // data to populate the RecyclerView with
        Question.questionComparator qCompare = Question.getQuestionComparator();
        questions = course.getQuestions();
        questions.sort(qCompare);
        setRecyclerView();
        setAddQuestionButtonListener();
        setBroadcastReceiver();
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(course.getName());
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        setProfileImageWithUrl();
        return true;
    }

    private void setBroadcastReceiver() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(IMAGE_UPLOADED)) {
                    String questionUrl = intent.getStringExtra("UPDATED URL");
                    String title = intent.getStringExtra(TITLE);
                    addNewQuestion(title, questionUrl);
                    fireStoreHandler.updateCourse(course.getId());
                }
            }
        };
        IntentFilter filter = new IntentFilter(IMAGE_UPLOADED);
        this.registerReceiver(br, filter);
    }

    private void setAddQuestionButtonListener() {
        findViewById(R.id.addQuestionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddQuestionActivity.class);
                startActivity(intent);
            }
        });
    }

    private String getNewCourseId() {
        String id = "";
        boolean idIsUnique = false;
        while (!idIsUnique) {
            id = AppData.getRandomId();
            idIsUnique = true;
            for (Question question : questions) {
                if (question.getId() != null && question.getId().equals(id)) {
                    idIsUnique = false;
                    break;
                }
            }
        }
        return id;
    }

    //TODO - Ido - is imagePath needed here?
    private void addNewQuestion(String title, String questionLink) { //todo should'nt return Question but id
        Question newQuestion = new Question(title, questionLink);
        newQuestion.setLink(questionLink);
        newQuestion.setId(getNewCourseId());
        questions.add(newQuestion);
        adapter.notifyDataSetChanged();
    }

    private void setRecyclerView() {
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CoursePageAdapter(this, questions);
        adapter.setClickListener(CoursePageActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(),
                DividerItemDecoration.VERTICAL));
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
        intent.putExtra(QuestionActivity.QUESTION_ID, questions.get(position).getId());
        intent.putExtra(QuestionActivity.COURSE_ID, course.getId());
        startActivity(intent);
    }

    //TODO Mor/Ido do we need those?
    private void insertSingleItem(Question newQuestion) {
        questions.add(newQuestion);
        adapter.notifyDataSetChanged();
    }


    private void removeSingleItem(int removeIndex) {
        questions.remove(removeIndex);
        adapter.notifyItemRemoved(removeIndex);
    }

    private void removeAllItems() {
        questions.clear();
        adapter.notifyDataSetChanged();
    }

    //TODO - can we delete it from here?
    private void getAppData() {
        //TODO - the data won't be loaded again like this, were gonna send only the relevant course from main activity
        appData = (AppData) getApplicationContext();
        fireStoreHandler = appData.fireStoreHandler;
        course = fireStoreHandler.getCurrentCourse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}

