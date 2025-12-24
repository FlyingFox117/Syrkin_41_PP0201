package com.example.ponyvilleschool.utils;

import com.example.ponyvilleschool.models.CourseProgress;
import com.example.ponyvilleschool.TaskData;
import com.example.ponyvilleschool.UserData;

import java.util.ArrayList;
import java.util.List;

//Статичный класс для хранения данных в одном месте. Работает как перекрёсток.
public class AppState {
    public static AppState instance;

    public static AppState getInstance() {
        if (instance == null) {
            instance = new AppState();
        }
        return instance;
    }
    public UserData currentUser;
    public List<CourseProgress> coursesProgress = new ArrayList<>();
    public List<TaskData> tasksData = new ArrayList<>();
    public int selectedCourse;
    public TaskData getTaskData(int index) {
        return tasksData.get(index);
    }
    public UserData getCurrentUser() {
        return currentUser;
    }
    public void setCurrentUser(UserData user) {
        this.currentUser = user;
    }
    public List<CourseProgress> getCoursesProgress() {
        return coursesProgress;
    }
    public void setCoursesProgress(List<CourseProgress> progress) {
        this.coursesProgress = progress;
    }
    public int getCurrentCourse()
    {
        return selectedCourse;
    }
    public void setCurrentCourse(int selected)
    {
        this.selectedCourse = selected;
    }
    public void clearTasks() {
        if (tasksData != null){
            tasksData.clear();
        }
    }
}
