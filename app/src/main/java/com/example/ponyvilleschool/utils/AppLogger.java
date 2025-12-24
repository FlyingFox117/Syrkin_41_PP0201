package com.example.ponyvilleschool.utils;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//Класс для написания логов
public class AppLogger {
    private static final String LOG_FILE_NAME = "school_logs.txt";

    //Получаем файл логов
    private static File getLogFile(Context context) {
        File dir = context.getExternalFilesDir(null);
        return new File(dir, LOG_FILE_NAME);
    }
    //Универсальная запись строки
    private static void write(Context context, String text) {
        try (FileWriter writer = new FileWriter(getLogFile(context), true)) {
            writer.append(text).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String time() {
        return new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());
    }
    //Лог начала запроса
    public static void logStart(
            Context context,
            String callerClass,
            String method,
            Object params
    ) {
        write(context,
                "\n[" + time() + "] START\n" +
                        "Класс: " + callerClass + "\n" +
                        "Метод: " + method + "\n" +
                        "Данные: " + params
        );
    }
    //Лог успешного выполнения
    public static void logSuccess(
            Context context,
            String callerClass,
            String method,
            Object response
    ) {
        write(context,
                "[" + time() + "] RESULT\n" +
                        "Класс: " + callerClass + "\n" +
                        "Метод: " + method + "\n" +
                        "Статус: OK\n" +
                        "Ответ: " + response
        );
    }
    //Лог ошибки
    public static void logFailure(
            Context context,
            String callerClass,
            String method,
            Throwable error
    ) {
        write(context,
                "[" + time() + "] RESULT\n" +
                        "Класс: " + callerClass + "\n" +
                        "Метод: " + method + "\n" +
                        "Статус: FAILURE\n" +
                        "Ошибка: " + error.getMessage()
        );
    }
}
