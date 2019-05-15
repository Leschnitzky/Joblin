package com.technion.android.joblin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.EMAIL_KEY;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

public class CandProfPrefActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    final Calendar myCalendar = Calendar.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<String> jobCategories = new ArrayList<>();
    Spinner mJobCategorySpinner;
    EditText mDateText;
    EditText mScopeText;
    EditText mLocationText;
    EditText mEducationText;
    EditText mFirstSkillText;
    EditText mSecondSkillText;
    EditText mThirdSkillText;

    EditText mDescriptionText;

    Button mSubmitButton;


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private String getStrinForEditText(Calendar myCalendar){
        return Integer.valueOf(myCalendar.get(myCalendar.DAY_OF_MONTH)).toString() + "/" +
                Integer.valueOf(myCalendar.get(myCalendar.MONTH)).toString() + "/" +
                Integer.valueOf(myCalendar.get(myCalendar.YEAR)).toString();
    }

    private void updateLabel() {

        mDateText.setText(getStrinForEditText(myCalendar));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initiateCategories();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cand_prof_pref);

        mDateText = findViewById(R.id.date_of_birth);
        mLocationText = findViewById(R.id.job_location_cand);
        mScopeText = findViewById(R.id.scope_cand);
        mDescriptionText = findViewById(R.id.description_cand);
        mFirstSkillText = findViewById(R.id.first_skill_cand);
        mSecondSkillText = findViewById(R.id.second_skill_cand);
        mThirdSkillText = findViewById(R.id.third_skill_cand);

        mSubmitButton = findViewById(R.id.submit_button_cand);

        mJobCategorySpinner = findViewById(R.id.category_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.
                support_simple_spinner_dropdown_item,jobCategories);
        mJobCategorySpinner.setAdapter(adapter);

        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CandProfPrefActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Check all fields
                Candidate cand = new Candidate(
                        mAuth.getCurrentUser().getEmail(),
                        getIntent().getStringExtra(LoginActivity.FIRST_NAME_KEY),
                        getIntent().getStringExtra(LoginActivity.LAST_NAME_KEY),
                        getIntent().getStringExtra(LoginActivity.URI_KEY),
               //         new Timestamp(myCalendar.getTime()),
                        7,
                        mLocationText.getText().toString(),
                        mScopeText.getText().toString(),
                        mEducationText.getText().toString(),
                        Arrays.asList(mFirstSkillText.getText().toString(),
                                mSecondSkillText.getText().toString(),
                                mThirdSkillText.getText().toString()),
                        mDescriptionText.getText().toString(),
                        mJobCategorySpinner.getSelectedItem().toString()
                );
                insertCandidate(cand);
            }
        });
    }

    void insertCandidate(Candidate candidate) {

        WriteBatch batch = db.batch();

        DocumentReference candidateDocumentReference = candidatesCollection.document(candidate.getEmail());
        batch.set(candidateDocumentReference, candidate);

        Map<String, Object> userMapData = new HashMap<>();
        userMapData.put(EMAIL_KEY, candidate.getEmail());

        DocumentReference userDocumentReference = usersCollection.document(candidate.getEmail());
        batch.set(userDocumentReference, userMapData);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CandProfPrefActivity.this, "ADDED CANDIDATE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CandProfPrefActivity.this,LoginActivity.class);
                startActivity(intent);
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


}
