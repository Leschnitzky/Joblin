package com.technion.android.joblin;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORY_KEY;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.SWIPES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;


public class CanMainActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    void getRecruitersForSwipingScreen_MainFunction(final String candidateMail) {
        getRecruitersForSwipingScreen_CollectDataAboutCandidate(candidateMail);
    }

    void getRecruitersForSwipingScreen_CollectDataAboutCandidate(final String candidateMail) {
        DocumentReference docRef = candidatesCollection.document(candidateMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String candidateJobCategory = (String) document.get(JOB_CATEGORY_KEY);
                        getRecruitersForSwipingScreen_FindRelevantRecruiters(candidateMail, candidateJobCategory);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    void getRecruitersForSwipingScreen_FindRelevantRecruiters(final String candidateMail,
                                                              final String candidateJobCategory) {

        recruitersCollection
                .whereEqualTo(JOB_CATEGORY_KEY, candidateJobCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Recruiter> listOfRecruiters = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Recruiter recruiter = document.toObject(Recruiter.class);
                                listOfRecruiters.add(recruiter);
                            }
                            getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped(candidateMail, listOfRecruiters);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped(final String candidateMail,
                                                                                  final List<Recruiter> listOfRecruiters) {

        candidatesCollection.document(candidateMail).collection(SWIPES_COLLECTION_NAME).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listOfRecruitersMailStrings = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                listOfRecruitersMailStrings.add(document.getId());
                            }
                            getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped_Final(listOfRecruiters, listOfRecruitersMailStrings);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    void getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped_Final(final List<Recruiter> listOfRecruiters,
                                                                                        final List<String> listOfRecruitersMailStrings) {

        List<Recruiter> finalListOfRecruiters = new ArrayList<>();
        for(Recruiter recruiter : listOfRecruiters) {
            String recruiterMail = recruiter.getEmail();
            if(! listOfRecruitersMailStrings.contains(recruiterMail)) {
                Recruiter recruiterToAdd = new Recruiter(recruiter);
                finalListOfRecruiters.add(recruiterToAdd);
            }
        }

        getRecruitersForSwipingScreen(finalListOfRecruiters);

    }


    void getRecruitersForSwipingScreen(List<Recruiter> listofRecruiters) {
        for(Recruiter profile : listofRecruiters){
            mSwipeView.addView(new RecruiterCard(mContext, profile, mSwipeView));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        DatabaseAPI databaseAPI = new DatabaseAPI();
        databaseAPI.initializeDBWithSomeData();
        mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        int bottomMargin = Utils.dpToPx(180);
        Point windowSize = Utils.getDisplaySize(getWindowManager());
        mSwipeView.getBuilder()

                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out_msg_view));

        getRecruitersForSwipingScreen_MainFunction("levi.weiss3@gmail.com");

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
