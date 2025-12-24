package com.example.ponyvilleschool;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public interface SupabaseService
{
    String APIkey = "APIkey";
    String URL = "https://mybase.supabase.co/";

    //Запрос на авторизацию
    @Headers({
            "apikey: " + APIkey,
            "Authorization: Bearer " + APIkey,
            "Content-Type: application/json"
    })
    @POST("rest/v1/rpc/auth_user")
    Call<Map<String, Object>> authUser(
            @Body Map<String, Object> params);

    //Запрос на регистрацию
    @Headers({
            "apikey: " + APIkey,
            "Authorization: Bearer " + APIkey,
            "Content-Type: application/json"
    })
    @POST("rest/v1/rpc/register_user")
    Call<Void> registerUser(
            @Body Map<String, Object> params);

    //Запрос на данные курса
    @Headers({
            "apikey: " + APIkey,
            "Authorization: Bearer " + APIkey,
            "Content-Type: application/json"
    })
    @POST("rest/v1/rpc/get_course_data")
    Call<List<TaskData>> getCourseData(
            @Body Map<String, Object> params);

    //Запрос на прогресс пользователя
    @Headers({
            "apikey: " + APIkey,
            "Authorization: Bearer " + APIkey,
            "Content-Type: application/json"
    })
    @POST("rest/v1/rpc/get_user_progress")
    Call<List<Map<String, Object>>> getUserProgress(
            @Body Map<String, Object> params);

    //Запрос на прогресс пользователя
    @Headers({
            "apikey: " + APIkey,
            "Authorization: Bearer " + APIkey,
            "Content-Type: application/json"
    })
    @POST("rest/v1/rpc/submit_result")
    Call<Void> submitResult(
            @Body Map<String, Object> params);

    //Запрос на получение вопросов
    @Headers({
            "apikey: " + APIkey,
            "Authorization: Bearer " + APIkey,
            "Content-Type: application/json"
    })
    @POST("rest/v1/rpc/get_practice_data")
    Call<List<QuestionData>> getPracticeData(
            @Body Map<String, Object> params
    );
}
