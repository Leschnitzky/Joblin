package com.technion.android.joblin;

import android.Manifest.permission;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import static com.technion.android.joblin.DatabaseUtils.NUMBER_OF_SWIPES_LEFT_KEY;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.SWIPES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;


public class CanMainActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    FirebaseFirestore db;
    CollectionReference candidatesCollection, recruitersCollection, usersCollection, jobCategoriesCollection;
    private FirebaseAuth mAuth;
    RotateLoading rl;
    private String email;
    private ImageButton mProfileButton;
    private ImageButton mMatchesButton;
    private TextView swipesLeftTxt;
    private FusedLocationProviderClient fusedLocationClient;
    private Location myLocation;

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
                        String candidateJobCategory = (String) document.get(JOB_CATEGORY_KEY);
                        getRecruitersForSwipingScreen_FindRelevantRecruiters(candidateMail, candidateJobCategory);
                    } else {
                    }
                } else {
                }
            }
        });

    }

    void getRecruitersForSwipingScreen_FindRelevantRecruiters(final String candidateMail,
                                                              final String candidateJobCategory) {
        recruitersCollection
                .whereEqualTo(JOB_CATEGORY_KEY, candidateJobCategory)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        List<Recruiter> listOfRecruiters = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Recruiter recruiter = document.toObject(Recruiter.class);
                            listOfRecruiters.add(recruiter);
                        }
                        getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped(candidateMail, listOfRecruiters);
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
                                listOfRecruitersMailStrings.add(document.getId());
                            }
                            getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped_Final(listOfRecruiters, listOfRecruitersMailStrings);
                        } else {
                        }
                    }
                });

    }

    void getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped_Final(final List<Recruiter> listOfRecruiters,
                                                                                        final List<String> listOfRecruitersMailStrings) {

        List<Recruiter> finalListOfRecruiters = new ArrayList<>();
        for (Recruiter recruiter : listOfRecruiters) {
            String recruiterMail = recruiter.getEmail();
            if (!listOfRecruitersMailStrings.contains(recruiterMail)) {
                Recruiter recruiterToAdd = new Recruiter(recruiter);
                finalListOfRecruiters.add(recruiterToAdd);
            }
        }

        getRecruitersForSwipingScreen(finalListOfRecruiters);

    }


    void getRecruitersForSwipingScreen(List<Recruiter> listofRecruiters) {
        for (Recruiter profile : listofRecruiters) {
            if (rl.isStart()) {
                rl.stop();
                findViewById(R.id.nothingNewTxt).animate().scaleY(0).start();
            }
            mSwipeView.addView(new RecruiterCard(mContext, profile, mSwipeView, email, myLocation));
        }
    }

    void SwipesLeftUpdate(final String candidateMail) {
        candidatesCollection.document(candidateMail)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Utils.errorPopUp(CanMainActivity.this, e.getMessage());
                            return;
                        }
                        swipesLeftTxt.setText(String.format("%d Left", documentSnapshot.getLong(NUMBER_OF_SWIPES_LEFT_KEY)));
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);
        LoginActivity.getInstance().finish();

        //Database initialization
        db = FirebaseFirestore.getInstance();
        mProfileButton = findViewById(R.id.profile_Button);
        mMatchesButton = findViewById(R.id.matches_Button);
        candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
        recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
        usersCollection = db.collection(USERS_COLLECTION_NAME);
        jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);
        mAuth = FirebaseAuth.getInstance();
        email = mAuth.getCurrentUser().getEmail();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            myLocation = location;
                        }
                    }
                });

        //swipeView initialization
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CanMainActivity.this,CandEditActivity.class);
                startActivity(intent);
            }
        });
        mMatchesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CanMainActivity.this,CanMatchesActivity.class);
                startActivity(intent);
            }
        });
        mSwipeView = findViewById(R.id.swipeView);
        swipesLeftTxt = findViewById(R.id.leftSwipedTxt);
        SwipesLeftUpdate(email);
        mContext = getApplicationContext();
        int bottomMargin = Utils.dpToPx(170);
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

        //initialization for display
        rl = findViewById(R.id.rotateloading);
        rl.start();
        TextView nothingNew = findViewById(R.id.nothingNewTxt);
        nothingNew.setText(R.string.no_new_rec_right_now);


        getRecruitersForSwipingScreen_MainFunction(email);
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


}
