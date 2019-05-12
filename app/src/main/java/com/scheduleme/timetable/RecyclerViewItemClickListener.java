package com.scheduleme.timetable;

import android.view.View;

public interface RecyclerViewItemClickListener {
    void onItemClick(View v,long id);
    void onDeleteClick(View v, long id);
    void onNotificationClicked(View v, long id, RecyclerViewAdapter.RecyclerViewHolder holder);
}
