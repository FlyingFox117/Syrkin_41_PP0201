package com.example.ponyvilleschool;

import static com.example.ponyvilleschool.utils.AppLogger.logFailure;
import static com.example.ponyvilleschool.utils.AppLogger.logStart;
import static com.example.ponyvilleschool.utils.AppLogger.logSuccess;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.ponyvilleschool.utils.AppState;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Класс для написания запросов к облачной базе данных Supabase
public class Supabase
{
    //Авторизация
    public static void user_auth( //Авторизация
        Context context,
        Map<String, Object> params,
        Runnable onSuccess,
        Runnable onFailure
    )
    {
        logStart(context, context.getClass().getSimpleName(), "auth_user", params);

        SupabaseService service = SupabaseClient.getInstance();
        service.authUser(params).enqueue(new Callback<>() {

            @Override
            public void onResponse(Call<Map<String, Object>> call,
                                   Response<Map<String, Object>> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        //Тело ответа
                        Map<String, Object> json = response.body();
                        // если id есть — авторизация успешна
                        if (json.get("id") != null) {
                            sendLogs(context, context.getClass().getSimpleName(), "auth_user", params);
                            UserData user = new UserData();
                            user.id = ((Double) json.get("id")).intValue();
                            user.name = (String) json.get("name");
                            user.role = (String) json.get("role");
                            user.last_login = (String) json.get("last_online");

                            // новый пользователь, available может быть null
                            if (json.get("available") == null) {
                                user.available = true;
                            } else {
                                user.available = (Boolean)
                                        json.get("available");
                            }
                            AppState.getInstance().setCurrentUser(user); //Создание нового пользователя
                            onSuccess.run();
                        } else {
                            Log.e("SUPABASE", "Ошибка авторизации");
                            Toast.makeText(context, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                            onFailure.run();
                        }
                    }
                    else
                        onFailure.run();
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                t.printStackTrace();
                sendLogs(context, context.getClass().getSimpleName(), "auth_user", t);
                Toast.makeText(context, "Ошибка соединения", Toast.LENGTH_SHORT).show();
                onFailure.run();
            }
        });
    }

    public static void user_register( //Регистрация
            Context context,
            Map<String, Object> params,
            Runnable onSuccess,
            Runnable onFailure
    )
    {
        logStart(context, context.getClass().getSimpleName(), "user_register", params);

        SupabaseService service = SupabaseClient.getInstance();
        service.registerUser(params).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    sendLogs(context, context.getClass().getSimpleName(), "user_register", params);
                    onSuccess.run();
                } else {
                    Toast.makeText(context, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                sendLogs(context, context.getClass().getSimpleName(), "user_register", t);
                onFailure.run();
            }
        });
    }

    public static void loadUserProgress( //Загрузка прогресса пользователя
            Context context,
            Map<String, Object> params,
            Consumer<List<Map<String, Object>>> onSuccess,
            Runnable onFailure
    )
    {
        logStart(context, context.getClass().getSimpleName(), "get_course_progress", params);

        SupabaseService service = SupabaseClient.getInstance();
        service.getUserProgress(params).enqueue(new Callback<List<Map<String, Object>>>() {

            @Override
            public void onResponse(
                    Call<List<Map<String, Object>>> call,
                    Response<List<Map<String, Object>>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    sendLogs(context, context.getClass().getSimpleName(), "get_course_progress", params);
                    onSuccess.accept(response.body());
                } else {
                    Log.e("SUPABASE", "Ошибка загрузки прогресса: " + response.raw());
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                t.printStackTrace();
                sendLogs(context, context.getClass().getSimpleName(), "get_course_progress", t);
                onFailure.run();
            }
        });
    }

    public static void getCourseData( //Загрузка данных курса
                                        Context context,
                                        Map<String, Object> params,
                                        Consumer<List<TaskData>> onSuccess,
                                        Runnable onFailure
    )
    {
        logStart(context, context.getClass().getSimpleName(), "get_course_data", params);

        SupabaseService service = SupabaseClient.getInstance();
        service.getCourseData(params).enqueue(new Callback<>() {

            @Override
            public void onResponse(
                    Call<List<TaskData>> call,
                    Response<List<TaskData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sendLogs(context, context.getClass().getSimpleName(), "get_course_data", params);
                    onSuccess.accept(response.body());
                } else {
                    Log.e("SUPABASE", "Ошибка загрузки прогресса: " + response.raw());
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<List<TaskData>> call, Throwable t) {
                t.printStackTrace();
                sendLogs(context, context.getClass().getSimpleName(), "get_course_data", t);
                onFailure.run();
            }
        });
    }

    public static void getPracticeData( //Получение заданий
            Context context,
            Map<String, Object> params,
            Consumer<List<QuestionData>> onSuccess,
            Runnable onFailure
    )
    {
        logStart(context, context.getClass().getSimpleName(), "get_practice_data", params);

        SupabaseService service = SupabaseClient.getInstance();
        service.getPracticeData(params).enqueue(new Callback<List<QuestionData>>() {

            @Override
            public void onResponse(
                    Call<List<QuestionData>> call,
                    Response<List<QuestionData>> response)
            {
                if (response.isSuccessful() && response.body() != null) {
                    sendLogs(context, context.getClass().getSimpleName(), "get_practice_data", params);
                    onSuccess.accept(response.body());
                } else {
                    Log.e("SUPABASE", "Ошибка загрузки заданий: " + response.raw());
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<List<QuestionData>> call, Throwable t) {
                t.printStackTrace();
                sendLogs(context, context.getClass().getSimpleName(), "get_practice_data", t);
                onFailure.run();
            }
        });
    }

    public static void submitResult( //Отправка результата
            Context context,
            Map<String, Object> params,
            Runnable onSuccess,
            Runnable onFailure
    )
    {
        logStart(context, context.getClass().getSimpleName(), "submit_result", params);

        SupabaseService service = SupabaseClient.getInstance();
        service.submitResult(params).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    sendLogs(context, context.getClass().getSimpleName(), "submit_result", params);
                    onSuccess.run();
                } else {
                    Log.e("SUPABASE", "Ошибка сохранения: " + response.raw());
                    onFailure.run();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Ошибка соединения", Toast.LENGTH_SHORT).show();
                sendLogs(context, context.getClass().getSimpleName(), "submit_result", t);
                onFailure.run();
            }
        });
    }

    private static void sendLogs(Context context, String caller, String method, Object response)
    {
        logSuccess(context, caller, method, response);
    }
    private static void sendLogs(Context context, String caller, String method, Throwable error)
    {
        logFailure(context, caller, method, error);
    }
}