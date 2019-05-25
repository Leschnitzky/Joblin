package com.technion.android.joblin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecMatchesItemsListAdapter extends FirestoreRecyclerAdapter<MatchesItem, RecMatchesItemViewHolder> {
    public RecMatchesItemsListAdapter(FirestoreRecyclerOptions recyclerOptions) {
        super(recyclerOptions);
    }

    @Override
    protected void onBindViewHolder(RecMatchesItemViewHolder holder, int position, MatchesItem model) {
        holder.bindToItem(model);
    }

    @Override
    public RecMatchesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_rec_matches_list_adapter, parent, false);

        return new RecMatchesItemViewHolder(view);
    }

}