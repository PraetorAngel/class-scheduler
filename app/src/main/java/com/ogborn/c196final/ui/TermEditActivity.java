package com.ogborn.c196final.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ogborn.c196final.R;
import com.ogborn.c196final.database.AppDatabase;
import com.ogborn.c196final.helper.DateUtil;
import com.ogborn.c196final.model.Term;

import java.util.Objects;

public class TermEditActivity extends AppCompatActivity {

    public static final String EXTRA_TERM_ID = "termId";
    private EditText titleField, startField, endField;
    private AppDatabase database;
    private Term existing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        database = AppDatabase.getDatabase(getApplicationContext());
        titleField = findViewById(R.id.edit_term_title);
        startField = findViewById(R.id.edit_term_start);
        endField = findViewById(R.id.edit_term_end);
        startField.setOnClickListener(v -> DateUtil.showDatePicker(this, startField));
        endField.setOnClickListener(v -> DateUtil.showDatePicker(this, endField));
        FloatingActionButton saveButton = findViewById(R.id.btn_save_term);

        int termId = getIntent().getIntExtra(EXTRA_TERM_ID, -1);
        if (termId != -1) {
            existing = database.termDao().getTermById(termId);
            if (existing != null) {
                titleField.setText(existing.getTitle());
                startField.setText(existing.getStartDate());
                endField.setText(existing.getEndDate());
                setTitle(getString(R.string.edit, existing.getTitle()));
            } else {
                finish();
            }
        } else {
            setTitle(R.string.action_add_term);
        }

        saveButton.setOnClickListener(v -> saveTerm());
    }

    private void saveTerm() {
        String title = titleField.getText().toString().trim();
        String start = startField.getText().toString().trim();
        String end = endField.getText().toString().trim();

        if (title.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, R.string.toast_all_required, Toast.LENGTH_SHORT).show();
            return;
        }

        if (DateUtil.parseDate(start).after(DateUtil.parseDate(end))) {
            Toast.makeText(this, R.string.toast_date_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        if (existing == null) {
            database.termDao().insert(new Term(title, start, end));
            Toast.makeText(this, getString(R.string.added, getString(R.string.label_term)), Toast.LENGTH_SHORT).show();
        } else {
            existing.setTitle(title);
            existing.setStartDate(start);
            existing.setEndDate(end);
            database.termDao().update(existing);
            Toast.makeText(this, getString(R.string.updated, getString(R.string.label_term)), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}