package com.example.ponyvilleschool;

import com.google.type.DateTime;

//MOCK-класс для хранения пользователя, авторизованного в системе
public class UserData {
    public int id; //Код пользователя. Нужен для получения его прогресса из таблицы progress
    public String name; //Имя пользователя
    public String role; //Роль пользователя
    public String last_login; //Последняя активность
    public boolean available; //Доступ к выполнению заданий

    public UserData(
            int id,
            String name,
            String role,
            String last_login,
            boolean available
    )
    {
        this.id = id;
        this.name = name;
        this.role = role;
        this.last_login = last_login;
        this.available = available;
    }

    public UserData()
    {

    }
}
