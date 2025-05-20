package com.ogborn.c196final.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ogborn.c196final.R;
import com.ogborn.c196final.adapter.TermAdapter;
import com.ogborn.c196final.database.AppDatabase;
import com.ogborn.c196final.helper.SwipeToDeleteCallback;
import com.ogborn.c196final.model.Course;
import com.ogborn.c196final.model.Term;

import java.util.List;
import java.util.Objects;

public class TermListActivity extends AppCompatActivity {

    public static final String EXTRA_TERM_ID = "termId";
    private RecyclerView termRecycler;
    private TermAdapter termAdapter;
    private List<Term> terms;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_list);
        setTitle(R.string.terms);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        termRecycler = findViewById(R.id.recycler_view_terms);
        termRecycler.setLayoutManager(new LinearLayoutManager(this));

        database = AppDatabase.getDatabase(getApplicationContext());
        terms = database.termDao().getAllTerms();
        termAdapter = new TermAdapter(terms);
        termRecycler.setAdapter(termAdapter);

        FloatingActionButton addTermButton = findViewById(R.id.btn_add_term);
        addTermButton.setOnClickListener(v -> {
            Intent intent = new Intent(TermListActivity.this, TermEditActivity.class);
            startActivity(intent);
        });

        termAdapter.setOnItemClickListener(term -> {
            Intent intent = new Intent(TermListActivity.this, TermDetailActivity.class);
            intent.putExtra(EXTRA_TERM_ID, term.getId());
            startActivity(intent);
        });

        new ItemTouchHelper(new SwipeToDeleteCallback<>(new SwipeToDeleteCallback.DeleteHandler<Term>() {
            @Override
            public void onDelete(int position, Term term) {
                List<Course> attachedCourses = database.courseDao().getCoursesForTerm(term.getId());

                if (!attachedCourses.isEmpty()) {
                    new androidx.appcompat.app.AlertDialog.Builder(TermListActivity.this)
                            .setTitle(R.string.message_cannot_delete_term)
                            .setMessage(R.string.message_delete_term_fail)
                            .setPositiveButton(R.string.action_acknowledge, (d, w) -> termAdapter.notifyItemChanged(position))
                            .show();
                    return;
                }

                new androidx.appcompat.app.AlertDialog.Builder(TermListActivity.this)
                        .setTitle(getString(R.string.label_delete, getString(R.string.label_term)))
                        .setMessage(getString(R.string.message_delete_confirmation, getString(R.string.label_term)))
                        .setPositiveButton(R.string.action_yes, (d, w) -> {
                            database.termDao().delete(term);
                            terms.remove(position);
                            termAdapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton(R.string.action_cancel, (d, w) -> termAdapter.notifyItemChanged(position))
                        .show();
            }

            @Override
            public Term getItem(int position) {
                return terms.get(position);
            }
        })).attachToRecyclerView(termRecycler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        terms = database.termDao().getAllTerms();
        termAdapter = new TermAdapter(terms);
        termRecycler.setAdapter(termAdapter);

        termAdapter.setOnItemClickListener(term -> {
            Intent intent = new Intent(this, TermDetailActivity.class);
            intent.putExtra(EXTRA_TERM_ID, term.getId());
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}