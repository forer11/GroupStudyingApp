package com.example.groupstudyingapp;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Answer implements Serializable {

    private String title;
    private String id;
    private String link;
    private int numOfRates;
    private float rating;
    private String imagePath;

    Answer(){}

    Answer(String title, String imagePath) {
        this.rating = 0;
        this.numOfRates = 0;
        this.title = title;
        this.imagePath = imagePath;
        this.id = null;
    }

    /////////////////////////////////// Getters ////////////////////////////////////////////////////
    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public float getRating() {
        return rating;
    }

    public String getImagePath() { return imagePath; }

    public String getTitle() {
        return title;
    }

    /////////////////////////////////// Setters ////////////////////////////////////////////////////
    public void setId(String id) {
        this.id = id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public void setTitle(String title) {
        this.title = title;
    }

    public static answersComparator getAnswerComparator() {
        return new answersComparator();
    }

    public static class answersComparator implements Comparator<Answer> {

        @Override
        public int compare(Answer a1, Answer a2) {
            if (a1.getRating() > a2.getRating()) {
                return -1;
            }
            else  if (a2.getRating() > a1.getRating()) {
                return  1;
            }
            return 0;
        }
    }
}
