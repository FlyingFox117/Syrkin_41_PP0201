package com.example.ponyvilleschool.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ponyvilleschool.models.CourseProgress;
import com.example.ponyvilleschool.R;
import com.example.ponyvilleschool.utils.AppState;
import com.google.gson.Gson;

import java.util.List;

public class CourseAdapter extends BaseAdapter {

    private final Context context;
    private final List<CourseProgress> courses;
    private final LayoutInflater inflater;

    public CourseAdapter(Context context, List<CourseProgress> courses) {
        this.context = context;
        this.courses = courses;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return courses.get(position).course_id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.model_course, parent, false);
        }

        ImageView img = convertView.findViewById(R.id.imgCourse);
        TextView title = convertView.findViewById(R.id.tvCourseTitle);
        TextView description = convertView.findViewById(R.id.tvCourseDescription);
        TextView progress = convertView.findViewById(R.id.tvCourseProgress);

        CourseProgress course = courses.get(position);

        img.setImageResource(course.imageResource);
        title.setText(course.title);
        description.setText(course.description);
        if (course.taskCount == 0)
        {
            progress.setText("Пока тут пусто!");
        }
        else {
            int percent = (int) ((double) course.progress / course.taskCount * 100.00);
            progress.setText("Прогресс: " + percent + "%");
        }
        Log.e("Данные курса", new Gson().toJson(AppState.getInstance().coursesProgress.get(position)));
        return convertView;
    }
}