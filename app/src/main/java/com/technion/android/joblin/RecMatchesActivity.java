package com.technion.android.joblin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.technion.android.joblin.DatabaseUtils.*;


public class RecMatchesActivity extends AppCompatActivity {

    RecMatchesItemsListAdapter adapter;
    private FirebaseAuth mAuth;
    RecyclerView recyclerViewList;

    FirebaseFirestore db;
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_matches);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        recyclerViewList = findViewById(R.id.RecyclerViewOfRecMatches);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewList.setLayoutManager(layoutManager);

        Query query = candidatesCollection.document(mAuth.getCurrentUser().getEmail())
                .collection(MATCHES_COLLECTION_NAME);

        FirestoreRecyclerOptions<MatchesItem> options = new FirestoreRecyclerOptions.Builder<MatchesItem>()
                .setQuery(query, MatchesItem.class)
                .build();

        adapter = new RecMatchesItemsListAdapter(options);
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
