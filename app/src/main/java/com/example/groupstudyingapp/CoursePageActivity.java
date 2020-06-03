package com.example.groupstudyingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flatdialoglibrary.dialog.FlatDialog;

import java.util.ArrayList;
import java.util.List;

public class CoursePageActivity extends AppCompatActivity implements CoursePageAdapter.ItemClickListener {
    public static final int CAMERA_ACTION = 0;
    public static final int GALLERY_ACTION = 1;

    CoursePageAdapter adapter;
    private ArrayList<Question> questions;
    AppData appData;
    FireStoreHandler fireStoreHandler;
    private Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);
        getAppData();
        // data to populate the RecyclerView with
        questions = course.getQuestions();
        setRecyclerView();
        findViewById(R.id.addQuestionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInsertDialog();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent
            imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case CAMERA_ACTION:
                if (resultCode == RESULT_OK) {
                    Uri imagePath = imageReturnedIntent.getData();
                    Question newQuestion = new Question("",imagePath.toString());
                    //TODO - update the image in firestore
                    Toast.makeText(CoursePageActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
//                    questionImage.setImageURI(selectedImage);
                }

                break;
            case GALLERY_ACTION:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(CoursePageActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                    Uri selectedImage = imageReturnedIntent.getData();
                    //TODO - update the image in firestore
//                    questionImage.setImageURI(selectedImage);
                }
                break;
        }
    }

    private void showInsertDialog() {
        FlatDialog flatDialog = new FlatDialog(CoursePageActivity.this);
        flatDialog.setTitle("Add a question")
                .setSubtitle("Write your question title here")
                .setFirstTextFieldHint("Question title")
                .setFirstButtonText("Camera")
                .setSecondButtonText("From Gallery")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);
                    }
                })
                .show();
        flatDialog.setCanceledOnTouchOutside(true);
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
       intent.putExtra("EXTRA_SESSION_ID", questions.get(position));
        startActivity(intent);
    }

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

    private void getAppData() {
        //TODO - the data won't be loaded again like this, were gonna send only the relevant course from main activity
        appData = (AppData) getApplicationContext();
        fireStoreHandler = appData.fireStoreHandler;
        List<Course> courses = fireStoreHandler.getCourses();
        course = courses.get(0);
    }
}

