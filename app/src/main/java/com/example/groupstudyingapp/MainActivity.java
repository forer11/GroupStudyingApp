package com.example.groupstudyingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends BaseMenuActivity {

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

        // Lookup the recyclerview in activity layout
        RecyclerView rvCourses = (RecyclerView) findViewById(R.id.rvCourses);

        // Create adapter passing in the sample user data
        CoursesAdapter adapter = new CoursesAdapter(appData.fireStoreHandler.getCourses());
        // Attach the adapter to the recyclerview to populate items
        rvCourses.setAdapter(adapter);
        // Set layout manager to position the items
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
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
                Intent intent = new Intent(getApplicationContext(), CoursePageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setViews() {
        saveButton = findViewById(R.id.save_button);
        gotoButton = findViewById(R.id.goto_button);
    }

    private void getAppData() {
        appData = (AppData) getApplicationContext();
        fireStoreHandler = appData.fireStoreHandler;
    }

}
