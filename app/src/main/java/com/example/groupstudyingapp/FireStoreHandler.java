package com.example.groupstudyingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class FireStoreHandler {
    public static final String UNSUCCESSFUL_IMAGE_UPLOAD = "unsuccessful_image_upload";
    private FirebaseFirestore db;
    private CollectionReference coursesRef;
    private ArrayList<String> coursesIds;
    private HashMap<String, Course> courses;
    private Context context;
    private String currentCourseId;
    private String currentImagePath;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    private static String COURSES = "courses";
    public static final String IMAGE_UPLOADED = "image_uploaded";

    public FireStoreHandler(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        coursesRef = db.collection(COURSES);
        coursesIds = new ArrayList<>();
        courses = new HashMap<>();
        loadData();

    }

    public void loadData() {

        coursesRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                coursesIds.add(document.getId());
                                loadCourse(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void updateData() {
        for (Course c : courses.values()) {
            updateCourse(c.getId());
        }
    }

    public void updateCourse(String id) { //TODO - should receive id and not Course object
        Course c = courses.get(id);
        if( c == null){
            Log.e("update_fail", "could'nt update the requested course, it does'nt exist");
        }
        else {
            coursesRef.document(id).set(c, SetOptions.merge())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "failed to get data", Toast
                                    .LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void addCourse(Course c) { // todo needed?
        final Course course = c;
        coursesRef.add(course)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();
                        course.setId(id);
                        coursesRef.document(id).set(course, SetOptions.merge());
                        coursesIds.add(id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    public void updateQuestion(Question question) {
        ArrayList<Question> questions = Objects.requireNonNull(courses.get(currentCourseId)).getQuestions();
        for (int i = 0; i < questions.size(); ++i) {
            if (questions.get(i).getImagePath().compareTo(currentImagePath) == 0) {
                questions.set(i, question);
            }
        }
        updateCourse(Objects.requireNonNull(currentCourseId));
    }


    /**
     * Uploads the image in localImagePath to fireStore, updates the link of newQuestion to the URI
     * of the uploaded image //todo - should set path and not link of newQuestion
     *
     * @param localImagePath  - a URI of local file path of the image to be uploaded
     * @param storedImagePath -the path in the fireStore storage to which the image will be uploaded
     * @param newQuestion     - the new question to which the new image belongs
     */
    public void uploadQuestionImage(final Uri localImagePath, String storedImagePath,
                                    final Question newQuestion, final Context ctx) {

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        final StorageReference questionsRef = storageRef.child(storedImagePath + ".jpg");
        UploadTask uploadTask = questionsRef.putFile(localImagePath);
        final Intent intent = new Intent();
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(ctx, "Failed Uploading", Toast.LENGTH_SHORT).show();
                Log.i(UNSUCCESSFUL_IMAGE_UPLOAD, "unsuccessful image upload");
                intent.setAction(AddQuestionActivity.FAILED_TO_UPLOAD);
                context.sendBroadcast(intent);
                //TODO - add a broadcast of failure
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(ctx, "Uploaded", Toast.LENGTH_SHORT).show();
                updateNewQuestionUri(newQuestion, questionsRef, taskSnapshot);
                intent.setAction(AddQuestionActivity.FINISHED_UPLOAD);
                context.sendBroadcast(intent);
                //TODO - add a broadcast of success
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }

    /**
     * Gets the URI of the recently uploaded image of the newQuestion
     *
     * @param newQuestion  the newQuestion //todo - needs to be id and not object?
     * @param questionsRef a reference to the questions images storage on firebase
     * @param uploadTask   the upload task that uploads the image
     */
    private void updateNewQuestionUri(final Question newQuestion,
                                      final StorageReference questionsRef, final UploadTask.TaskSnapshot uploadTask) {
        questionsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String photoUrl = uri.toString();
                Intent intent = new Intent();
                intent.setAction(IMAGE_UPLOADED);
                intent.putExtra("UPDATED URL", photoUrl);
                intent.putExtra("UPDATED QUESTION", newQuestion);
                context.sendBroadcast(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.i("Nooooo", exception.toString());
            }
        });


//        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,
//                Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                // Continue with the task to get the download URL
//                return questionsRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    newQuestion.setLink(task.getResult());
//                    updateData(); // todo - empty method
//                    Log.i("URI LIOR YAY", newQuestion.getLink().toString()); //todo delete
//                } else {
//                    // todo - handle failure
//                }
//            }
//        });
    }


    public void buildDB() { // call once
        Course course1 = new Course(); // todo - temp
        course1.setName("Databases");

        Question q1 = new Question();
        q1.setId("q1");
        q1.setTitle("question 1");
        q1.setLink("gs://groupstudyingapp.appspot.com/questions/q1.PNG");
        q1.setRating(5);
        q1.setImagePath("gs://groupstudyingapp.appspot.com/questions/q1.PNG");

        Answer a1 = new Answer();
        a1.setId("a1");
        a1.setRating(5);
        a1.setLink("gs://groupstudyingapp.appspot.com/answers/a1.PNG");
        q1.setTitle("question 1");
        a1.setImagePath("gs://groupstudyingapp.appspot.com/answers/a1.PNG");
        q1.addAnswer(a1);

        course1.addQuestion(q1);

        Question q2 = new Question();
        q2.setId("q2");
        q2.setTitle("question 2");
        q2.setLink("gs://groupstudyingapp.appspot.com/questions/q2.PNG");
        q2.setRating(3);
        q2.setImagePath("gs://groupstudyingapp.appspot.com/questions/q2.PNG");

        Answer a2 = new Answer();
        a2.setId("a2");
        a2.setRating(4);
        a2.setLink("gs://groupstudyingapp.appspot.com/answers/a2.PNG");
        a2.setImagePath("gs://groupstudyingapp.appspot.com/answers/a2.PNG");
        q2.addAnswer(a2);

        course1.addQuestion(q2);

        addCourse(course1);

        Course course2 = new Course(); // todo - temp
        course2.setName("Image Processing");

        Question q3 = new Question();
        q3.setId("q3");
        q3.setTitle("question 3");
        q3.setLink("gs://groupstudyingapp.appspot.com/questions/q3.PNG");
        q3.setRating(2);
        q3.setImagePath("gs://groupstudyingapp.appspot.com/questions/q3.PNG");

        Answer a3 = new Answer();
        a3.setId("a3");
        a3.setRating(4);
        a3.setLink("gs://groupstudyingapp.appspot.com/answers/a3.PNG");
        a3.setImagePath("gs://groupstudyingapp.appspot.com/answers/a3.PNG");
        q3.addAnswer(a3);

        course2.addQuestion(q3);

        Question q4 = new Question();
        q4.setId("q4");
        q4.setTitle("question 4");
        q4.setLink("gs://groupstudyingapp.appspot.com/questions/q5.PNG");
        q4.setRating(5);
        q4.setImagePath("gs://groupstudyingapp.appspot.com/questions/q5.PNG");

        Answer a4 = new Answer();
        a4.setId("a4");
        a4.setRating(4);
        a4.setLink("gs://groupstudyingapp.appspot.com/answers/a4.PNG");
        a4.setImagePath("gs://groupstudyingapp.appspot.com/answers/a4.PNG");
        q4.addAnswer(a4);

        course2.addQuestion(q4);

        addCourse(course2);

    }

    private void loadCourse(String courseId) {
        final String cid = courseId;
        DocumentReference docRef = coursesRef.document(courseId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                courses.put(cid, documentSnapshot.toObject(Course.class));
            }
        });
    }


    public void setCurrentCourseId(int position) {
        currentCourseId = coursesIds.get(position);
    }

    public void setCurrentImagePath(String path) { currentImagePath = path; }


    public Course getCurrentCourse() {
        return courses.get(currentCourseId);
    }

    public ArrayList<String> getCoursesIds() {
        return coursesIds;
    }

    public Course getCourseById(String id) {
        return courses.get(id);
    }
}
