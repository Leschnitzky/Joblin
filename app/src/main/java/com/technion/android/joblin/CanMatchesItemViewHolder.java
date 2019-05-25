package com.technion.android.joblin;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.view.View;

public class CanMatchesItemViewHolder extends RecyclerView.ViewHolder {
    private TextView dataTextView;
    private TextView idTextView;

    public CanMatchesItemViewHolder(View itemView) {
        super(itemView);
        dataTextView = itemView.findViewById(R.id.dataTextView);
        idTextView = itemView.findViewById(R.id.idTextView);
    }

    public void bindToItem(MatchesItem item) {
        dataTextView.setText(item.getTitle());
        String start_char = ((Character)Character.toUpperCase(item.getTitle().charAt(0))).toString();
        idTextView.setText(start_char);
    }
}
