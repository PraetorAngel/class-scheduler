package com.ogborn.c196final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ogborn.c196final.R;
import com.ogborn.c196final.model.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courses;
    private OnItemClickListener onItemClickListener;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    @NonNull
    public CourseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.title.setText(course.getTitle());
        holder.instructor.setText(course.getInstructorName());
        holder.status.setText(course.getStatus());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {return courses.size();}

    public interface OnItemClickListener {
        void onItemClick(Course course);
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, status, instructor;

        CourseViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_course_title);
            instructor = itemView.findViewById(R.id.text_course_instructor);
            status = itemView.findViewById(R.id.text_course_status);
        }
    }
}