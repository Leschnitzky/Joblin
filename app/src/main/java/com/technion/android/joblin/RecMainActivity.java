package com.technion.android.joblin;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORY_KEY;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.SWIPES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;


public class RecMainActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);
    private final String email = "john3@gmail.com";
    RotateLoading rl;

    void getCandidatesForSwipingScreen_MainFunction(final String recruiterMail) {
        getCandidatesForSwipingScreen_CollectDataAboutRecruiter(recruiterMail);
    }

    void getCandidatesForSwipingScreen_CollectDataAboutRecruiter(final String recruiterMail) {
        DocumentReference docRef = recruitersCollection.document(recruiterMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String recruiterJobCategory = (String) document.get(JOB_CATEGORY_KEY);
                        getCandidatesForSwipingScreen_FindRelevantCandidates(recruiterMail, recruiterJobCategory);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    void getCandidatesForSwipingScreen_FindRelevantCandidates(final String recruiterMail,
                                                              final String recruiterJobCategory) {

        candidatesCollection
                .whereEqualTo(JOB_CATEGORY_KEY, recruiterJobCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Candidate> listOfCandidates = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Candidate candidate = document.toObject(Candidate.class);
                                listOfCandidates.add(candidate);
                            }
                            getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped(recruiterMail, listOfCandidates);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    void getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped(final String recruiterMail,
                                                                                  final List<Candidate> listOfCandidates) {

        recruitersCollection.document(recruiterMail).collection(SWIPES_COLLECTION_NAME).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listOfCandidatesMailStrings = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                listOfCandidatesMailStrings.add(document.getId());
                            }
                            getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped_Final(listOfCandidates, listOfCandidatesMailStrings);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    void getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped_Final(final List<Candidate> listOfCandidates,
                                                                                        final List<String> listOfCandidatesMailStrings) {

        List<Candidate> finalListOfCandidates = new ArrayList<>();
        for(Candidate candidate : listOfCandidates) {
            String candidateMail = candidate.getEmail();
            if(! listOfCandidatesMailStrings.contains(candidateMail)) {
                Candidate candidateToAdd = new Candidate(candidate);
                finalListOfCandidates.add(candidateToAdd);
            }
        }

        getCandidatesForSwipingScreen(finalListOfCandidates);
    }


    void getCandidatesForSwipingScreen(List<Candidate> listOfCandidates) {
        for(Candidate profile : listOfCandidates){
            if(rl.isStart())
            {
                rl.stop();
                findViewById(R.id.nothingNewTxt).animate().scaleY(0).start();
            }
            mSwipeView.addView(new CandidateCard(mContext, profile, mSwipeView,email));
        }
    }

    void searchNew()
    {
        while (mSwipeView.getAllResolvers().isEmpty())
            getCandidatesForSwipingScreen_MainFunction(email);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        DatabaseAPI databaseAPI = new DatabaseAPI();
        databaseAPI.initializeDBWithSomeData();
        mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        rl = findViewById(R.id.rotateloading);
        rl.start();
        TextView nothingNew = findViewById(R.id.nothingNewTxt);
        nothingNew.setText(R.string.no_new_can_right_now);

        int bottomMargin = Utils.dpToPx(180);
        Point windowSize = Utils.getDisplaySize(getWindowManager());
        mSwipeView.getBuilder()
                .setIsUndoEnabled(true)
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out_msg_view));
        getCandidatesForSwipingScreen_MainFunction(email);

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                if(mSwipeView.getAllResolvers().isEmpty()) {
                    rl.start();
                    findViewById(R.id.nothingNewTxt).animate().scaleY(1).start();
                    searchNew();
                }
            }
        });

        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });
    }
}