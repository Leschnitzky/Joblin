package com.technion.android.joblin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.technion.android.joblin.DatabaseUtils.*;


public class CanMatchesActivity extends AppCompatActivity {

    CanMatchesItemsListAdapter adapter;
    private FirebaseAuth mAuth;
    RecyclerView recyclerViewList;
    String currentUserMail;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    private ImageView toProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_matches);

        mAuth = FirebaseAuth.getInstance();
        currentUserMail = mAuth.getCurrentUser().getEmail();

        toProfileButton = findViewById(R.id.profile_back_button);
        toProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CanMatchesActivity.this, CanMainActivity.class);
                startActivity(intent);
            }
        });

        recyclerViewList = findViewById(R.id.RecyclerViewOfCanMatches);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerViewList.setHasFixedSize(true);
        recyclerViewList.setLayoutManager(layoutManager);

        Query query = candidatesCollection.document(currentUserMail).collection(MATCHES_COLLECTION_NAME);

        FirestoreRecyclerOptions<MatchesItem> options = new FirestoreRecyclerOptions.Builder<MatchesItem>()
                .setQuery(query, MatchesItem.class)
                .build();

        adapter = new CanMatchesItemsListAdapter(options);
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
