package com.technion.android.joblin;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.technion.android.joblin.DatabaseUtils.*;

public class LoginActivity extends AppCompatActivity {

    public static final String FIRST_NAME_KEY = "firstName";
    public static final String LAST_NAME_KEY = "lastName";
    private boolean not_in_db_cand = false;
    private boolean not_in_db_recr = false;

    private final int RC_SIGN_IN = 530;
    private String mUserFirstName;
    private String mUserLastName;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Init", "firebaseAuthWithGoogle:" + acct.getId());

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

    //Todo: 1 - Add the login aftermath
    //Todo: 2 - Redesign layout
    //Todo: 3 - Integrate with Levi's API
    //Todo: 4 - Write Robolectric tests to see if the user can break the app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen_layout);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
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

                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Log.d("ANOTHER", e.getMessage());
            }
        }
    }


    // Todo: Redundant?
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
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
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                    // Todo: Change to bar's activity name once pulled from dev (Cand)
                        Intent intent = new Intent(LoginActivity.this, CandProfPrefActivity.class);
                        intent.putExtra(FIRST_NAME_KEY,mUserFirstName);
                        intent.putExtra(LAST_NAME_KEY,mUserLastName);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Not Found Cand", Toast.LENGTH_SHORT).show();
                        // Not a Cand, check if recruiter
                        isRecruiterInDB(email);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
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
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        // Todo: Change to bar's activity name once pulled from dev (Recr)
                        Intent intent = new Intent(LoginActivity.this, RecrProfPrefActivity.class);
                        intent.putExtra(FIRST_NAME_KEY,mUserFirstName);
                        intent.putExtra(LAST_NAME_KEY,mUserLastName);
                        startActivity(intent);
                    } else {
                        // Not a Cand nor Recr
                        // Logged in with google, just need to choose
                        Log.d(TAG, "No such document");

                        Toast.makeText(LoginActivity.this, "Not Found Recr", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, ChooseUserTypeActivity.class);
                        intent.putExtra(FIRST_NAME_KEY,mUserFirstName);
                        intent.putExtra(LAST_NAME_KEY,mUserLastName);
                        startActivity(intent);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
