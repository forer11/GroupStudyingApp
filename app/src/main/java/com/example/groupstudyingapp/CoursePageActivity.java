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

public class CoursePageActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    public static final int CAMERA_ACTION = 0;
    public static final int GALLERY_ACTION = 1;

    MyRecyclerViewAdapter adapter;
    private ArrayList<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);

        // data to populate the RecyclerView with
        questions = new ArrayList<>();
        questions.add("Horse");
        questions.add("Cow");
        questions.add("Camel");
        questions.add("Sheep");
        questions.add("Goat");
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
                    Uri selectedImage = imageReturnedIntent.getData();
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
        adapter = new MyRecyclerViewAdapter(this, questions);
        adapter.setClickListener(CoursePageActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(),
                DividerItemDecoration.VERTICAL));
    }


    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
        intent.putExtra("EXTRA_SESSION_ID", position);
        startActivity(intent);
    }

    private void insertSingleItem() {
        String item = "Pig";
        int insertIndex = 2;
        questions.add(insertIndex, item);
        adapter.notifyItemInserted(insertIndex);
    }

    private void insertMultipleItems() {
        ArrayList<String> items = new ArrayList<>();
        items.add("Pig");
        items.add("Chicken");
        items.add("Dog");
        int insertIndex = 2;
        questions.addAll(insertIndex, items);
        adapter.notifyItemRangeInserted(insertIndex, items.size());
    }

    private void removeSingleItem() {
        int removeIndex = 2;
        questions.remove(removeIndex);
        adapter.notifyItemRemoved(removeIndex);
    }

    private void removeMultipleItems() {
        int startIndex = 2; // inclusive
        int endIndex = 4;   // exclusive
        int count = endIndex - startIndex; // 2 items will be removed
        questions.subList(startIndex, endIndex).clear();
        adapter.notifyItemRangeRemoved(startIndex, count);
    }

    private void removeAllItems() {
        questions.clear();
        adapter.notifyDataSetChanged();
    }

    private void replaceOldListWithNewList() {
        // clear old list
        questions.clear();

        // add new list
        ArrayList<String> newList = new ArrayList<>();
        newList.add("Lion");
        newList.add("Wolf");
        newList.add("Bear");
        questions.addAll(newList);

        // notify adapter
        adapter.notifyDataSetChanged();
    }

    private void updateSingleItem() {
        String newValue = "I like sheep.";
        int updateIndex = 3;
        questions.set(updateIndex, newValue);
        adapter.notifyItemChanged(updateIndex);
    }

    private void moveSingleItem() {
        int fromPosition = 3;
        int toPosition = 1;

        // update data array
        String item = questions.get(fromPosition);
        questions.remove(fromPosition);
        questions.add(toPosition, item);

        // notify adapter
        adapter.notifyItemMoved(fromPosition, toPosition);
    }
}

