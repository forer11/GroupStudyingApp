package com.example.groupstudyingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.example.flatdialoglibrary.dialog.FlatDialog;

import java.util.Objects;

public class PrevCoursePageActivity extends AppCompatActivity {
    public static final int CAMERA_ACTION = 0;
    public static final int GALLERY_ACTION = 1;
    private ExpandingList expandingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prev_activity_course_page);
        expandingList = findViewById(R.id.expanding_list_main);
        createItems();
        setToolbar();
        findViewById(R.id.addQuestionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInsertDialog(new MainActivity.OnItemCreated() {
                    @Override
                    public void itemCreated(String title) {
//                            View newSubItem = item.createSubItem();
//                            configureSubItem(item, newSubItem, title);
                    }
                });
            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //TODO - update to course name from firestore
        getSupportActionBar().setTitle("Open source Workshop");
    }

    private void createItems() {
        //Todo - take from firestore
        addItem("2019 MOED A", new String[]{}, R.color.pink, R.drawable.exam);
        addItem("2019 MOED B", new String[]{}, R.color.blue, R.drawable.exam);
        addItem("2018 MOED A", new String[]{}, R.color.purple, R.drawable.exam);
        addItem("2018 MOED B", new String[]{}, R.color.yellow, R.drawable.exam);
        addItem("2017 MOED A", new String[]{}, R.color.orange, R.drawable.exam);
        addItem("2017 MOED B", new String[]{}, R.color.green, R.drawable.exam);
        addItem("2016 MOED A", new String[]{}, R.color.blue, R.drawable.exam);
        addItem("2016 MOED B", new String[]{}, R.color.yellow, R.drawable.exam);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case CAMERA_ACTION:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    //TODO - update the image in firestore
                    Toast.makeText(PrevCoursePageActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
//                    questionImage.setImageURI(selectedImage);
                }

                break;
            case GALLERY_ACTION:
                if(resultCode == RESULT_OK){
                    Toast.makeText(PrevCoursePageActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
                    Uri selectedImage = imageReturnedIntent.getData();
                    //TODO - update the image in firestore
//                    questionImage.setImageURI(selectedImage);
                }
                break;
        }
    }

    private void showInsertDialog(final MainActivity.OnItemCreated positive) {
        FlatDialog flatDialog = new FlatDialog(PrevCoursePageActivity.this);
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
                        startActivityForResult(pickPhoto , 1);
                    }
                })
                .show();
        flatDialog.setCanceledOnTouchOutside(true);
    }

    private void addItem(final String title, String[] subItems, int colorRes, int iconRes) {
        //Let's create an item with R.layout.expanding_layout
        ExpandingItem item = expandingList.createNewItem(R.layout.expanding_layout);
        //If item creation is successful, let's configure it
        if (item != null) {
            item.setIndicatorColorRes(colorRes);
            item.setIndicatorIconRes(iconRes);
            //It is possible to get any view inside the inflated layout. Let's set the text in the item
            ((TextView) item.findViewById(R.id.title)).setText(title);
//            //We can create items in batch.
//            item.createSubItems(subItems.length);
//            for (int i = 0; i < item.getSubItemsCount(); i++) {
//                //Let's get the created sub item by its index
//                final View view = item.getSubItemView(i);
//
//                //Let's set some values in
//                configureSubItem(item, view, subItems[i]);
//            }

            item.findViewById(R.id.remove_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    expandingList.removeItem(item);
                }
            });
        }
    }

//
//    private void configureSubItem(final ExpandingItem item, final View view, final String subTitle) {
//        ((TextView) view.findViewById(R.id.sub_title)).setText(subTitle);
//        view.findViewById(R.id.remove_sub_item).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                item.removeSubItem(view);
//            }
//        });
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getBaseContext(), QuestionActivity.class);
//                intent.putExtra("EXTRA_SESSION_ID", subTitle);
//                startActivity(intent);
//
//            }
//        });
//    }

}
