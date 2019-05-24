package com.technion.android.joblin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MatchesItemsListAdapter extends FirestoreRecyclerAdapter<MatchesItem, MatchesItemViewHolder> {
    public MatchesItemsListAdapter(FirestoreRecyclerOptions recyclerOptions) {
        super(recyclerOptions);
    }

    @Override
    protected void onBindViewHolder(MatchesItemViewHolder holder, int position, MatchesItem model) {
        holder.bindToItem(model);
    }

    @Override
    public MatchesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_matches_list_adapter, parent, false);

        return new MatchesItemViewHolder(view);
    }

}