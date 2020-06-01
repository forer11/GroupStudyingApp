package com.example.groupstudyingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends BaseMenuActivity {

    private EditText editTextTile, editTextDescription;
    private Button saveButton, gotoButton;
    AppData appData;
    FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        getAppData();
        setViews();

        setButtonsClickListeners();
    }



    interface OnItemCreated {
        void itemCreated(String title);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setButtonsClickListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Course currentCourse =  new Course(); // todo - temp
                currentCourse.setName("Intro");

                Question question = new Question();
                question.setTitle("title");
                question.setLink("www.google.com");
                question.setRating(5);
                currentCourse.addQuestion(question);
                appData.fireStoreHandler.addCourse(currentCourse);

//                String title = editTextTile.getText().toString();
//                String description = editTextDescription.getText().toString();
//                Note note = new Note();
//                note.setTitle(title);
//                note.setDescription(description);
//
//                fireStoreHandler.saveNote(note, getBaseContext());
            }
        });

        gotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CoursePageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setViews() {
        editTextTile = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        saveButton = findViewById(R.id.save_button);
        gotoButton = findViewById(R.id.goto_button);
    }

    private void getAppData() {
        appData = (AppData) getApplicationContext();
        fireStoreHandler = appData.fireStoreHandler;
    }


}
