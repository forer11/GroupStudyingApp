package com.example.groupstudyingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flatdialoglibrary.dialog.FlatDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CoursePageActivity extends AppCompatActivity implements CoursePageAdapter.ItemClickListener {
    public static final int CAMERA_ACTION = 0;
    public static final int GALLERY_ACTION = 1;
    public static final String IMAGE_UPLOADED = "image_uploaded";
    public static final String IMAGE_FILE_CREATION_FAILURE = "image_file_creation_failure";
    public static final String PACKEGE_NAME = "com.example.android.fileprovider";
    public static final String PLS_UPLOAD_IMG = "Please upload an image";
    boolean isPhotoEntered = false;
    CoursePageAdapter adapter;
    private ArrayList<Question> questions;
    AppData appData;
    FireStoreHandler fireStoreHandler;
    private Course course;
    private String newQuestionImagePath;
    private Uri newImageUri;

    /**
     * The broadcast receiver of the activity
     **/
    private BroadcastReceiver br;

    /**
     * The local path of the last image taken by the camera
     **/
    private String currentPhotoPath;

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

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(IMAGE_UPLOADED)) {
                    Toast.makeText(CoursePageActivity.this, "Image uploaded!",
                            Toast.LENGTH_SHORT).show();
                    String questionUrl = intent.getStringExtra("UPDATED URL");
                    Question question = (Question)intent.getSerializableExtra("UPDATED QUESTION");
                    addNewQuestion(Objects.requireNonNull(question),questionUrl);
                    fireStoreHandler.updateData();
                }
            }
        };

        IntentFilter filter = new IntentFilter(IMAGE_UPLOADED);
        this.registerReceiver(br, filter);

    }

    //////////////////////////// onActivityResult related methods //////////////////////////////////
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_ACTION:
                    handleCameraImageCase();
                    break;

                case GALLERY_ACTION:
                    handleGalleryImageCase(imageReturnedIntent);
                    break;
            }
        } else {
            Log.i("image_save_error", "image was'nt saved");
        }
    }

    /**
     * @param imageReturnedIntent handle the GALLERY_ACTION case in onActivityResult
     */
    private void handleGalleryImageCase(Intent imageReturnedIntent) {
        isPhotoEntered = true;
        newImageUri = imageReturnedIntent.getData();
        if (newImageUri != null) {
            newQuestionImagePath = "questions/" + newImageUri.getLastPathSegment();
        }
    }

    /**
     * handles the CAMERA_ACTION case in onActivityResult
     */
    private void handleCameraImageCase() {
        File imgFile = new File(currentPhotoPath);
        if (imgFile.exists()) {
            isPhotoEntered = true;
            newImageUri = Uri.fromFile(imgFile);
            newQuestionImagePath = "questions/" + newImageUri.getLastPathSegment();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Once you decide the directory for the file, you need to create a collision-resistant file name.
     * You may wish also to save the path in a member variable for later use. Here's an example
     * solution in a method that returns a unique file name for a new photo using a date-time stamp.
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Sets everything that is needed for handling an image from the camera and calls the relevant
     * activity
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i(IMAGE_FILE_CREATION_FAILURE, "Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, PACKEGE_NAME, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_ACTION);
            }
        }
    }

    //TODO - Ido - is imagePath needed here?
    private void addNewQuestion(Question newQuestion,String questionLink) { //todo should'nt return Question but id
        newQuestion.setLink(questionLink);
        questions.add(newQuestion);
        adapter.notifyDataSetChanged();
    }

    private void showInsertDialog() {
        final FlatDialog flatDialog = new FlatDialog(CoursePageActivity.this);
        flatDialog.setFirstTextField("");
        flatDialog.setTitle("Add a question")
                .setSubtitle("Write your question title here")
                .setFirstTextFieldHint("Question title")
                .setFirstButtonText("Camera")
                .setSecondButtonText("From Gallery")
                .setThirdButtonText("Done")
                .withFirstButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent();
                    }
                })
                .withSecondButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, GALLERY_ACTION);
                    }
                })
                .withThirdButtonListner(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (flatDialog.getFirstTextField().equals("")) {
                            Toast.makeText(CoursePageActivity.this, "Please write title", Toast.LENGTH_SHORT).show();
                        } else {
                            String questionTitleInput = flatDialog.getFirstTextField();
                            if (!isPhotoEntered) {
                                Toast.makeText(CoursePageActivity.this, PLS_UPLOAD_IMG, Toast.LENGTH_SHORT).show();
                            } else {
                                Question newQuestion = new Question(questionTitleInput,
                                        newQuestionImagePath);
                                fireStoreHandler.uploadQuestionImage(newImageUri,
                                        newQuestionImagePath,
                                        newQuestion,
                                        CoursePageActivity.this);
                                flatDialog.dismiss();
                            }
                        }

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
        course = fireStoreHandler.getCurrentCourse();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}

