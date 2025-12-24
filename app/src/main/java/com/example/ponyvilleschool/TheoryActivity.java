package com.example.ponyvilleschool;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ponyvilleschool.utils.AppState;

public class TheoryActivity extends AppCompatActivity {

    private int taskId;
    private Button btnContinue;
    private TaskData task;
    private MediaController mediaController;
    private VideoView videoView;
    private int videoPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        taskId = getIntent().getIntExtra("task_id", -1);
        task = AppState.getInstance().getTaskData(taskId);
        if (task == null) {
            finish();
            return;
        }
        if (task.text != null && !task.text.isEmpty()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_theory_text);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.theory_text_layout), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            setupTextTheory();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_theory_video);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.theory_video_layout), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            setupVideoTheory();
        }
        initializeTimer();
    }
    private void setupTextTheory()  //Установка текста
    {
        TextView title = findViewById(R.id.tvTheoryTitle);
        TextView text = findViewById(R.id.tvTheoryText);
        ImageButton btnBack = findViewById(R.id.btnTheoryBack);
        btnContinue = findViewById(R.id.btnTheoryContinue);

        title.setText(task.title);
        text.setText(task.text);

        btnBack.setOnClickListener(v -> finish());

        btnContinue.setEnabled(false);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            btnContinue.setEnabled(true);
        }, 8000);

        btnContinue.setOnClickListener(v -> {
            openPractice();
        });
    }
    private void setupVideoTheory() //Установка видео
    {
        TextView title = findViewById(R.id.tvTheoryTitle);
        ImageButton btnBack = findViewById(R.id.btnTheoryBack);
        btnContinue = findViewById(R.id.btnTheoryContinue);
        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        title.setText(task.title);

        btnBack.setOnClickListener(v -> finish());

        videoView.setVideoURI(Uri.parse(task.video));
        videoView.setOnPreparedListener(mp -> {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();

            float videoRatio = (float) videoWidth / videoHeight;
            float screenRatio = (float) videoView.getWidth() / videoView.getHeight();

            ViewGroup.LayoutParams params = videoView.getLayoutParams();

            if (videoRatio > screenRatio) {
                params.width = videoView.getWidth();
                params.height = (int) (videoView.getWidth() / videoRatio);
            } else {
                params.height = videoView.getHeight();
                params.width = (int) (videoView.getHeight() * videoRatio);
            }

            videoView.setLayoutParams(params);
            mp.start();
        });

        btnContinue.setEnabled(false);
        videoView.setOnCompletionListener(mp -> {
            btnContinue.setEnabled(true);
        });

        btnContinue.setOnClickListener(v -> {
            openPractice();
        });
    }
    private void openPractice() //Переход к практическому заданию
    {
        Intent intent = new Intent(this, PracticeActivity.class);
        intent.putExtra("task_id", task.order - 1);
        startActivity(intent);
        finish();
    }
    private void initializeTimer() //Инициализация таймера
    {
        btnContinue.setEnabled(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            btnContinue.setEnabled(true);
        }, 10_000);
    }

    @Override
    protected void onPause() //Пауза плеера
    {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoPosition = videoView.getCurrentPosition();
            videoView.pause();
        }
    }
    @Override
    protected void onResume() //Воспроизведение плеера
    {
        super.onResume();
        if (videoView != null) {
            videoView.seekTo(videoPosition);
            videoView.start();
        }
    }
}
