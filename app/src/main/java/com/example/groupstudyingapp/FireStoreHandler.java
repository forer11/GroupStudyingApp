package com.example.groupstudyingapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class FireStoreHandler {
    private FirebaseFirestore db;
    private CollectionReference coursesRef;
    private ArrayList<String> coursesIds = new ArrayList<String>();
    private Course currentCourse;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private static String COURSES = "courses";

    public FireStoreHandler() {
        db = FirebaseFirestore.getInstance();
        coursesRef = db.collection(COURSES);

    }

    public void loadData(){
        //todo
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


}
