package com.ogborn.c196final.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ogborn.c196final.R;
import com.ogborn.c196final.model.Assessment;

import java.util.List;

public class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.AssessmentViewHolder> {

    private final List<Assessment> assessments;
    private OnItemClickListener onItemClickListener;

    public AssessmentAdapter(List<Assessment> assessments) {
        this.assessments = assessments;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    @NonNull
    public AssessmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assessment, parent, false);
        return new AssessmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AssessmentViewHolder holder, int position) {
        Assessment assessment = assessments.get(position);
        holder.title.setText(assessment.getTitle());
        holder.dates.setText(holder.itemView.getContext().getString(R.string.date_range, assessment.getStartDate(), assessment.getEndDate()));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(assessment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Assessment assessment);
    }

    public static class AssessmentViewHolder extends RecyclerView.ViewHolder {
        TextView title, dates;

        AssessmentViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_assessment_title);
            dates = itemView.findViewById(R.id.text_assessment_dates);
        }
    }
}