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
import com.google.firebase.auth.FirebaseUser;
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
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.ContentValues.TAG;

public class FireStoreHandler {

    public static final String IMAGE_UPLOADED = "image_uploaded";
    public static final String ANSWER_IMG_UPLOADED = "answer_image_uploaded";
    private static final String UNSUCCESSFUL_IMAGE_UPLOAD = "unsuccessful_image_upload";
    private static final String COURSE_UPDATE_FAILURE_MESSAGE = "could'nt update the requested course, it does'nt exist";
    public static final String TITLE = "title";
    private static final String USERS = "Users";
    public static final String FAILED_LOADING_DATA = "Failed loading data";
    public static final String LOADING_DATA_SUCCESS = "LOADING_DATA_SUCCESS";

    private FirebaseFirestore db;
    private CollectionReference coursesRef;
    private CollectionReference usersRef;
    private ArrayList<String> coursesIds;
    private HashMap<String, Course> courses;
    private Context context;
    private String currentCourseId;
    private String currentImagePath;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    private static String COURSES = "courses";
    boolean sentOnce;
    AtomicInteger numOfCourses;
    int size;


    public FireStoreHandler(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
        coursesRef = db.collection(COURSES);
        usersRef = db.collection(USERS);
        coursesIds = new ArrayList<>();
        courses = new HashMap<>();
        sentOnce = false;
        loadData();

    }

