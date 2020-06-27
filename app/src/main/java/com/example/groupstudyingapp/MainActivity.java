package com.example.groupstudyingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends BaseMenuActivity implements CoursesAdapter.ItemClickListener {

    private Button saveButton;
    AppData appData;
    FireStoreHandler fireStoreHandler;
    private ArrayList<Course> coursesList;
    private CoursesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        getAppData();
        setRecyclerViews();

        final FloatingActionButton addCourseButton = findViewById(R.id.addCourseButton);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SendEmailActivity.class);
                startActivity(intent);
            }
        });
    }

    // TODO: lior , delete before submission
    private void addCoursesTemp() {
        //change the names before uploading more courses
        String[] names = {"Image Processing", "Communication Networks", "Algorithms", "OOP", "IML",
                "Logic", "Nand To Tetris", "Probability and Statistics",
                "Cryptography And Software Security", "Infi 1", "Infi 2", "Programming Workshop in C", "Programming Workshop in CPP"};

        for (String name : names) {
            addCourseTemp(name);

        }
    }

    // TODO: lior , delete before submission
    private void addCourseTemp(String name) {
        Course course = new Course();
        course.setName(name);
        fireStoreHandler.addCourse(course);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        setProfile();

        MenuItem searchItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) searchItem.getActionView();

        setSearchQueryTextListener(searchView);
        return true;
    }

    private void setSearchQueryTextListener(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setRecyclerViews() {
        coursesList = new ArrayList<>();
        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        adapter = new CoursesAdapter(appData.fireStoreHandler, coursesList);
        adapter.setClickListener(MainActivity.this);
        rvCourses.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvCourses.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rvCourses.getContext(),
                layoutManager.getOrientation());
        rvCourses.addItemDecoration(mDividerItemDecoration);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), CoursePageActivity.class);
        fireStoreHandler.setCurrentCourseId(coursesList.get(position).getId());
        startActivity(intent);
    }

    private void getAppData() {
        appData = (AppData) getApplicationContext();
        fireStoreHandler = appData.fireStoreHandler;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
