package com.example.ponyvilleschool.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import static com.example.ponyvilleschool.Supabase.user_auth;
import static com.example.ponyvilleschool.Supabase.user_register;

import com.example.ponyvilleschool.R;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {
    private boolean isLoginMode = true; //Режим авторизации
    private TextView tvTitle, tvSwitchMode; //Текстовые подписи
    private EditText etLogin, etPassword, etRepeatPassword, etName; //Поля ввода
    private Button btnLogin, btnRegister; //Кнопки

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.auth_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupView();
        setupListeners();
        };
    private void setupView() {
        tvTitle = findViewById(R.id.tvTitle);
        tvSwitchMode = findViewById(R.id.tvSwitchMode);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
        etName = findViewById(R.id.etName);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    } //Присвоение ID
    private void setupListeners() {
        tvSwitchMode.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            updateUI();
        });

        btnLogin.setOnClickListener(v -> {
            if (isLoginMode) {
                login();
            } else {
                switchToLogin();
            }
        });

        btnRegister.setOnClickListener(v -> {
            if (!isLoginMode) {
                register();
            } else {
                switchToRegister();
            }
        });
    } //Присвоение обработчиков

    private void updateUI() {
        if (isLoginMode) {
            tvTitle.setText("Вход");

            etName.setVisibility(View.GONE);
            etRepeatPassword.setVisibility(View.GONE);

            btnLogin.setText("Войти");
            btnRegister.setText("Зарегистрироваться");

            tvSwitchMode.setText("Нет аккаунта? Зарегистрироваться");
        } else {
            tvTitle.setText("Регистрация");

            etName.setVisibility(View.VISIBLE);
            etRepeatPassword.setVisibility(View.VISIBLE);

            btnLogin.setText("Назад ко входу");
            btnRegister.setText("Зарегистрироваться");

            tvSwitchMode.setText("Уже есть аккаунт? Войти");
        }
    } //Смена режима регистрация/авторизация
    private void switchToLogin() {
        isLoginMode = true;
        updateUI();
    } //К Авторизации
    private void switchToRegister() {
        isLoginMode = false;
        updateUI();
    } //К регистрации
    private void login() {
        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();

        Map<String, Object> newData = new HashMap<>(); //Hash-карта с данными дл запроса
        newData.put("login", login);
        newData.put("password", password);
        user_auth(
                this, //контекст
                newData, //Hash-карта
                () -> { Intent intent = new Intent(this, CourseActivity.class);
            startActivity(intent);
            finish();;}, //Успешное выполнение
                () -> {}); //Неуспешное выполнение
    } //Авторизация
    private void register() {
        String login = etLogin.getText().toString();
        String password = etPassword.getText().toString();
        String repeat = etRepeatPassword.getText().toString();
        String name = etName.getText().toString();

        if (login.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Пароли не может быть короче восьми символов", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repeat)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Регистрация: " + name, Toast.LENGTH_SHORT).show();

        Map<String, Object> newData = new HashMap<>();
        newData.put("p_login", login);
        newData.put("p_password", password);
        newData.put("p_name", password);
        user_register(
                this,
                newData,
                () -> {
                    Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                },
                () -> {
                    Toast.makeText(this, "Ошибка соединения", Toast.LENGTH_SHORT).show();
                });
    } //Регистрация
}