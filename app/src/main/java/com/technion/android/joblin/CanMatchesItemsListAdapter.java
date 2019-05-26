package com.technion.android.joblin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CanMatchesItemsListAdapter extends FirestoreRecyclerAdapter<MatchesItem, CanMatchesItemViewHolder> {
    public CanMatchesItemsListAdapter(FirestoreRecyclerOptions recyclerOptions) {
        super(recyclerOptions);
    }

    @Override
    protected void onBindViewHolder(CanMatchesItemViewHolder holder, int position, MatchesItem model) {
        holder.bindToItem(model);
    }

    @Override
    public CanMatchesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_can_matches_list_adapter, parent, false);

        return new CanMatchesItemViewHolder(view);
    }

}