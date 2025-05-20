package com.ogborn.c196final.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ogborn.c196final.R;
import com.ogborn.c196final.adapter.CourseAdapter;
import com.ogborn.c196final.database.AppDatabase;
import com.ogborn.c196final.helper.SwipeToDeleteCallback;
import com.ogborn.c196final.model.Course;
import com.ogborn.c196final.model.Term;

import java.util.List;
import java.util.Objects;

public class TermDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TERM_ID = "termId";
    public static final String EXTRA_COURSE_ID = "courseId";
    private TextView title, start, end;
    private RecyclerView courseRecycler;
    private CourseAdapter adapter;
    private AppDatabase database;
    private int termId;
    private List<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        database = AppDatabase.getDatabase(getApplicationContext());

        termId = getIntent().getIntExtra(EXTRA_TERM_ID, -1);
        if (termId == -1) finish();

        title = findViewById(R.id.term_detail_title);
        start = findViewById(R.id.term_detail_start);
        end = findViewById(R.id.term_detail_end);
        courseRecycler = findViewById(R.id.recycler_view_term_courses);
        courseRecycler.setLayoutManager(new LinearLayoutManager(this));
        reloadTermDetails();

        new ItemTouchHelper(new SwipeToDeleteCallback<>(new SwipeToDeleteCallback.DeleteHandler<Course>() {
            @Override
            public void onDelete(int position, Course course) {
                new androidx.appcompat.app.AlertDialog.Builder(TermDetailActivity.this)
                    .setTitle(getString(R.string.label_delete, getString(R.string.label_course)))
                    .setMessage(R.string.message_delete_course)
                    .setPositiveButton(R.string.action_yes, (dialog, which) -> {
                        database.assessmentDao().deleteAllForCourse(course.getId());
                        database.courseDao().delete(course);
                        courses.remove(position);
                        adapter.notifyItemRemoved(position);
                    })
                    .setNegativeButton(R.string.action_cancel, (dialog, which) -> adapter.notifyItemChanged(position))
                    .show();
            }

            @Override
            public Course getItem(int position) {
                return courses.get(position);
            }
        })).attachToRecyclerView(courseRecycler);

        findViewById(R.id.btn_add_course).setOnClickListener(v -> {
            Intent intent = new Intent(this, CourseEditActivity.class);
            intent.putExtra(EXTRA_TERM_ID, termId);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, TermEditActivity.class);
            intent.putExtra(EXTRA_TERM_ID, termId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadTermDetails();
    }

    private void reloadTermDetails() {
        Term term = database.termDao().getTermById(termId);
        if (term == null) {
            finish();
            return;
        }

        setTitle(getString(R.string.details, term.getTitle()));
        title.setText(term.getTitle());
        start.setText(term.getStartDate());
        end.setText(term.getEndDate());

        courses = database.courseDao().getCoursesForTerm(termId);
        adapter = new CourseAdapter(courses);
        courseRecycler.setAdapter(adapter);

        adapter.setOnItemClickListener(course -> {
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra(EXTRA_COURSE_ID, course.getId());
            startActivity(intent);
        });
    }
}