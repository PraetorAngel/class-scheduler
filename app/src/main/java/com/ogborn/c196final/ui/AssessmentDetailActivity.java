package com.ogborn.c196final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ogborn.c196final.R;
import com.ogborn.c196final.database.AppDatabase;
import com.ogborn.c196final.model.Assessment;

import java.util.Objects;

public class AssessmentDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ASSESSMENT_ID = "assessmentId";
    private static final String PREFS_NAME = "alerts";
    private static final String ALERT_PREFIX = "alert";
    private static final String START_SUFFIX = "_start";
    private static final String END_SUFFIX = "_end";
    private TextView title, type, start, end;
    private AppDatabase database;
    private Assessment assessment;
    private int assessmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        database = AppDatabase.getDatabase(getApplicationContext());

        assessmentId = getIntent().getIntExtra(EXTRA_ASSESSMENT_ID, -1);
        if (assessmentId == -1) {
            finish();
            return;
        }
        assessment = database.assessmentDao().getAssessmentById(assessmentId);
        if (assessment == null) {
            finish();
            return;
        }

        title = findViewById(R.id.assessment_detail_title);
        type = findViewById(R.id.assessment_detail_type);
        start = findViewById(R.id.assessment_detail_start);
        end = findViewById(R.id.assessment_detail_end);

        reloadAssessmentDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, AssessmentEditActivity.class);
            intent.putExtra(EXTRA_ASSESSMENT_ID, assessmentId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadAssessmentDetails();
    }

    private void reloadAssessmentDetails() {
        assessment = database.assessmentDao().getAssessmentById(assessmentId);
        if (assessment == null) {
            finish();
            return;
        }

        setTitle(getString(R.string.details, assessment.getTitle()));
        title.setText(assessment.getTitle());
        type.setText(assessment.getType());
        start.setText(assessment.getStartDate());
        end.setText(assessment.getEndDate());

        boolean startAlert = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getBoolean(ALERT_PREFIX + assessment.getId() + START_SUFFIX, false);

        boolean endAlert = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getBoolean(ALERT_PREFIX + assessment.getId() + END_SUFFIX, false);
        CheckBox checkboxStart = findViewById(R.id.checkbox_detail_alert_start);
        CheckBox checkboxEnd = findViewById(R.id.checkbox_detail_alert_end);
        checkboxStart.setChecked(startAlert);
        checkboxEnd.setChecked(endAlert);
    }
}