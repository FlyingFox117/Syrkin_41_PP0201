package com.example.ponyvilleschool;

import com.example.ponyvilleschool.models.Answer;

import java.util.List;

public class QuestionData {
    public int id;
    public int task_id;
    public String text;
    public int correct_answer_id;
    public List<Answer> answers;

    public QuestionData(
            int id,
            int task_id,
            String text,
            int correct_answer_id,
            List<Answer> answers
    )
    {
        this.id = id;
        this.task_id = task_id;
        this.text = text;
        this.correct_answer_id = correct_answer_id;
        this.answers = answers;
    }
}

