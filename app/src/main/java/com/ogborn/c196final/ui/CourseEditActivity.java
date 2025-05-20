package com.ogborn.c196final.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ogborn.c196final.R;
import com.ogborn.c196final.database.AppDatabase;
import com.ogborn.c196final.helper.DateUtil;
import com.ogborn.c196final.helper.NotificationUtil;
import com.ogborn.c196final.model.Course;

import java.util.Objects;

public class CourseEditActivity extends AppCompatActivity {

    public static final String EXTRA_TERM_ID = "termId";
    public static final String EXTRA_COURSE_ID = "courseId";
    private EditText titleInput, startInput, endInput, instructorInput, phoneInput, emailInput, notesInput;
    private AppDatabase database;
    private int termId;
    private int courseId;
    private Spinner statusSpinner;
    private Course existing;
    private CheckBox checkboxStart, checkboxEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        titleInput = findViewById(R.id.edit_course_title);
        startInput = findViewById(R.id.edit_course_start);
        endInput = findViewById(R.id.edit_course_end);
        startInput.setOnClickListener(v -> DateUtil.showDatePicker(this, startInput));
        endInput.setOnClickListener(v -> DateUtil.showDatePicker(this, endInput));
        instructorInput = findViewById(R.id.edit_course_instructor);
        phoneInput = findViewById(R.id.edit_course_phone);
        emailInput = findViewById(R.id.edit_course_email);
        notesInput = findViewById(R.id.edit_course_notes);
        checkboxStart = findViewById(R.id.checkbox_alert_start);
        checkboxEnd = findViewById(R.id.checkbox_alert_end);
        statusSpinner = findViewById(R.id.spinner_course_status);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.course_status_options,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        database = AppDatabase.getDatabase(getApplicationContext());

        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);
        termId = getIntent().getIntExtra(EXTRA_TERM_ID, -1);

        if (courseId != -1) {
            existing = database.courseDao().getCourseById(courseId);
            if (existing == null) {
                Toast.makeText(this, getString(R.string.not_found, getString(R.string.label_course)), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            termId = existing.getTermId();
            setTitle(getString(R.string.edit, existing.getTitle()));
            populateFields(existing);
        } else if (termId != -1) {
            setTitle(getString(R.string.label_add_course, database.termDao().getTermById(termId).getTitle()));
        }

        FloatingActionButton saveBtn = findViewById(R.id.btn_save_course);
        saveBtn.setOnClickListener(v -> saveCourse());
    }

    private void populateFields(Course currentCourse) {
        titleInput.setText(currentCourse.getTitle());
        startInput.setText(currentCourse.getStartDate());
        endInput.setText(currentCourse.getEndDate());
        instructorInput.setText(currentCourse.getInstructorName());
        phoneInput.setText(currentCourse.getInstructorPhone());
        emailInput.setText(currentCourse.getInstructorEmail());
        notesInput.setText(currentCourse.getNotes());
        String[] statuses = getResources().getStringArray(R.array.course_status_options);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equalsIgnoreCase(currentCourse.getStatus())) {
                statusSpinner.setSelection(i);
                break;
            }
        }

        NotificationUtil.bindAlertCheckboxes(this, currentCourse.getId(), checkboxStart, checkboxEnd);
    }

    private void saveCourse() {
        String title = titleInput.getText().toString().trim();
        String start = startInput.getText().toString().trim();
        String end = endInput.getText().toString().trim();
        String instructor = instructorInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString().trim();
        String notes = notesInput.getText().toString().trim();

        if (title.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, R.string.toast_title_start_end_required, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, R.string.toast_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.PHONE.matcher(phone).matches()){
            Toast.makeText(this, R.string.toast_invalid_phone, Toast.LENGTH_SHORT).show();
            return;
        }
        if (DateUtil.parseDate(start).after(DateUtil.parseDate(end))){
            Toast.makeText(this, R.string.toast_date_mismatch,Toast.LENGTH_SHORT).show();
            return;
        }

        Course course = new Course(termId, title, start, end, status, instructor, phone, email, notes);

        if (existing != null) {
            course.setId(courseId);
            database.courseDao().update(course);
            Toast.makeText(this, getString(R.string.updated, getString(R.string.label_course)), Toast.LENGTH_SHORT).show();
        } else {
            database.courseDao().insert(course);
            Toast.makeText(this, getString(R.string.added, getString(R.string.label_course)), Toast.LENGTH_SHORT).show();
        }

        NotificationUtil.applyAlertPreferences(
                this,
                course.getId(),
                title,
                start,
                end,
                checkboxStart.isChecked(),
                checkboxEnd.isChecked(),
                NotificationUtil.AlertDomain.COURSE
        );

        finish();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}