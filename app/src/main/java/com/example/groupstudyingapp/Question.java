package com.example.groupstudyingapp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Question implements Serializable {

    private String title;
    private String id;
    private String link;
    private int numOfRates;
    private float rating;
    private ArrayList<Answer> answers;
    private String imagePath;
    private Date creationDate;
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
        this.creationDate = new Date();
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

    public static questionCompareHighestRatingFirst getQuestionComparator() {
        return new questionCompareHighestRatingFirst();
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


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addAnswer(String title, String answersUrl) {
        Answer newAnswer = new Answer(title, answersUrl);
        answers.add(newAnswer);
    }

    public void sortAnswers() {
        answers.sort(Answer.getAnswerComparator());
    }


    public static class questionCompareHighestRatingFirst implements Comparator<Question> {

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

    public static class questionCompareLowestRatingFirst implements Comparator<Question> {

        @Override
        public int compare(Question q1, Question q2) {
            return new questionCompareHighestRatingFirst().compare(q2, q1);
        }
    }

    public static class questionCompareTitle implements Comparator<Question> {

        @Override
        public int compare(Question q1, Question q2) {
            return q1.title.compareTo(q2.title);
        }
    }

    public static class questionCompareDateCreated implements Comparator<Question> {

        @Override
        public int compare(Question q1, Question q2) {
            if (q1.creationDate == null){
                q1.creationDate = new Date(1999, 12, 30);
            }
            if(q2.creationDate == null){
                q2.creationDate = new Date(1999, 12, 30);
            }
            return q1.creationDate.compareTo(q2.creationDate);
        }
    }

}
