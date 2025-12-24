package com.example.ponyvilleschool.activities;

import static com.example.ponyvilleschool.Supabase.loadUserProgress;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ponyvilleschool.adapters.CourseAdapter;
import com.example.ponyvilleschool.models.CourseProgress;
import com.example.ponyvilleschool.R;
import com.example.ponyvilleschool.TaskActivity;
import com.example.ponyvilleschool.utils.AppState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseActivity extends AppCompatActivity {

    private TextView available; //Количество доступных заданий
    private SwipeRefreshLayout swiper; //Свипер - для обновления
    private ListView listView; //Список заданий

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupView();
        setupListeners();

        createMockCourses(); //Генерация локальных данных
        getUserProgress(); //Получение облачных данных
    }
    private void setupView() //Присвоение ID
    {
        available = findViewById(R.id.tvAvailableTasks);
        listView = findViewById(R.id.listCourses);
        swiper = findViewById(R.id.cSwipeRefresh);
        Toast.makeText(this, "Доступ: " + AppState.getInstance().currentUser.available, Toast.LENGTH_SHORT).show();
    }
    private void setupListeners() //Присвоение обработчиков
    {
        swiper.setOnRefreshListener(this::getUserProgress);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            courseClick(position);
        });
    }
    private void courseClick(int position) //Выбор курса
    {
        if (AppState.getInstance().coursesProgress.get(position).taskCount == 0)
        {
            Toast.makeText(this, "В этом курсе пока нет заданий!", Toast.LENGTH_SHORT).show();
            return;
        }
        CourseProgress selected = AppState.getInstance().coursesProgress.get(position);

        AppState.getInstance().setCurrentCourse(selected.course_id - 1);
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("course_id", selected.course_id);
        intent.putExtra("course_title", selected.title);
        startActivity(intent);
    }
    private void getUserProgress() //Получение прогресса пользователя
    {
        swiper.setRefreshing(true);
        Map<String, Object> newData = new HashMap<>();
        newData.put("p_user_id", AppState.getInstance().currentUser.id);
        loadUserProgress(
                this,
                newData,
                cloudData -> {
                    applyProgressFromCloud(cloudData);
                    setupCoursesList();
                    swiper.setRefreshing(false);
                },
                () -> {
                    Toast.makeText(this, "Ошибка загрузки прогресса!", Toast.LENGTH_SHORT).show();
                    setupCoursesList();
                    swiper.setRefreshing(false);
                }
        );
        if (AppState.getInstance().currentUser.available)
        {
            available.setText("1");
        } else
            available.setText("0");
    }
    private void applyProgressFromCloud(List<Map<String, Object>> cloudData) //Обработка облачных данных
    {

        for (Map<String, Object> row : cloudData) {
            int courseId = ((Double) row.get("course_id")).intValue();
            int taskCount = ((Double) row.get("task_count")).intValue();
            int progress = ((Double) row.get("progress")).intValue();

            for (CourseProgress course : AppState.getInstance().coursesProgress) {
                if (course.course_id == courseId) {
                    course.taskCount = taskCount;
                    course.progress = progress;
                    break;
                }
            }
        }
    }
    private void setupCoursesList() //Извлечение облачных данных
    {
        CourseAdapter adapter = new CourseAdapter(
                this,
                AppState.getInstance().coursesProgress
        );
        listView.setAdapter(adapter);
    }
    private void createMockCourses() //Создание локально-хранимых данных (временно)
    {
        List<CourseProgress> list = new ArrayList<>();
        list.add(new CourseProgress(1, "Уроки Честности", "Честность - умение быть правдивым, как Эпплджек...", 10, 0, R.drawable.applejack_course));
        list.add(new CourseProgress(2, "Основы Щедрости", "Щедрость - делиться с лучшим с другими, как Рарити...", 10, 0, R.drawable.rarity_course));
        list.add(new CourseProgress(3, "Устав Верности", "Верность - быть рядом с друзьями в трудную минуту, как Радуга...", 10, 0, R.drawable.rainbow_course));
        list.add(new CourseProgress(4, "Курс Доброты", "Доброта - помогать и заботиться о других, как Флаттершай...", 10, 0, R.drawable.fluttershy_course));
        list.add(new CourseProgress(5, "Праздник Радости", "Радость - находить позитив в любом дне, как Пинки Пай...", 10, 0, R.drawable.pinkie_course));
        list.add(new CourseProgress(6, "Книга Гармония","Гармония - соединение всех элементов в одно целое.", 10, 0, R.drawable.twilight_course));
        AppState.getInstance().coursesProgress = list;
    }
}
