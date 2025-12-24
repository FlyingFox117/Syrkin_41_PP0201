package com.example.ponyvilleschool;

import static com.example.ponyvilleschool.Supabase.getPracticeData;
import static com.example.ponyvilleschool.Supabase.submitResult;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ponyvilleschool.models.Answer;
import com.example.ponyvilleschool.utils.AppState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PracticeActivity extends AppCompatActivity {
    private TextView tvQuestion, tvProgress, tvTitle;
    private Button btnAnswer1, btnAnswer2, btnAnswer3, btnContinue;
    private ImageButton btnPracticeBack;
    private LinearLayout header;
    private List<QuestionData> questions = new ArrayList<>();
    private int currentQuestionIndex = 0, correctAnswers = 0;
    private TaskData task;
    private boolean answered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.practice_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        int taskId = getIntent().getIntExtra("task_id", -1);
        task = AppState.getInstance().getTaskData(taskId);

        tvQuestion = findViewById(R.id.practiceQuestion);
        header = findViewById(R.id.header);
        tvTitle = findViewById(R.id.tvPracticeTitle);
        tvProgress = findViewById(R.id.tvCurrentTask);
        btnAnswer1 = findViewById(R.id.btnAnswerOne);
        btnAnswer2 = findViewById(R.id.btnAnswerTwo);
        btnAnswer3 = findViewById(R.id.btnAnswerThree);
        btnPracticeBack = findViewById(R.id.btnPracticeBack);
        btnContinue = findViewById(R.id.btnSubmit);
        btnContinue.setOnClickListener(v -> {
            ++currentQuestionIndex;
            showQuestion();
        });
        btnPracticeBack.setOnClickListener(v -> {
            finish();
        });
        loadQuestion();
    };
    private void loadQuestion() //Загрузка вопросов
    {
        Map<String, Object> params = new HashMap<>();
        params.put("task_id", task.id);
        getPracticeData(
                this,
                params,
                questionsFromCloud -> {
                    questions.clear();
                    questions.addAll(questionsFromCloud);
                    tvTitle.setText(task.title);
                    showQuestion();
                },
                () -> { Toast.makeText(this, "Ошибка загрузки вопросов", Toast.LENGTH_SHORT).show();
                loadQuestion();
                }
        );
    }
    private void showQuestion() //Показ вопроса/завершение задания
    {
        answered = false;
        if (currentQuestionIndex >= questions.size())
        {
            int total = questions.size();
            int correct = correctAnswers;
            int percent = (int) ((correct * 100f) / total);

            String message =
                    "Правильных ответов: " + correct + " из " + total +
                            "\nРезультат: " + percent + "%";

            new AlertDialog.Builder(this)
                    .setTitle("Задание завершено")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Завершить", (dialog, which) -> {
                        submitResultAndExit(correct);
                    })
                    .show();
            return;
        }
        QuestionData q = questions.get(currentQuestionIndex);

        animateQuestionChange(() -> {
            tvQuestion.setText(q.text);
            tvProgress.setText(
                    (currentQuestionIndex + 1) + "/" + questions.size()
            );
            header.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.head)
            );
            setupAnswerButton(btnAnswer1, q.answers.get(0));
            setupAnswerButton(btnAnswer2, q.answers.get(1));
            setupAnswerButton(btnAnswer3, q.answers.get(2));
            if (currentQuestionIndex == questions.size() - 1)
            {
                btnContinue.setText("Завершить задание");
            } else
                btnContinue.setText("Продолжить");
        });
    }
    private void animateQuestionChange(Runnable updateContent) //Анимация для вопросов
    {
        tvQuestion.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    updateContent.run();
                    tvQuestion.animate()
                            .alpha(1f)
                            .setDuration(200)
                            .start();
                })
                .start();
    }
    private void setupAnswerButton(Button button, Answer answer) //Настройка кнопок ответа
    {
        button.setText(answer.text);
        button.setTag(answer.id);
        button.setEnabled(true);

        button.setOnClickListener(v -> onAnswerSelected(button));
    }
    private void onAnswerSelected(Button clickedButton) //Выбор ответа
    {
        if (answered) return;

        answered = true;

        QuestionData q = questions.get(currentQuestionIndex);
        int selectedAnswerId = (int) clickedButton.getTag();

        boolean isCorrect = selectedAnswerId == q.correct_answer_id;

        if (isCorrect) {
            correctAnswers++;
            Toast.makeText(this, "Правильный ответ!", Toast.LENGTH_SHORT).show();
            header.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.finished)
            );
        } else {
            Toast.makeText(this, "Неправильно", Toast.LENGTH_SHORT).show();
            header.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.blocked)
            );
        }

        disableAnswerButtons();
    }
    private void disableAnswerButtons() //Выключение кнопок после ответа
    {
        btnAnswer1.setEnabled(false);
        btnAnswer2.setEnabled(false);
        btnAnswer3.setEnabled(false);
    }
    private void submitResultAndExit(int percent)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("p_user_id", AppState.getInstance().currentUser.id);
        params.put("p_task_id", task.id);
        params.put("p_score", percent);
        params.put("p_course_id", AppState.getInstance().selectedCourse + 1);
        submitResult(
                this,
                params,
                () -> {
                    AppState.getInstance().currentUser.available = false; //Обновление лимита
                    Toast.makeText(
                            this,
                            "Результат сохранён!",
                            Toast.LENGTH_SHORT
                    ).show();
                    finish();
                },
                () -> Toast.makeText(
                        this,
                        "Ошибка отправки результата",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}
