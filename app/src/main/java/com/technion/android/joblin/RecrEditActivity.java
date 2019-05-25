package com.technion.android.joblin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class RecrEditActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog dialog;
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    ImageView userImage;
    TextView mUserName;
    Context mContext;
    TextView mUserSkills;
    TextView mUserEducation;
    ImageView mProfileBackButton;
    TextView mUserJobCategory;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recr_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_recr);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        dialog = new ProgressDialog(RecrEditActivity.this);
        dialog.setMessage("Retrieving user information...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        mUserSkills = findViewById(R.id.recr_skills);
        mUserName = findViewById(R.id.recr_nameText);
        mUserEducation = findViewById(R.id.recr_education);
        mUserJobCategory = findViewById(R.id.recr_job_category);
        mContext = this;
        mProfileBackButton = findViewById(R.id.recr_edit_back_button);

        mProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecrEditActivity.this,RecMainActivity.class);
                startActivity(intent);
            }
        });

        userImage = findViewById(R.id.recr_image);
        String email = mAuth.getCurrentUser().getEmail();
        getRecruiter(email);
    }


    void getRecruiter(final String email) {
        DocumentReference docRef = recruitersCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Recruiter recruiter = document.toObject(Recruiter.class);

                        String skillText = "";
                        for(String skill : recruiter.getRequiredSkillsList()){
                            skillText += skill;
                            skillText += ", ";
                        }
                        skillText = skillText.substring(0,skillText.length() - 2);
                        skillText += ".";
                        mUserSkills.setText(skillText);
                        mUserEducation.setText(recruiter.getRequiredEducation());
                        mUserName.setText(recruiter.getName() +" "+ recruiter.getLastName());
                        mUserJobCategory.setText(recruiter.getJobCategory());
                        GlideApp.with(mContext).load(recruiter.getImageUrl()).into(userImage);
                        dialog.hide();
                    } else {
                        dialog.hide();
                        Toast.makeText(RecrEditActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dialog.hide();
                    Toast.makeText(RecrEditActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

