package com.technion.android.joblin;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import io.opencensus.internal.StringUtil;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

public class CandEditActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog dialog;
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    ImageView userImage;
    TextView mUserName;
    TextView mUserSkills;
    TextView mUserEducation;
    TextView mUserJobCategory;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cand_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cand);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        dialog.setMessage("Retrieving user information...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        mUserSkills = findViewById(R.id.user_skills);
        mUserName = findViewById(R.id.user_nameText);
        mUserEducation = findViewById(R.id.user_education);
        mUserJobCategory = findViewById(R.id.user_job_category);

        userImage = findViewById(R.id.user_image);
        String email = mAuth.getCurrentUser().getEmail();
        getCandidate(email);
    }


    void getCandidate(final String email) {
        DocumentReference docRef = candidatesCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Candidate candidate = document.toObject(Candidate.class);

                        String skillText = "";
                        for(String skill : candidate.getSkillsList()){
                            skillText += skill;
                            skillText += ", ";
                        }
                        skillText = skillText.substring(0,skillText.length() - 2);
                        skillText += ".";
                        mUserSkills.setText(skillText);
                        mUserEducation.setText(candidate.getEducation());
                        mUserName.setText(candidate.getName() +" "+ candidate.getLastName());
                        mUserJobCategory.setText(candidate.getJobCategory());

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                    getApplicationContext().getContentResolver(),Uri.parse(candidate.getImageUrl()));
                            dialog.hide();
                        } catch (IOException e) {
                            e.printStackTrace();

                        }
                    } else {
                        Toast.makeText(CandEditActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CandEditActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
