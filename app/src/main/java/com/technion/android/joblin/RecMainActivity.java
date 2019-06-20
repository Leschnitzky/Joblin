package com.technion.android.joblin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.victor.loading.rotate.RotateLoading;

import org.imperiumlabs.geofirestore.GeoFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORY_KEY;
import static com.technion.android.joblin.DatabaseUtils.JOB_LOCATION_KEY;
import static com.technion.android.joblin.DatabaseUtils.JOB_POINT_KEY;
import static com.technion.android.joblin.DatabaseUtils.NUMBER_OF_SWIPES_LEFT_KEY;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.REQUIRED_SCOPE_KEY;
import static com.technion.android.joblin.DatabaseUtils.SCOPE_KEY;
import static com.technion.android.joblin.DatabaseUtils.SWIPES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;


public class RecMainActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    FirebaseFirestore db;
    CollectionReference candidatesCollection, recruitersCollection, usersCollection, jobCategoriesCollection;
    RotateLoading rl;
    private String email;
    private TextView swipesLeftTxt;
    private SharedPreferences sharedPrefs;

    private enum Filter
    {
        CATEGORY, CITY, SCOPE
    }

    void InitializeMissingAttributes(DocumentSnapshot document, final String recruiterMail)
    {
        DocumentReference docRef = recruitersCollection.document(recruiterMail);
        Map<String,Object> attr = new HashMap<>();
        GeoPoint geoPoint = null;
        boolean addedPoint = false;
        if(document.get(JOB_POINT_KEY)==null)
        {
            Geocoder geocoder = new Geocoder(this);
            try {
                Address address = geocoder.getFromLocationName(document.get(JOB_LOCATION_KEY).toString(), 1).get(0);
                geoPoint = new GeoPoint(address.getLatitude(), address.getLongitude());
            }
            catch (Exception e) {
                return;
            }
            attr.put(JOB_POINT_KEY,geoPoint);
            addedPoint = true;
        }
        if(!attr.isEmpty())
            docRef.update(attr).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Utils.newAttributesPopup(getApplicationContext());
                }
            });
        if(document.get("g")==null)
        {
            GeoFirestore geoFirestore = new GeoFirestore(recruitersCollection);
            GeoPoint location = addedPoint ? geoPoint : (GeoPoint) document.get(JOB_POINT_KEY);
            geoFirestore.setLocation(recruiterMail, Objects.requireNonNull(location));
        }
    }

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
                    if (Objects.requireNonNull(document).exists()) {
                        InitializeMissingAttributes(document, recruiterMail);
                        String recruiterJobCategory = (String) document.get(JOB_CATEGORY_KEY);
                        GeoPoint recruiterJobLocation = (GeoPoint) document.get(JOB_POINT_KEY);
                        String recruiterJobScope = (String) document.get(REQUIRED_SCOPE_KEY);
                        int filter_method = sharedPrefs.getInt(getResources().getString(R.string.saved_filtering_method), Filter.CATEGORY.ordinal());
                        if(filter_method==Filter.CATEGORY.ordinal())
                            getCandidatesForSwipingScreen_FindRelevantCandidates(recruiterMail, recruiterJobCategory);
                        else if(filter_method==Filter.CITY.ordinal())
                            getCandidatesForSwipingScreen_FindRelevantCandidatesWithCity(recruiterMail, recruiterJobCategory,recruiterJobLocation);
                        else
                            getCandidatesForSwipingScreen_FindRelevantCandidatesWithScope(recruiterMail, recruiterJobCategory,recruiterJobScope);
                    }
                }
            }
        });
    }

    void getCandidatesForSwipingScreen_FindRelevantCandidates(final String recruiterMail,
                                                              final String recruiterJobCategory) {

        candidatesCollection
                .whereEqualTo(JOB_CATEGORY_KEY, recruiterJobCategory)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        List<Candidate> listOfCandidates = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots)) {
                            Candidate candidate = document.toObject(Candidate.class);
                            listOfCandidates.add(candidate);
                        }
                        getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped(recruiterMail, listOfCandidates);
                    }
                });
    }

    void getCandidatesForSwipingScreen_FindRelevantCandidatesWithCity(final String recruiterMail,
                                                                      final String recruiterJobCategory,
                                                                      final GeoPoint recruiterLocation) {
        candidatesCollection
                .whereEqualTo(JOB_CATEGORY_KEY, recruiterJobCategory)
                .whereEqualTo(JOB_POINT_KEY, recruiterLocation)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        List<Candidate> listOfCandidates = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots)) {
                            Candidate candidate = document.toObject(Candidate.class);
                            listOfCandidates.add(candidate);
                        }
                        getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped(recruiterMail, listOfCandidates);
                    }
                });
    }

    void getCandidatesForSwipingScreen_FindRelevantCandidatesWithScope(final String recruiterMail,
                                                                       final String recruiterJobCategory,
                                                                       final String recruiterScope) {
        candidatesCollection
                .whereEqualTo(JOB_CATEGORY_KEY, recruiterJobCategory)
                .whereEqualTo(SCOPE_KEY, recruiterScope)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        List<Candidate> listOfCandidates = new ArrayList<>();
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots)) {
                            Candidate candidate = document.toObject(Candidate.class);
                            listOfCandidates.add(candidate);
                        }
                        getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped(recruiterMail, listOfCandidates);
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
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                listOfCandidatesMailStrings.add(document.getId());
                            }
                            getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped_Final(listOfCandidates, listOfCandidatesMailStrings);
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

    void SwipesLeftUpdate(final String recruiterMail)
    {
        recruitersCollection.document(recruiterMail)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        swipesLeftTxt.setText(String.format("%d Left",documentSnapshot.getLong(NUMBER_OF_SWIPES_LEFT_KEY)));
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

//        LoginActivity.getInstance().finish();

        ImageButton imageButton = findViewById(R.id.profile_Button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecMainActivity.this,RecrEditActivity.class);
                startActivity(intent);
            }
        });

        ImageButton matchButton = findViewById(R.id.matches_Button);
        matchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecMainActivity.this,RecMatchesActivity.class);
                startActivity(intent);
            }
        });

        //Database initialization
        db = FirebaseFirestore.getInstance();
        candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
        recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
        usersCollection = db.collection(USERS_COLLECTION_NAME);
        jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        //swipeView initialization
        mSwipeView = findViewById(R.id.swipeView);
        swipesLeftTxt = findViewById(R.id.leftSwipedTxt);
        SwipesLeftUpdate(email);
        mContext = getApplicationContext();
        int bottomMargin = getResources().getDimensionPixelSize(R.dimen._130sdp);
        Point windowSize = Utils.getDisplaySize(getWindowManager());
        int padding = getResources().getDimensionPixelSize(R.dimen._7sdp);
        mSwipeView.getBuilder()
                .setIsUndoEnabled(true)
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(padding)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_out_msg_view));
        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {
            @Override
            public void onItemRemoved(int count) {
                if(mSwipeView.getAllResolvers().isEmpty()) {
                    rl.start();
                    findViewById(R.id.nothingNewTxt).animate().scaleY(1).start();
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("This will exit the application, are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(0);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

    @Override
    protected void onStart() {
        super.onStart();
        //initialization for display
        rl = findViewById(R.id.rotateloading);
        rl.start();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        TextView nothingNew = findViewById(R.id.nothingNewTxt);
        nothingNew.setText(R.string.no_new_rec_right_now);
        findViewById(R.id.nothingNewTxt).animate().scaleY(1).start();
        mSwipeView.removeAllViews();
        getCandidatesForSwipingScreen_MainFunction(email);
    }

}
