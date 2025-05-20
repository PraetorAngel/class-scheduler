package com.ogborn.c196final.ui;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ogborn.c196final.R;
import com.ogborn.c196final.database.AppDatabase;
import com.ogborn.c196final.helper.DateUtil;
import com.ogborn.c196final.helper.NotificationUtil;
import com.ogborn.c196final.model.Assessment;

import java.util.Objects;

public class AssessmentEditActivity extends AppCompatActivity {

    public static final String EXTRA_ASSESSMENT_ID = "assessmentId";
    public static final String EXTRA_COURSE_ID = "courseId";
    private EditText titleInput, typeInput, startInput, endInput;
    private AppDatabase database;
    private int courseId;
    private Assessment existing;
    private CheckBox checkboxStart, checkboxEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_edit);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        database = AppDatabase.getDatabase(getApplicationContext());
        titleInput = findViewById(R.id.edit_assessment_title);
        typeInput = findViewById(R.id.edit_assessment_type);
        checkboxStart = findViewById(R.id.checkbox_alert_start);
        checkboxEnd = findViewById(R.id.checkbox_alert_end);
        startInput = findViewById(R.id.edit_assessment_start);
        endInput = findViewById(R.id.edit_assessment_end);
        startInput.setOnClickListener(v -> DateUtil.showDatePicker(this, startInput));
        endInput.setOnClickListener(v -> DateUtil.showDatePicker(this, endInput));

        int assessmentId = getIntent().getIntExtra(EXTRA_ASSESSMENT_ID, -1);
        courseId = getIntent().getIntExtra(EXTRA_COURSE_ID, -1);

        if (assessmentId != -1) {
            existing = database.assessmentDao().getAssessmentById(assessmentId);
            if (existing != null) {
                courseId = existing.getCourseId();
                populate(existing);
                setTitle(getString(R.string.edit, existing.getTitle()));
            } else {
                Toast.makeText(this, getString(R.string.not_found, getString(R.string.label_assessment)), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (courseId != -1) {
            setTitle(getString(R.string.action_add_assessment));
        } else {
            Toast.makeText(this, R.string.toast_missing_id, Toast.LENGTH_SHORT).show();
            finish();
        }

        FloatingActionButton save = findViewById(R.id.btn_save_assessment);
        save.setOnClickListener(v -> saveAssessment());
    }

    private void populate(Assessment currentAssessment) {
        titleInput.setText(currentAssessment.getTitle());
        typeInput.setText(currentAssessment.getType());
        startInput.setText(currentAssessment.getStartDate());
        endInput.setText(currentAssessment.getEndDate());

        NotificationUtil.bindAlertCheckboxes(this, currentAssessment.getId(), checkboxStart, checkboxEnd);
    }

    private void saveAssessment() {
        String title = titleInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();
        String start = startInput.getText().toString().trim();
        String end = endInput.getText().toString().trim();

        if (title.isEmpty() || type.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, R.string.toast_all_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (DateUtil.parseDate(start).after(DateUtil.parseDate(end))){
            Toast.makeText(this, R.string.toast_date_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        Assessment assessment = new Assessment(courseId, title, type, start, end);

        if (existing != null) {
            // so this is a little clunky because it fires whether changes are made or not, but i cba to refactor rn
            assessment.setId(existing.getId());
            database.assessmentDao().update(assessment);
            Toast.makeText(this, getString(R.string.updated, getString(R.string.label_assessment)), Toast.LENGTH_SHORT).show();
        } else {
            database.assessmentDao().insert(assessment);
            Toast.makeText(this, getString(R.string.added, getString(R.string.label_assessment)), Toast.LENGTH_SHORT).show();
        }

        NotificationUtil.applyAlertPreferences(
                this,
                assessment.getId(),
                title,
                start,
                end,
                checkboxStart.isChecked(),
                checkboxEnd.isChecked(),
                NotificationUtil.AlertDomain.ASSESSMENT
        );

        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}