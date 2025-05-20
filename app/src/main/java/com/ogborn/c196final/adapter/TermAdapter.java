package com.ogborn.c196final.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ogborn.c196final.R;
import com.ogborn.c196final.model.Term;

import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private final List<Term> terms;
    private OnItemClickListener onItemClickListener;

    public TermAdapter(List<Term> terms) {
        this.terms = terms;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    @NonNull
    public TermViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_term, parent, false);
        return new TermViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TermViewHolder holder, int position) {
        Term term = terms.get(position);
        holder.title.setText(term.getTitle());
        holder.dates.setText(holder.itemView.getContext()
                .getString(R.string.date_range, term.getStartDate(), term.getEndDate()));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(term);
            }
        });
    }

    @Override
    public int getItemCount() {
        return terms.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Term term);
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        TextView title, dates;

        TermViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_term_title);
            dates = itemView.findViewById(R.id.text_term_dates);
        }
    }
}