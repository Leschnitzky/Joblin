package com.technion.android.joblin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

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

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    private ImageView toProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_matches);

        mAuth = FirebaseAuth.getInstance();
        toProfileButton = findViewById(R.id.profile_back_button);
        toProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewList = findViewById(R.id.RecyclerViewOfRecMatches);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewList.setLayoutManager(layoutManager);

        Query query = recruitersCollection.document(mAuth.getCurrentUser().getEmail())
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
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
