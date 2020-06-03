package com.example.groupstudyingapp;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class FireStoreHandler {
    private FirebaseFirestore db;
    private CollectionReference coursesRef;
    private ArrayList<String> coursesIds;
    private HashMap<String, Course> courses;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private static String COURSES = "courses";

    public FireStoreHandler() {
        db = FirebaseFirestore.getInstance();
        coursesRef = db.collection(COURSES);
        coursesIds = new ArrayList<>();
        courses = new HashMap<>();
        loadData();

    }

    public void loadData(){

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

    public void updateData(){
        //todo
    }

    private void updateCourse(Course c, final Context context){
        coursesRef.document(c.getId()).set(c, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( context, "failed to get data", Toast
                                .LENGTH_SHORT).show();
                    }
                });
    }

    public void addCourse(Course c){ // todo needed?
        final Course course = c;
        db.collection(COURSES)
                .add(course)
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


    public void saveNote(Note note, final Context context) {
        coursesRef.document("My First Note").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void buildDB() { // call once
        Course course1 =  new Course(); // todo - temp
        course1.setName("Data Structures");

        Question q1 = new Question();
        q1.setId("q1");
        q1.setTitle("question 1");
        q1.setLink("www.google.com");
        q1.setRating(5);
        q1.setImagePath("gs://groupstudyingapp.appspot.com/questions/q1.PNG");

        Answer a1 = new Answer();
        a1.setId("a1");
        a1.setRating(5);
        a1.setImagePath("gs://groupstudyingapp.appspot.com/answers/a1.PNG");
        q1.addAnswer(a1);

        course1.addQuestion(q1);

        Question q2 = new Question();
        q2.setId("q2");
        q2.setTitle("question 2");
        q2.setLink("www.google.com");
        q2.setRating(3);
        q2.setImagePath("gs://groupstudyingapp.appspot.com/questions/q2.PNG");

        Answer a2 = new Answer();
        a2.setId("a2");
        a2.setRating(4);
        a2.setImagePath("gs://groupstudyingapp.appspot.com/answers/a2.PNG");
        q2.addAnswer(a2);

        course1.addQuestion(q2);

        addCourse(course1);

        Course course2 =  new Course(); // todo - temp
        course2.setName("Intro");

        Question q3 = new Question();
        q3.setId("q3");
        q3.setTitle("question 3");
        q3.setLink("www.google.com");
        q3.setRating(2);
        q3.setImagePath("gs://groupstudyingapp.appspot.com/questions/q3.PNG");

        Answer a3 = new Answer();
        a3.setId("a3");
        a3.setRating(4);
        a3.setImagePath("gs://groupstudyingapp.appspot.com/answers/a3.PNG");
        q3.addAnswer(a3);

        course2.addQuestion(q3);

        Question q4 = new Question();
        q4.setId("q4");
        q4.setTitle("question 4");
        q4.setLink("www.google.com");
        q4.setRating(5);
        q4.setImagePath("gs://groupstudyingapp.appspot.com/questions/q5.PNG");

        Answer a4 = new Answer();
        a4.setId("a4");
        a4.setRating(4);
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

    public ArrayList<Course> getCourses() {
        return new ArrayList<Course>(courses.values());
    }

}
