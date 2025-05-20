package com.ogborn.c196final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ogborn.c196final.R;
import com.ogborn.c196final.database.AppDatabase;
import com.ogborn.c196final.helper.NotificationUtil;
import com.ogborn.c196final.model.Assessment;
import com.ogborn.c196final.model.Course;
import com.ogborn.c196final.model.Term;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationUtil.requestNotificationPermissionOnce(this);
        setContentView(R.layout.activity_main);

        ImageView appIcon = findViewById(R.id.app_icon);

        initializeDummyData(); //will be removed for production code
        appIcon.setOnClickListener(v ->
            appIcon.animate()
                    .scaleX(4f)
                    .scaleY(4f)
                    .alpha(0f)
                    .translationY(-200f)
                    .setDuration(400)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .withEndAction(() -> {
                        Intent intent = new Intent(MainActivity.this, TermListActivity.class);
                        startActivity(intent);
                    })
                    .start());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView appIcon = findViewById(R.id.app_icon);

        appIcon.setScaleX(1f);
        appIcon.setScaleY(1f);
        appIcon.setAlpha(1f);
        appIcon.setTranslationY(0f);
    }

    private void initializeDummyData() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        if (db.termDao().getAllTerms().isEmpty()) {
            long termId1 = db.termDao().insert(new Term("Fall 2025", "2025-08-01", "2025-12-15"));
            long courseId1 = db.courseDao().insert(new Course((int) termId1, "Intro to Android", "2025-08-01", "2025-10-01", "In Progress",
                    "Jane Smith", "555-1234", "jane@example.com", "Android fundamentals"));
            long courseId2 = db.courseDao().insert(new Course((int) termId1, "Databases", "2025-08-01", "2025-12-01", "In Progress",
                    "Mike Green", "555-5678", "mike@example.com", "SQL and Room"));
            db.assessmentDao().insert(new Assessment((int) courseId1, "Android Midterm", "Objective", "2025-09-01", "2025-09-10"));
            db.assessmentDao().insert(new Assessment((int) courseId1, "Android Final", "Performance", "2025-09-15", "2025-09-30"));
            db.assessmentDao().insert(new Assessment((int) courseId2, "DB Quiz", "Objective", "2025-09-05", "2025-09-12"));
            db.assessmentDao().insert(new Assessment((int) courseId2, "DB Project", "Performance", "2025-10-01", "2025-11-01"));

            long termId2 = db.termDao().insert(new Term("Spring 2026", "2026-01-10", "2026-05-20"));
            long courseId3 = db.courseDao().insert(new Course((int) termId2, "Java Basics", "2026-01-10", "2026-03-01", "Planned",
                    "Alice Wong", "555-9999", "alice@example.com", "Intro to Java"));
            long courseId4 = db.courseDao().insert(new Course((int) termId2, "Project Management", "2026-02-01", "2026-05-15", "Planned",
                    "Tom Hill", "555-1111", "tom@example.com", "Agile workflows"));
            db.assessmentDao().insert(new Assessment((int) courseId3, "Java Test 1", "Objective", "2026-01-20", "2026-01-25"));
            db.assessmentDao().insert(new Assessment((int) courseId3, "Java Final", "Performance", "2026-02-10", "2026-03-01"));
            db.assessmentDao().insert(new Assessment((int) courseId4, "PM Report", "Objective", "2026-03-15", "2026-03-25"));
            db.assessmentDao().insert(new Assessment((int) courseId4, "PM Presentation", "Performance", "2026-04-10", "2026-05-10"));

            long termId3 = db.termDao().insert(new Term("Summer 2026", "2026-06-01", "2026-08-15"));
            long courseId5 = db.courseDao().insert(new Course((int) termId3, "UI/UX Design", "2026-06-01", "2026-07-15", "In Progress",
                    "Rachel Lee", "555-4444", "rachel@example.com", "Human-centered design"));
            long courseId6 = db.courseDao().insert(new Course((int) termId3, "Web Dev", "2026-06-10", "2026-08-10", "Planned",
                    "Eric Johnson", "555-8888", "eric@example.com", "HTML/CSS/JS"));
            db.assessmentDao().insert(new Assessment((int) courseId5, "UX Critique", "Objective", "2026-06-15", "2026-06-20"));
            db.assessmentDao().insert(new Assessment((int) courseId5, "Design Project", "Performance", "2026-07-01", "2026-07-15"));
            db.assessmentDao().insert(new Assessment((int) courseId6, "JS Quiz", "Objective", "2026-07-01", "2026-07-10"));
            db.assessmentDao().insert(new Assessment((int) courseId6, "Capstone Website", "Performance", "2026-07-15", "2026-08-10"));

            long termId4 = db.termDao().insert(new Term("Fall 2026", "2026-09-01", "2026-12-20"));
            long courseId7 = db.courseDao().insert(new Course((int) termId4, "Kotlin Dev", "2026-09-01", "2026-10-30", "In Progress",
                    "Lena Ray", "555-7777", "lena@example.com", "Kotlin for Android"));
            long courseId8 = db.courseDao().insert(new Course((int) termId4, "Cybersecurity", "2026-09-15", "2026-12-15", "Planned",
                    "Dan Miller", "555-2222", "dan@example.com", "Security basics"));
            db.assessmentDao().insert(new Assessment((int) courseId7, "Kotlin Midterm", "Objective", "2026-09-20", "2026-09-30"));
            db.assessmentDao().insert(new Assessment((int) courseId7, "Kotlin Final", "Performance", "2026-10-10", "2026-10-30"));
            db.assessmentDao().insert(new Assessment((int) courseId8, "Cyber Test", "Objective", "2026-10-15", "2026-10-25"));
            db.assessmentDao().insert(new Assessment((int) courseId8, "Security Report", "Performance", "2026-11-01", "2026-12-01"));
        }
    }
}