package com.technion.android.joblin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CanMatchesActivity extends AppCompatActivity {

    MatchesItemsListAdapter adapter;
    private FirebaseAuth mAuth;
    private static final String TAG = "MyListActivity";
    FirebaseFirestore db;
    RecyclerView recyclerViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_matches);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerViewList = findViewById(R.id.RecyclerViewOfCanMatches);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewList.setLayoutManager(layoutManager);

        Query query = db.collection("Recruiters")
                .document(mAuth.getCurrentUser().getEmail())
                .collection("Matches")
                .limit(22);

        FirestoreRecyclerOptions<MatchesItem> options = new FirestoreRecyclerOptions.Builder<MatchesItem>()
                .setQuery(query, MatchesItem.class)
                .build();

        adapter = new MatchesItemsListAdapter(options);
        recyclerViewList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
