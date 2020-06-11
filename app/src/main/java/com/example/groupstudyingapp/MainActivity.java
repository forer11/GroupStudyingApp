package com.example.groupstudyingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends BaseMenuActivity implements CoursesAdapter.ItemClickListener {

    private Button saveButton;
    AppData appData;
    FireStoreHandler fireStoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        getAppData();

        setButtonsClickListeners();
        setRecyclerViews();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setRecyclerViews() {
        RecyclerView rvCourses = findViewById(R.id.rvCourses);
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
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), CoursePageActivity.class);
        fireStoreHandler.setCurrentCourseId(position);
        startActivity(intent);
    }

    private void getAppData() {
        appData = (AppData) getApplicationContext();
        fireStoreHandler = appData.fireStoreHandler;
    }

}
