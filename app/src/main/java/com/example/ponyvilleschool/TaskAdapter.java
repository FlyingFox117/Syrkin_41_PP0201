package com.example.ponyvilleschool;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ponyvilleschool.utils.AppState;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TaskAdapter extends BaseAdapter {

    private final Context context;
    private final List<TaskData> tasks;
    private final LayoutInflater inflater;

    public TaskAdapter(Context context, List<TaskData> tasks) {
        this.context = context;
        this.tasks = tasks;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) //Генерация заданий
    {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.model_task, parent, false);
        }

        ImageView lock = convertView.findViewById(R.id.imgLock);
        TextView title = convertView.findViewById(R.id.tvTaskTitle);
        TextView description = convertView.findViewById(R.id.tvTaskDescription);
        TextView status = convertView.findViewById(R.id.tvTaskStatus);
        ImageView taskimage = convertView.findViewById(R.id.imgTask);

        TaskData task = tasks.get(position);

        int course = AppState.getInstance().selectedCourse;
        int progress = AppState.getInstance().getCoursesProgress().get(course).progress;
        int order = task.order;
        if (order <= progress) {
            status.setText("Пройдено");
            status.setTextColor(Color.parseColor("#4CAF50"));
        }
        else if (order == progress + 1)
        {
            status.setText("Новое задание");
            status.setTextColor(Color.parseColor("#FF9800"));
        }
        else
        {
            lock.setVisibility(View.VISIBLE);
            status.setText("Закрыто");
            status.setTextColor(Color.GRAY);
        }
        title.setText(task.title);
        description.setText(task.description);
        Picasso.get()
                .load(task.image_url)
                .into(taskimage);
        Log.e("Данные задания", new Gson().toJson(AppState.getInstance().tasksData.get(position)));
        return convertView;
    }
}
