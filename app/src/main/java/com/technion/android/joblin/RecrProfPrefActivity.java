package com.technion.android.joblin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import static com.technion.android.joblin.DatabaseUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecrProfPrefActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    final Calendar myCalendar = Calendar.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressDialog dialog;
    private ArrayList<String> jobCategories = new ArrayList<>();
    Spinner mJobCategorySpinner;
    EditText mScopeText;
    EditText mLocationText;
    EditText mEducationText;
    EditText mFirstSkillText;
    EditText mSecondSkillText;
    EditText mThirdSkillText;

    Intent thisIntent;

    EditText mDescriptionText;

    Button mSubmitButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initiateCategories();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recr_prof_pref);
        dialog = new ProgressDialog(RecrProfPrefActivity.this);
        thisIntent = getIntent();

        mLocationText = findViewById(R.id.location_recr);
        mScopeText = findViewById(R.id.scope_recr);
        mEducationText = findViewById(R.id.education_recr);
        mDescriptionText = findViewById(R.id.job_description);
        mFirstSkillText = findViewById(R.id.first_skill_recr);
        mSecondSkillText = findViewById(R.id.second_skill_recr);
        mThirdSkillText = findViewById(R.id.third_skill_recr);

        mSubmitButton = findViewById(R.id.submit_button_recr);

        mJobCategorySpinner = findViewById(R.id.category_spinner_recr);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.
                support_simple_spinner_dropdown_item,jobCategories);
        mJobCategorySpinner.setAdapter(adapter);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Check all fields
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.setInverseBackgroundForced(false);
                dialog.show();
                ArrayList<Integer> emptyFields = checkAllEditTextsForEmptyStrings();
                if(emptyFields.isEmpty()){
                    Recruiter recr = new Recruiter(

                            mAuth.getCurrentUser().getEmail(),
                            thisIntent.getStringExtra(LoginActivity.FIRST_NAME_KEY),
                            thisIntent.getStringExtra(LoginActivity.LAST_NAME_KEY),
                            thisIntent.getStringExtra(LoginActivity.URI_KEY),
                            //         new Timestamp(myCalendar.getTime()),
                            mJobCategorySpinner.getSelectedItem().toString(),
                            mScopeText.getText().toString(),
                            mDescriptionText.getText().toString(),
                            mLocationText.getText().toString(),
                            mEducationText.getText().toString(),
                            Arrays.asList(mFirstSkillText.getText().toString(),
                                    mSecondSkillText.getText().toString(),
                                    mThirdSkillText.getText().toString())
                    );
                    insertRecruiter(recr);
                } else {
                    String errorToast = createEmptyFieldToastMessage(emptyFields);
                    Toast.makeText(RecrProfPrefActivity.this, errorToast, Toast.LENGTH_SHORT).show();
                    dialog.hide();
                }
            }
        });
    }

    private String createEmptyFieldToastMessage(ArrayList<Integer> emptyFields) {
        String error = "The following fields are empty: ";
        for(Integer index : emptyFields){
            error += getFieldName(index);
            error += ", ";
        }
        return error.substring(0,error.length() -2 ) + ".";
    }

    private String getFieldName(Integer index) {
        switch (index){
            case 1: return "Location";
            case 2: return "Scope";
            case 3: return "Education";
            case 4: return "First Skill";
            case 5: return "Second Skill";
            case 6: return "Third Skill";
            case 7: return "Description";
            default: return null;
        }
    }

    private ArrayList<Integer> checkAllEditTextsForEmptyStrings() {
        ArrayList<Integer> emptyFields = new ArrayList<>();
        if(isEmpty(mLocationText)) emptyFields.add(1);
        if(isEmpty(mScopeText)) emptyFields.add(2);
        if(isEmpty(mEducationText)) emptyFields.add(3);
        if(isEmpty(mFirstSkillText)) emptyFields.add(4);
        if(isEmpty(mSecondSkillText)) emptyFields.add(5);
        if(isEmpty(mThirdSkillText)) emptyFields.add(6);
        if(isEmpty(mDescriptionText)) emptyFields.add(7);
        return emptyFields;

    }

    void insertRecruiter(Recruiter recruiter) {
//        Map<String, Object> candidateMapData = new HashMap<>();
//        candidateMapData.put(AGE_KEY, candidate.getAge());
//        candidateMapData.put(EMAIL_KEY, candidate.getEmail());
//        candidateMapData.put(JOB_CATEGORY_KEY, candidate.getJobCategory());
//        candidateMapData.put(JOB_LOCATION_KEY, candidate.getJobLocation());
//        candidateMapData.put(LAST_NAME_KEY, candidate.getLastName());
//        candidateMapData.put(MORE_INFO_KEY, candidate.getMoreInfo());
//        candidateMapData.put(NAME_KEY, candidate.getName());
//        candidateMapData.put(SCOPE_KEY, candidate.getScope());
//        candidateMapData.put(SKILLS_KEY, candidate.getSkillsList());
//        candidateMapData.put(EDUCATION_KEY, candidate.getEducation());

        WriteBatch batch = db.batch();

        DocumentReference candidateDocumentReference = recruitersCollection.document(recruiter.getEmail());
        batch.set(candidateDocumentReference, recruiter);

        Map<String, Object> userMapData = new HashMap<>();
        userMapData.put(EMAIL_KEY, recruiter.getEmail());


        DocumentReference userDocumentReference = usersCollection.document(recruiter.getEmail());
        batch.set(userDocumentReference, userMapData);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.hide();
                Toast.makeText(RecrProfPrefActivity.this, "ADDED RECR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initiateCategories() {
        jobCategories.add("Accounting");
        jobCategories.add("Computer Science");
        jobCategories.add("Education");
        jobCategories.add("Finance");
        jobCategories.add("IT");
        jobCategories.add("Media");
        jobCategories.add("Sales");

    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }



}
