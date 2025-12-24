package com.example.ponyvilleschool.models;

//MOCK-класс для хранения данных о всех курсах, доступных в программе
public class CourseProgress {
    public int course_id;
    public String title;
    public String description;
    public int taskCount;
    public int progress;
    public int imageResource;

    public CourseProgress(
            int course_id,
            String title,
            String description,
            int taskCount,
            int progress,
            int imageResource
    )
    {
        this.course_id = course_id;
        this.title = title;
        this.description = description;
        this.taskCount = taskCount;
        this.progress = progress;
        this.imageResource = imageResource;
    }
}
