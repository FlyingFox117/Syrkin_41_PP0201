package com.example.ponyvilleschool;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.ponyvilleschool.models.CourseProgress;
import com.example.ponyvilleschool.utils.AppState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ExampleAndroidTest {

    public ExampleAndroidTest()
    {

    }
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry
                .getInstrumentation()
                .getTargetContext();
    }

    @Test
    public void testAuthAndLoadCourseTasks() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] testResult = {false};

        // 1️⃣ Авторизация
        Map<String, Object> authParams = new HashMap<>();
        authParams.put("login", "test_user@mail.com");
        authParams.put("password", "test_password");

        Supabase.user_auth(
                context,
                authParams,
                () -> {
                    try {
                        assertNotNull(
                                "Пользователь не авторизовался",
                                AppState.getInstance().currentUser
                        );

                        // 2️⃣ MOCK-курсы
                        List<CourseProgress> list = new ArrayList<>();
                        list.add(new CourseProgress(1, "Уроки Честности", "", 10, 0, R.drawable.applejack_course));
                        list.add(new CourseProgress(2, "Основы Щедрости", "", 10, 0, R.drawable.rarity_course));
                        list.add(new CourseProgress(3, "Устав Верности", "", 10, 0, R.drawable.rainbow_course));
                        list.add(new CourseProgress(4, "Курс Доброты", "", 10, 0, R.drawable.fluttershy_course));
                        list.add(new CourseProgress(5, "Праздник Радости", "", 10, 0, R.drawable.pinkie_course));
                        list.add(new CourseProgress(6, "Книга Гармония", "", 10, 0, R.drawable.twilight_course));

                        AppState.getInstance().coursesProgress = list;

                        // 3️⃣ Загружаем прогресс
                        Map<String, Object> progressParams = new HashMap<>();
                        progressParams.put(
                                "p_user_id",
                                AppState.getInstance().currentUser.id
                        );

                        Supabase.loadUserProgress(
                                context,
                                progressParams,
                                (response) -> {
                                    try {
                                        assertFalse(
                                                "Прогресс курсов пуст",
                                                AppState.getInstance()
                                                        .getCoursesProgress()
                                                        .isEmpty()
                                        );

                                        CourseProgress progress =
                                                AppState.getInstance()
                                                        .getCoursesProgress()
                                                        .get(0);

                                        assertTrue(
                                                "Прогресс отрицательный",
                                                progress.progress >= 0
                                        );

                                        // 4️⃣ Загружаем задания курса
                                        Map<String, Object> courseParams = new HashMap<>();
                                        courseParams.put("course_id", progress.course_id);
                                        courseParams.put(
                                                "p_user_id",
                                                AppState.getInstance().currentUser.id
                                        );

                                        Supabase.getCourseData(
                                                context,
                                                courseParams,
                                                (tasks) -> {
                                                    try {
                                                        assertFalse(
                                                                "Задания не загружены",
                                                                tasks.isEmpty()
                                                        );

                                                        TaskData task = tasks.get(0);

                                                        assertEquals(
                                                                "Задание не принадлежит курсу",
                                                                progress.course_id,
                                                                1
                                                        );

                                                        // ✅ УСПЕХ
                                                        testResult[0] = true;
                                                    } catch (AssertionError e) {
                                                        fail(e.getMessage());
                                                    } finally {
                                                        latch.countDown();
                                                    }
                                                },
                                                () -> {
                                                    fail("Ошибка загрузки заданий курса");
                                                    latch.countDown();
                                                }
                                        );

                                    } catch (AssertionError e) {
                                        fail(e.getMessage());
                                        latch.countDown();
                                    }
                                },
                                () -> {
                                    fail("Ошибка загрузки прогресса пользователя");
                                    latch.countDown();
                                }
                        );

                    } catch (AssertionError e) {
                        fail(e.getMessage());
                        latch.countDown();
                    }
                },
                () -> {
                    fail("Авторизация не удалась");
                    latch.countDown();
                }
        );

        // ⏳ ждём async
        latch.await(15, TimeUnit.SECONDS);

        assertTrue(
                "Интеграционный тест не завершился успешно",
                testResult[0]
        );
    }
}