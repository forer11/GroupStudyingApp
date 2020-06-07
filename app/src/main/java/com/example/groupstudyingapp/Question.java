package com.example.groupstudyingapp;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {

    private String title;
    private String id;
    private Uri link;
    private float rating;
    private ArrayList<Answer> answers;
    private String imagePath;
    //todo tags?

    Question() {
        answers = new ArrayList<>();
    }

    Question(String title, String imagePath) {
        this.answers = new ArrayList<>();
        this.rating = 0;
        this.title = title;
        this.imagePath = imagePath;
    }

    /////////////////////////////////// Getters ////////////////////////////////////////////////////
    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public Uri getLink() {
        return link;
    }

    public float getRating() {
        return rating;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public String getImagePath() {
        return imagePath;
    }

    /////////////////////////////////// Setters ////////////////////////////////////////////////////

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLink(Uri link) {
        this.link = link;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addAnswer(Answer a) {
        answers.add(a);
    }

}
