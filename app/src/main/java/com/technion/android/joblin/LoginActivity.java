package com.technion.android.joblin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

public class LoginActivity extends AppCompatActivity {

    public static final String FIRST_NAME_KEY = "firstName";
    public static final String LAST_NAME_KEY = "lastName";
    public static final String URI_KEY = "userPhoto";
    private boolean not_in_db_cand = false;
    private boolean not_in_db_recr = false;

    private final int RC_SIGN_IN = 530;
    ProgressDialog dialog;
    private String mUserFirstName;
    private String mUserLastName;
    private Uri mUserPhoto;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView privatepolicy;

    Context context = this;

    private static LoginActivity activity;

    public static final int ALARM_REQUEST_CODE = 101;



    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);


    public static LoginActivity getInstance(){
        return activity;
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            //Will check and advance the user to choose if that's his first time
                            isCandidateOrRecrInDB(user.getEmail());
//
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_layout);

//        DatabaseAPI dbAPI = new DatabaseAPI();
//        dbAPI.initializeDBWithSomeData();

        dialog = new ProgressDialog(LoginActivity.this);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        activity = this;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        privatepolicy = findViewById(R.id.privacyPolicyLink);
        privatepolicy.setText(
                Html.fromHtml(
                        "<a href=\"https://joblin.flycricket.io/privacy.html\">Privacy Policy</a>"));
        privatepolicy.setMovementMethod(LinkMovementMethod.getInstance());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

//        DatabaseAPI dbAPI = new DatabaseAPI();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mUserFirstName = account.getGivenName();
                mUserLastName = account.getFamilyName();
                mUserPhoto = account.getPhotoUrl();

                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


//    // Todo: Redundant?
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        GoogleSignInAccount currentUser = GoogleSignIn.getLastSignedInAccount(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(currentUser != null) {
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.setInverseBackgroundForced(false);
            dialog.show();
            mUserFirstName = account.getDisplayName();
            mUserLastName = account.getFamilyName();
            mUserPhoto = currentUser.getPhotoUrl();
            isCandidateOrRecrInDB(currentUser.getEmail());
        }
    }

    void isCandidateOrRecrInDB(final String email) {
        DocumentReference docRef = candidatesCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Already a candidate
                        dialog.hide();
                        Intent intent = new Intent(LoginActivity.this, CanMainActivity.class);
                        startActivity(intent);
                    } else {
                        // Not a Cand, check if recruiter
                        isRecruiterInDB(email);
                    }
                } else {
                }
            }
        });
    }


    void isRecruiterInDB(final String email) {
        DocumentReference docRef = recruitersCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Tis a recruiter
                        dialog.hide();

                        Intent intent = new Intent(LoginActivity.this, RecMainActivity.class);
                        startActivity(intent);
                    } else {
                        // Not a Cand nor Recr
                        // Logged in with google, just need to choose


                        Intent intent = new Intent(LoginActivity.this, ChooseUserTypeActivity.class);
                        intent.putExtra(FIRST_NAME_KEY,mUserFirstName);
                        intent.putExtra(LAST_NAME_KEY,mUserLastName);
                        intent.putExtra(URI_KEY,mUserPhoto.toString());
                        startActivity(intent);
                        dialog.hide();
                    }
                } else {
                }
            }
        });
    }


//    This is the insertion of the FCM Token
    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        //TODO: add the token adding logic
}


    private void initFCM(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "initFCM: token: " + token);
        sendRegistrationToServer(token);

    }
}
