package com.example.groupstudyingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends BaseMenuActivity implements CoursesAdapter.ItemClickListener{

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
        setRecyclerViews();
    }



    interface OnItemCreated {
        void itemCreated(String title);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setRecyclerViews() {
        RecyclerView rvCourses = (RecyclerView) findViewById(R.id.rvCourses);
        CoursesAdapter adapter = new CoursesAdapter(appData.fireStoreHandler);
        adapter.setClickListener(MainActivity.this);
        rvCourses.setAdapter(adapter);
        rvCourses.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setButtonsClickListeners() {  // todo delete this function
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), CoursePageActivity.class);
        fireStoreHandler.setCurrentCourseId(position);
        startActivity(intent);
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
