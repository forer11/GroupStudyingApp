package com.example.groupstudyingapp;

import java.util.ArrayList;

public class Course {

    private String name;
    private String id;
    private ArrayList<Question> questions;

    public Course(){
        questions = new ArrayList<Question>();
    }

    /////////////////////////////////// Getters ////////////////////////////////////////////////////
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    /////////////////////////////////// Setters ////////////////////////////////////////////////////

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addQuestion(Question q){
        questions.add(q);
        //todo updateCourse
    }


}
