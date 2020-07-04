package com.example.groupstudyingapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

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
        View gotoSortButton = getLayoutInflater().inflate(R.layout.sort_button, null);
        toolbar.addView(gotoSortButton);
        gotoSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortDialog();
            }
        });
    }

    public void showSortDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder
                (MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.courses_sort_options_dialog, null);
        dialogBuilder.setView(view);
        final AlertDialog alertdialog = dialogBuilder.create();
        onClickDialog(view, alertdialog);
        Objects.requireNonNull(alertdialog.getWindow()).setBackgroundDrawable
                (new ColorDrawable(Color.TRANSPARENT));
        alertdialog.show();
    }

    private void onClickDialog(View view, final AlertDialog alertDialog) {
        Button sortByName = view.findViewById(R.id.button_sort_by_name);
        Button sortByMost = view.findViewById(R.id.button_sort_by_most);
        Button sortByLeast = view.findViewById(R.id.button_sort_by_least);

        sortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coursesList.sort(new Comparator<Course>() {
                    @Override
                    public int compare(Course o1, Course o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                adapter.notifyDataSetChanged();
                alertDialog.cancel();
            }
        });

        sortByMost.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                coursesList.sort(new Comparator<Course>() {
                    @Override
                    public int compare(Course o1, Course o2) {
                        return o2.getQuestions().size() - o1.getQuestions().size();
                    }
                });
                adapter.notifyDataSetChanged();
                alertDialog.cancel();
            }
        });

        sortByLeast.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                coursesList.sort(new Comparator<Course>() {
                    @Override
                    public int compare(Course o1, Course o2) {
                        return o1.getQuestions().size() - o2.getQuestions().size();
                    }
                });
                adapter.notifyDataSetChanged();
                alertDialog.cancel();
            }
        });
    }

    private void setRecyclerViews() {
        coursesList = new ArrayList<>();
        RecyclerView rvCourses = findViewById(R.id.rvCourses);
        adapter = new CoursesAdapter(appData.fireStoreHandler, coursesList, this);
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

    void toggleEmptySearchResults() {
        TextView emptyResultsText = findViewById(R.id.empty_search_results_text);
        if (adapter.getItemCount() == 0) {
            emptyResultsText.setVisibility(View.VISIBLE);
        } else {
            emptyResultsText.setVisibility(View.GONE);
        }
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
