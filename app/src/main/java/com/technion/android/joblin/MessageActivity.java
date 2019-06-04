package com.technion.android.joblin;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.aminography.redirectglide.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;


public class MessageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    String currentUserMail;
    Intent intent;
    ImageView profileImage;
    TextView profileName;
    String otherEmail;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    private ImageView toProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mAuth = FirebaseAuth.getInstance();
        currentUserMail = mAuth.getCurrentUser().getEmail();
        intent = getIntent();
        profileName = findViewById(R.id.profileMessageName);
        profileImage = findViewById(R.id.profileMessageImage);
        otherEmail = intent.getStringExtra("email");
        if(intent.getStringExtra("type").equals("rec"))
            getRecruiter(otherEmail);
        else
            getCandidate(otherEmail);
    }
    void getCandidate(final String email) {
        DocumentReference docRef = candidatesCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Candidate mProfile = document.toObject(Candidate.class);
                        profileName.setText(String.format("%s %s", mProfile.getName(), mProfile.getLastName()));
                        GlideApp.with(profileImage.getContext()).load(mProfile.getImageUrl()).into(profileImage);
                    }
                }
            }
        });
    }
    void getRecruiter(final String email) {
        DocumentReference docRef = recruitersCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Recruiter mProfile = document.toObject(Recruiter.class);
                        profileName.setText(String.format("%s %s", mProfile.getName(), mProfile.getLastName()));
                        GlideApp.with(profileImage.getContext()).load(mProfile.getImageUrl()).into(profileImage);
                    }
                }
            }
        });
    }
}
