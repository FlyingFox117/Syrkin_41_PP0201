package com.example.ponyvilleschool;

import static com.example.ponyvilleschool.Supabase.getCourseData;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ponyvilleschool.utils.AppState;

import java.util.HashMap;
import java.util.Map;

public class TaskActivity extends AppCompatActivity {

    private int courseId, userId; //ID курса и пользователя
    private ListView listView; //Отображаемый список заданий
    private TextView txtHeader, available; //Заголовок
    private SwipeRefreshLayout swiper; //Обновление
    private ImageButton btnBack; //Кнопка "Назад"
    private TaskAdapter adapter; //Адаптер для отображения заданий

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tasks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tasks_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        courseId = getIntent().getIntExtra("course_id", -1);
        userId = AppState.getInstance().currentUser.id;

        available = findViewById(R.id.tvAvailableTasks);
        listView = findViewById(R.id.listTasks);
        txtHeader = findViewById(R.id.tvCourseTitle);
        btnBack = findViewById(R.id.btnBack);
        swiper = findViewById(R.id.tSwipeRefresh);

        adapter = new TaskAdapter(
                this,
                AppState.getInstance().tasksData); //Адаптер заданий
        listView.setAdapter(adapter);
        loadTasks(courseId, userId);
        btnBack.setOnClickListener(v -> onBackPressed());
        swiper.setOnRefreshListener(() ->{
            loadTasks(courseId, userId);
                });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectTask(position);
        });

        String courseTitle = getIntent().getStringExtra("course_title");
        txtHeader.setText(courseTitle);
    }

    @Override
    public void onBackPressed() //Кнопка "Назад"
    {
        TaskActivity.super.onBackPressed();
        finish();
    }
    private void loadTasks(int courseId, int userId) //Загрузка заданий
    {
        swiper.setRefreshing(true);
        AppState.getInstance().clearTasks();

        Map<String, Object> params = new HashMap<>();
        params.put("course_id", courseId);
        params.put("p_user_id", userId);

        getCourseData(
                this,
                params,
                tasks -> {
                    AppState.getInstance().tasksData.addAll(tasks);
                    adapter.notifyDataSetChanged();
                    swiper.setRefreshing(false);
                },
                () -> {
                    Toast.makeText(this, "Ошибка загрузки заданий", Toast.LENGTH_SHORT).show();
                    swiper.setRefreshing(false);
                }
        );
        if (AppState.getInstance().currentUser.available)
        {
            available.setText("1");
        } else
            available.setText("0");
    }
    private void selectTask(int position) //Выбор задания
    {
        if (!AppState.getInstance().currentUser.available)
        {
            Toast.makeText(
                    this,
                    "Твой лимит заданий на сегодня истёк",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        TaskData task = AppState.getInstance().tasksData.get(position);
        int taskOrder = task.order;
        int progress = AppState.getInstance()
                .getCoursesProgress().get(courseId - 1)
                .progress;

        if (taskOrder <= progress) {
            showDialog(
                    "Перепройти задание?",
                    () -> openTask(task)
            );
        }
        else if (taskOrder == progress + 1) {
            showDialog(
                    "Начать новое задание?",
                    () -> openTask(task)
            );
        }
        else {
            Toast.makeText(
                    this,
                    "Выполни предыдущие задания!",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
    private void showDialog(String message, Runnable onYes) //Диалоговое окно
    {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Да", (dialog, which) -> onYes.run())
                .setNegativeButton("Нет", null)
                .show();
    }
    private void openTask(TaskData task) //Запуск задания
    {
        Intent intent = new Intent(this, TheoryActivity.class);
        intent.putExtra("task_id", task.order - 1);
        startActivity(intent);
    }
}
