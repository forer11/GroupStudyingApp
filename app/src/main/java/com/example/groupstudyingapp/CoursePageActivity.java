package com.example.groupstudyingapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.ActionBarOverlayLayout;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

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
        questions = course.getQuestions();
        questions.sort(new Question.questionCompareHighestRatingFirst());
        toggleAddQuestionsNote();
        setRecyclerView();
        setAddQuestionButtonListener();
        setBroadcastReceiver();
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(course.getName());

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
    private void addNewQuestion(String title, String questionLink) {
        Question newQuestion = new Question(title, questionLink);
        newQuestion.setLink(questionLink);
        newQuestion.setId(getNewCourseId());
        questions.add(newQuestion);
        toggleAddQuestionsNote();
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
        // if you use this function - need to add toggleAddQuestionsNote()
        adapter.notifyDataSetChanged();
    }


    private void removeSingleItem(int removeIndex) {
        questions.remove(removeIndex);
        // if you use this function - need to add toggleAddQuestionsNote()
        adapter.notifyItemRemoved(removeIndex);
    }

    private void removeAllItems() {
        questions.clear();
        // if you use this function - need to add toggleAddQuestionsNote()
        adapter.notifyDataSetChanged();
    }

    public void toggleAddQuestionsNote() {
        int totalQuestions = questions.size();
        ImageView noQuestionsIcon = findViewById(R.id.no_questions_icon);
        TextView noQuestionsText = findViewById(R.id.no_questions_text);
        TextView addQuestionText = findViewById(R.id.add_question_text);

        if (totalQuestions == 0) {
            noQuestionsIcon.setVisibility(View.VISIBLE);
            noQuestionsText.setVisibility(View.VISIBLE);
            addQuestionText.setVisibility(View.VISIBLE);
        } else {
            noQuestionsIcon.setVisibility(View.GONE);
            noQuestionsText.setVisibility(View.GONE);
            addQuestionText.setVisibility(View.GONE);
        }
    }

    void toggleEmptySearchResults() {
        TextView emptyResultsText = findViewById(R.id.empty_search_results_text);
        if (adapter.getItemCount() == 0) {
            emptyResultsText.setVisibility(View.VISIBLE);
        } else {
            emptyResultsText.setVisibility(View.GONE);
        }
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

    /**
     * show the sign out dialog on screen
     */
    public void showSortDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder
                (CoursePageActivity.this);
        View view = getLayoutInflater().inflate(R.layout.questions_sort_options_dialog, null);
        dialogBuilder.setView(view);
//        setDialogView(dialogBuilder, view);
        final AlertDialog alertdialog = dialogBuilder.create();
        onClickDialog(view, alertdialog);
        Objects.requireNonNull(alertdialog.getWindow()).setBackgroundDrawable
                (new ColorDrawable(Color.TRANSPARENT));
        alertdialog.show();
    }

    private void onClickDialog(View view, final AlertDialog alertDialog) {
        Button sortByHighRating = view.findViewById(R.id.button_sort_by_high_rating);
        Button sortByLowRating = view.findViewById(R.id.button_sort_by_low_rating);
        Button sortByDate = view.findViewById(R.id.button_sort_by_date);
        Button sortByTitle = view.findViewById(R.id.button_sort_by_title);

        sortByHighRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.sort(new Question.questionCompareHighestRatingFirst());
                setRecyclerView();
                alertDialog.cancel();
            }
        });

        sortByLowRating.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                questions.sort(new Question.questionCompareLowestRatingFirst());
                setRecyclerView();
                alertDialog.cancel();
            }
        });

        sortByDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                questions.sort(new Question.questionCompareDateCreated());
                setRecyclerView();
                alertDialog.cancel();
            }
        });

        sortByTitle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                questions.sort(new Question.questionCompareTitle());
                setRecyclerView();
                alertDialog.cancel();
            }
        });


    }
}

