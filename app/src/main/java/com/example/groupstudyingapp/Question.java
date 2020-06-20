package com.example.groupstudyingapp;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Question implements Serializable {

    private String title;
    private String id;
    private String link;
    private int numOfRates;
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
        this.numOfRates = 0;
        this.title = title;
        this.imagePath = imagePath;
        this.id = null;
    }

    /////////////////////////////////// Getters ////////////////////////////////////////////////////
    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public float getRating() {
        return rating;
    }

    public int getNumOfRates() { return numOfRates; }

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

    public void setLink(String link) {
        this.link = link;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setNumOfRates(int numOfRates) { this.numOfRates = numOfRates; }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public static questionComparator getQuestionComparator() {
        return new questionComparator();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addAnswer(String title, String answersUrl) {
        Answer newAnswer = new Answer(title, answersUrl);
        answers.add(newAnswer);
    }


    public static class questionComparator implements Comparator<Question> {

        @Override
        public int compare(Question q1, Question q2) {
            if (q1.getRating() > q2.getRating()) {
                return -1;
            }
            else  if (q2.getRating() > q1.getRating()) {
                return  1;
            }
            return 0;
        }
    }

}
