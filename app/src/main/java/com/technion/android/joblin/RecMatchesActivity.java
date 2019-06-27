package com.technion.android.joblin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
    Context mContext;
    Intent intent;

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
        mContext = this;
        intent = getIntent();
        toProfileButton = findViewById(R.id.profile_back_button);
        toProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager.getInstance(mContext).
                        unregisterReceiver(currentActivityReceiver);
                currentActivityReceiver = null;
                if(intent.getBooleanExtra("isNotif",false)) {
                    Intent i = new Intent(RecMatchesActivity.this, RecMainActivity.class);
                    startActivity(i);
                }
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

    private BroadcastReceiver currentActivityReceiver;

    @Override
    protected void onResume() {
        super.onResume();

        currentActivityReceiver = new CurrentActivityReceiver(this);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(currentActivityReceiver, CurrentActivityReceiver.CURRENT_ACTIVITY_RECEIVER_FILTER);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(currentActivityReceiver);
        currentActivityReceiver = null;
        super.onPause();
    }

}
