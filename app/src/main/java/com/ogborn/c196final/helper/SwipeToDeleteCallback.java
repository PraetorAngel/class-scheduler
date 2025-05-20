package com.ogborn.c196final.helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback<T> extends ItemTouchHelper.SimpleCallback {

    public interface DeleteHandler<T> {
        void onDelete(int position, T item);
        T getItem(int position);
    }

    private final DeleteHandler<T> handler;

    public SwipeToDeleteCallback(DeleteHandler<T> handler) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.handler = handler;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder tgt) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        T item = handler.getItem(position);
        handler.onDelete(position, item);
    }
}