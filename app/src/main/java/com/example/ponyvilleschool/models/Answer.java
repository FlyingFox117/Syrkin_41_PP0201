package com.example.ponyvilleschool.models;

public class Answer{
    public int id;
    public int question_id;
    public String text;

    public Answer(
            int id,
            int question_id,
            String text
    )
    {
        this.id = id;
        this.question_id = question_id;
        this.text = text;
    }
}
