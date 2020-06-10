package com.example.groupstudyingapp;

import android.net.Uri;

import java.io.Serializable;

public class Answer implements Serializable {

    private String id;
    private Uri link;
    private float rating;
    private String imagePath;

    Answer(){}

    /////////////////////////////////// Getters ////////////////////////////////////////////////////
    public String getId() {
        return id;
    }

    public Uri getLink() {
        return link;
    }

    public float getRating() {
        return rating;
    }

    public String getImagePath() { return imagePath; }

    /////////////////////////////////// Setters ////////////////////////////////////////////////////
    public void setId(String id) {
        this.id = id;
    }

    public void setLink(Uri link) {
        this.link = link;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

}
