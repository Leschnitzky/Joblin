package com.technion.android.joblin;

import android.Manifest.permission;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentChange.Type;
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
import org.imperiumlabs.geofirestore.callbacks.GeoQueryDataEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORY_KEY;
import static com.technion.android.joblin.DatabaseUtils.JOB_RADIUS_KEY;
import static com.technion.android.joblin.DatabaseUtils.NUMBER_OF_SWIPES_LEFT_KEY;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.SWIPES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

public class CanMainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private Activity mActivity = this;
    FirebaseFirestore db;
    CollectionReference candidatesCollection, recruitersCollection, usersCollection, jobCategoriesCollection;
    RotateLoading rl;
    private String email;
    private TextView swipesLeftTxt;
    private Location myLocation;
    private SharedPreferences sharedPrefs;
    FusedLocationProviderClient fusedLocationClient;

    private enum Filter {
        CATEGORY, DISTANCE, CITY, SCOPE
    }

    public static Boolean candSuperLiked = false;

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
                    if (Objects.requireNonNull(document).exists()) {
                        String candidateJobCategory = (String) document.get(JOB_CATEGORY_KEY);
                        List<Double> l = (List<Double>) document.get("l");
                        assert l != null;
                        GeoPoint candidateJobLocation = new GeoPoint(l.get(0), l.get(1));
                        GeoPoint candidateCurrentLocation = (myLocation!=null) ?
                            new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()) : null;
                        Long candidateJobRadius = (Long) document.get(JOB_RADIUS_KEY);
                        int filter_method = sharedPrefs.getInt(getResources().getString(R.string.saved_filtering_method), Filter.CATEGORY.ordinal());
                        if (filter_method == Filter.CATEGORY.ordinal())
                            getRecruitersForSwipingScreen_FindRelevantRecruiters(candidateMail, candidateJobCategory);
                        else if (filter_method == Filter.DISTANCE.ordinal()) {
                            if(candidateCurrentLocation!=null)
                            getRecruitersForSwipingScreen_FindRelevantRecruitersWithDistance(candidateMail, candidateJobCategory,
                                    candidateCurrentLocation, candidateJobRadius);
                            else
                                LocationDeniedPopUp();
                        }
                        else if (filter_method == Filter.CITY.ordinal())
                            getRecruitersForSwipingScreen_FindRelevantRecruitersWithCity(candidateMail, candidateJobCategory, candidateJobLocation);
                    }
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
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                            if (documentChange.getType().equals(Type.ADDED)) {
                                Recruiter recruiter = documentChange.getDocument().toObject(Recruiter.class);
                                listOfRecruiters.add(recruiter);
                            }
                        }
                        getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped(candidateMail, listOfRecruiters);
                    }
                });
    }

    void getRecruitersForSwipingScreen_FindRelevantRecruitersWithDistance(final String candidateMail,
                                                                          final String candidateJobCategory,
                                                                          final GeoPoint candidateLocation,
                                                                          final Long radius) {
        GeoFirestore geoFirestore = new GeoFirestore(recruitersCollection);
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
                        List<DocumentSnapshot> documents = new ArrayList<>();
                        geoFirestore.queryAtLocation(candidateLocation, radius).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                            @Override
                            public void onDocumentEntered(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {
                                documents.add(documentSnapshot);
                            }

                            @Override
                            public void onDocumentExited(@NotNull DocumentSnapshot documentSnapshot) {

                            }

                            @Override
                            public void onDocumentMoved(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {

                            }

                            @Override
                            public void onDocumentChanged(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots)) {
                                    for (DocumentSnapshot inDistanceDoc : documents) {
                                        if (inDistanceDoc.getId().equals(document.getId())) {
                                            Recruiter recruiter = document.toObject(Recruiter.class);
                                            listOfRecruiters.add(recruiter);
                                        }
                                    }
                                }
                                getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped(candidateMail, listOfRecruiters);
                            }

                            @Override
                            public void onGeoQueryError(@NotNull Exception e) {

                            }
                        });
                    }
                });
    }

    void getRecruitersForSwipingScreen_FindRelevantRecruitersWithCity(final String candidateMail,
                                                                      final String candidateJobCategory,
                                                                      final GeoPoint candidateLocation) {
        GeoFirestore geoFirestore = new GeoFirestore(recruitersCollection);
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
                        List<DocumentSnapshot> documents = new ArrayList<>();
                        geoFirestore.queryAtLocation(candidateLocation, 0).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                            @Override
                            public void onDocumentEntered(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {
                                documents.add(documentSnapshot);
                            }

                            @Override
                            public void onDocumentExited(@NotNull DocumentSnapshot documentSnapshot) {

                            }

                            @Override
                            public void onDocumentMoved(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {

                            }

                            @Override
                            public void onDocumentChanged(@NotNull DocumentSnapshot documentSnapshot, @NotNull GeoPoint geoPoint) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots)) {
                                    for (DocumentSnapshot inDistanceDoc : documents) {
                                        if (inDistanceDoc.getId().equals(document.getId())) {
                                            Recruiter recruiter = document.toObject(Recruiter.class);
                                            listOfRecruiters.add(recruiter);
                                        }
                                    }
                                }
                                getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped(candidateMail, listOfRecruiters);
                            }

                            @Override
                            public void onGeoQueryError(@NotNull Exception e) {

                            }
                        });
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
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                listOfRecruitersMailStrings.add(document.getId());
                            }
                            getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped_Final(listOfRecruiters, listOfRecruitersMailStrings);
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

        //Database initialization
        db = FirebaseFirestore.getInstance();
        ImageButton mProfileButton = findViewById(R.id.profile_Button);
        ImageButton mMatchesButton = findViewById(R.id.matches_Button);
        candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
        recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
        usersCollection = db.collection(USERS_COLLECTION_NAME);
        jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //swipeView initialization
        mSwipeView = findViewById(R.id.swipeView);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CanMainActivity.this, CandEditActivity.class);
                startActivity(intent);
            }
        });
        mMatchesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CanMainActivity.this, CanMatchesActivity.class);
                startActivity(intent);
            }
        });
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
                if (mSwipeView.getAllResolvers().isEmpty()) {
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
        findViewById(R.id.starBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                candSuperLiked = true;
                mSwipeView.doSwipe(true);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //initialization for display
        rl = findViewById(R.id.rotateloading);
        rl.start();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSwipeView = findViewById(R.id.swipeView);
        TextView nothingNew = findViewById(R.id.nothingNewTxt);
        nothingNew.setText(R.string.no_new_rec_right_now);
        findViewById(R.id.nothingNewTxt).animate().scaleY(1).start();
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            {

                ActivityCompat.requestPermissions(CanMainActivity.this,
                        new String[]{permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            }
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                myLocation = location;
                            }
                            mSwipeView.removeAllViews();
                            getRecruitersForSwipingScreen_MainFunction(email);
                        }
                    });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        myLocation = location;
                                    }
                                    mSwipeView.removeAllViews();
                                    getRecruitersForSwipingScreen_MainFunction(email);
                                }
                            });
                }
                else
                    LocationDeniedPopUp();
            }
        }
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

    private void LocationDeniedPopUp()
    {
        new SweetAlertDialog(CanMainActivity.this,SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Location Permission Denied")
                .setContentText("Can't filter by distance or show distance to jobs")
                .setConfirmText("Ask again")
                .setConfirmClickListener(new OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        ActivityCompat.requestPermissions(CanMainActivity.this,
                                new String[]{permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelClickListener(new OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        mSwipeView.removeAllViews();
                        getRecruitersForSwipingScreen_MainFunction(email);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelText("Continue")
                .show();
    }
}
