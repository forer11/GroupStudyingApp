package com.example.groupstudyingapp;

public class Question {

    private String title;
    private String id;
    private String link;
    private float rating;
    private Answer[] answers;
    //todo tags?

    Question(){}

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

    public Answer[] getAnswers() {
        return answers;
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

    public void setAnswers(Answer[] answers) {
        this.answers = answers;
    }

}
