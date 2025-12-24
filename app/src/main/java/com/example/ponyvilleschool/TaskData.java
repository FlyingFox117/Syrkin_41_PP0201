package com.example.ponyvilleschool;

import android.content.Intent;

public class TaskData {
    int id;
    String title;
    int order;
    String image_url;
    String text;
    String video;
    String description;
    Integer result;

    public TaskData(
            int id,
            String title,
            int order,
            String image_url,
            String text,
            String video,
            String description,
            Integer result
    ){
        this.id = id;
        this.title = title;
        this.order = order;
        this.image_url = image_url;
        this.description = description;
        if (text != null) {
            this.text = text;
        } else
            this.video = video;
        this.result = result;
    }
}