    public void createUserIfNotExists(FirebaseUser user) {
        if (user != null && user.getEmail() != null) {
            final DocumentReference userRef = usersRef.document(user.getEmail());
            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot == null || !documentSnapshot.exists()) {
                                userRef.set(new HashMap<String, Object>(), SetOptions.merge());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v(TAG, "error loading user");
                        }
                    });
        }
    }

    public void loadData() {
        coursesRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            size = task.getResult().size();
                            numOfCourses = new AtomicInteger();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                coursesIds.add(document.getId());
                                loadCourse(document.getId(), size);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            sendBroadcastWhenOpeningApp(FAILED_LOADING_DATA);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendBroadcastWhenOpeningApp(FAILED_LOADING_DATA);
                    }
                });
    }

    private void sendBroadcastWhenOpeningApp(String action) {
        if (!sentOnce) {
            Intent intent = new Intent();
            intent.setAction(action);
            context.sendBroadcast(intent);
            sentOnce = true;
        }
    }

    public void updateData() {
        for (Course c : courses.values()) {
            updateCourse(c.getId());
        }
    }

    public void updateCourse(String id) { //TODO - should receive id and not Course object
        Course c = courses.get(id);
        if (c == null) {
            Log.e("update_fail", COURSE_UPDATE_FAILURE_MESSAGE);
        } else {
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
        ArrayList<Question> questions = Objects.requireNonNull(courses.get(currentCourseId))
                .getQuestions();
        for (int i = 0; i < questions.size(); ++i) {
            if (questions.get(i).getImagePath().compareTo(currentImagePath) == 0) {
                questions.set(i, question);
            }
        }
        updateCourse(Objects.requireNonNull(currentCourseId));
    }

    /**
     * Uploads the image in localImagePath to fireStore, updates the link of newQuestion to the URI
     * of the uploaded image
     *
     * @param localImagePath  - a URI of local file path of the image to be uploaded
     * @param storedImagePath -the path in the fireStore storage to which the image will be uploaded
     * @param title           - the title of the new question
     * @param ctx             - context
     */
    public void uploadQuestionImage(final Uri localImagePath, String storedImagePath,
                                    final String title, final Context ctx) {

        final ProgressDialog progressDialog = getProgressDialog(ctx);
        StorageReference storageRef = storage.getReference();
        final StorageReference questionsRef = storageRef.child(storedImagePath + ".jpg");
        UploadTask uploadTask = questionsRef.putFile(localImagePath);
        final Intent intent = new Intent();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                setUploadOnFailure(progressDialog, ctx, intent,
                        QuestionActivity.FAILED_TO_UPLOAD_ANSWER_IMG);
                //TODO - add a broadcast of failure
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(ctx, "Uploaded", Toast.LENGTH_SHORT).show();
                updateNewQuestionUri(title, questionsRef, taskSnapshot, IMAGE_UPLOADED);
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

    private void setUploadOnFailure(ProgressDialog progressDialog, Context ctx, Intent intent,
                                    String failedToUploadAnswerImg) {
        progressDialog.dismiss();
        Toast.makeText(ctx, "Failed Uploading", Toast.LENGTH_SHORT).show();
        Log.i(UNSUCCESSFUL_IMAGE_UPLOAD, "unsuccessful image upload");
        intent.setAction(failedToUploadAnswerImg);
        context.sendBroadcast(intent);
    }

    /**
     * Uploads the image in localImagePath to fireStore, updates the link of newQuestion to the URI
     * of the uploaded image
     *
     * @param localImagePath  - a URI of local file path of the image to be uploaded
     * @param storedImagePath -the path in the fireStore storage to which the image will be uploaded
     * @param title           - the title of the new question, to which the new image belongs
     */
    public void uploadAnswerImage(final Uri localImagePath, String storedImagePath,
                                  final String title, final Context ctx) { //todo oooooooo

        final ProgressDialog progressDialog = getProgressDialog(ctx);
        StorageReference storageRef = storage.getReference();
        final StorageReference answersRef = storageRef.child(storedImagePath + ".jpg");
        UploadTask uploadTask = answersRef.putFile(localImagePath);
        final Intent intent = new Intent();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                setUploadOnFailure(progressDialog, ctx, intent, AddQuestionActivity.FAILED_TO_UPLOAD);
                //TODO - add a broadcast of failure
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(ctx, "Uploaded", Toast.LENGTH_SHORT).show();
                updateNewQuestionUri(title, answersRef, taskSnapshot,
                        QuestionActivity.FINISHED_UPLOAD_ANSWER_IMG);
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

    private ProgressDialog getProgressDialog(Context ctx) {
        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * Gets the URI of the recently uploaded image of the newQuestion
     *
     * @param title        the new Question's title
     * @param questionsRef a reference to the questions images storage on firebase
     * @param uploadTask   the upload task that uploads the image
     */
    private void updateNewQuestionUri(final String title,
                                      final StorageReference questionsRef,
                                      final UploadTask.TaskSnapshot uploadTask,
                                      final String action) {
        questionsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String photoUrl = uri.toString();
                Intent intent = new Intent();
                intent.setAction(action);
                intent.putExtra("UPDATED URL", photoUrl);
                intent.putExtra(QuestionActivity.TITLE, title);
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


//    public void buildDB() { // call once
//        Course course1 = new Course(); // todo - temp
//        course1.setName("Databases");
//
//        Question q1 = new Question();
//        q1.setId("q1");
//        q1.setTitle("question 1");
//        q1.setLink("gs://groupstudyingapp.appspot.com/questions/q1.PNG");
//        q1.setRating(5);
//        q1.setImagePath("gs://groupstudyingapp.appspot.com/questions/q1.PNG");
//
//        Answer a1 = new Answer();
//        a1.setId("a1");
//        a1.setRating(5);
//        a1.setLink("gs://groupstudyingapp.appspot.com/answers/a1.PNG");
//        q1.setTitle("question 1");
//        a1.setImagePath("gs://groupstudyingapp.appspot.com/answers/a1.PNG");
//        q1.addAnswer(a1);
//
//        course1.addQuestion(q1);
//
//        Question q2 = new Question();
//        q2.setId("q2");
//        q2.setTitle("question 2");
//        q2.setLink("gs://groupstudyingapp.appspot.com/questions/q2.PNG");
//        q2.setRating(3);
//        q2.setImagePath("gs://groupstudyingapp.appspot.com/questions/q2.PNG");
//
//        Answer a2 = new Answer();
//        a2.setId("a2");
//        a2.setRating(4);
//        a2.setLink("gs://groupstudyingapp.appspot.com/answers/a2.PNG");
//        a2.setImagePath("gs://groupstudyingapp.appspot.com/answers/a2.PNG");
//        q2.addAnswer(a2);
//
//        course1.addQuestion(q2);
//
//        addCourse(course1);
//
//        Course course2 = new Course(); // todo - temp
//        course2.setName("Image Processing");
//
//        Question q3 = new Question();
//        q3.setId("q3");
//        q3.setTitle("question 3");
//        q3.setLink("gs://groupstudyingapp.appspot.com/questions/q3.PNG");
//        q3.setRating(2);
//        q3.setImagePath("gs://groupstudyingapp.appspot.com/questions/q3.PNG");
//
//        Answer a3 = new Answer();
//        a3.setId("a3");
//        a3.setRating(4);
//        a3.setLink("gs://groupstudyingapp.appspot.com/answers/a3.PNG");
//        a3.setImagePath("gs://groupstudyingapp.appspot.com/answers/a3.PNG");
//        q3.addAnswer(a3);
//
//        course2.addQuestion(q3);
//
//        Question q4 = new Question();
//        q4.setId("q4");
//        q4.setTitle("question 4");
//        q4.setLink("gs://groupstudyingapp.appspot.com/questions/q5.PNG");
//        q4.setRating(5);
//        q4.setImagePath("gs://groupstudyingapp.appspot.com/questions/q5.PNG");
//
//        Answer a4 = new Answer();
//        a4.setId("a4");
//        a4.setRating(4);
//        a4.setLink("gs://groupstudyingapp.appspot.com/answers/a4.PNG");
//        a4.setImagePath("gs://groupstudyingapp.appspot.com/answers/a4.PNG");
//        q4.addAnswer(a4);
//
//        course2.addQuestion(q4);
//
//        addCourse(course2);
//
//    }

    private void loadCourse(String courseId, final int size) {
        final String cid = courseId;
        DocumentReference docRef = coursesRef.document(courseId);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        courses.put(cid, documentSnapshot.toObject(Course.class));
                        if (numOfCourses.incrementAndGet() == size) {
                            sendBroadcastWhenOpeningApp(LOADING_DATA_SUCCESS);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendBroadcastWhenOpeningApp(FAILED_LOADING_DATA);
            }
        });
    }

    public void addNewAnswer(String questionId, String title, String answerUrl) {
        Question question = getQuestionById(questionId);
        question.addAnswer(title, answerUrl);
        updateCourse(currentCourseId);
    }

    //TODO Ido/Mor check if we need it
    public void setCurrentCourseId(int position) {
        currentCourseId = coursesIds.get(position);
    }

    public void setCurrentCourseId(String id) {
        currentCourseId = id;
    }

    public void setCurrentImagePath(String path) {
        currentImagePath = path;
    }


    public Course getCurrentCourse() {
        return courses.get(currentCourseId);
    }

    public ArrayList<String> getCoursesIds() {
        return coursesIds;
    }

    public Course getCourseById(String id) {
        return courses.get(id);
    }

    public Question getQuestionById(String id) {
        Course currentCourse = getCurrentCourse();
        for (Question q : currentCourse.getQuestions()) {
            if (q.getId().equals(id)) {
                return q;
            }
        }
        return null;
    }
}
