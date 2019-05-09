package com.technion.android.joblin;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


class DatabaseAPI {

    class communicationExceptionWithDB extends Exception {}

    private final String AGE_KEY = "age";
    private final String EMAIL_KEY = "email";
    private final String JOB_CATEGORY_KEY = "job category";
    private final String JOB_LOCATION_KEY = "job location";
    private final String LAST_NAME_KEY = "last name";
    private final String MORE_INFO_KEY = "more info";
    private final String NAME_KEY = "name";
    private final String SCOPE_KEY = "scope";
    private final String SKILLS_KEY = "skills";
    private final String EDUCATION_KEY = "education";

    private FirebaseFirestore db;
    private static final String TAG = "DatabaseAPI";
    private CollectionReference candidatesCollection;
    private CollectionReference recruitersCollection;

    private boolean isUserInDatabase = false;
    private boolean communicationProblem = false;

    DatabaseAPI() {
        db = FirebaseFirestore.getInstance();
        String candidatesCollectionName = "candidates";
        String recruitersCollectionName = "recruiters";
        candidatesCollection = db.collection(candidatesCollectionName);
        recruitersCollection = db.collection(recruitersCollectionName);
    }

    boolean isUserInTheDB(String email) throws communicationExceptionWithDB {
        if(isCandidateInDB(email)) {
            return true;
        }

        return isRecruiterInDB(email);
    }

    private boolean isCandidateInDB(final String email) throws communicationExceptionWithDB {
        candidatesCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        String currentEmail = (String) document.get("email");
                        if(currentEmail.equals(email)) {
                            isUserInDatabase = true;
                            return;
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    communicationProblem = true;
                }
            }
        });

        if(communicationProblem) {
            communicationProblem = false;
            throw new communicationExceptionWithDB();
        }

        if(isUserInDatabase) {
            isUserInDatabase = false;
            return true;
        }

        return false;
    }

    private boolean isRecruiterInDB(final String email) throws communicationExceptionWithDB {
        recruitersCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        String currentEmail = (String) document.get("email");
                        if(currentEmail.equals(email)) {
                            isUserInDatabase = true;
                            return;
                        }
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                    communicationProblem = true;
                }
            }
        });

        if(communicationProblem) {
            communicationProblem = false;
            throw new communicationExceptionWithDB();
        }

        if(isUserInDatabase) {
            isUserInDatabase = false;
            return true;
        }

        return false;
    }

    void insertCandidate(Candidate candidate) throws communicationExceptionWithDB {
        Map<String, Object> candidateMapData = new HashMap<>();
        candidateMapData.put(AGE_KEY, candidate.getAge());
        candidateMapData.put(EMAIL_KEY, candidate.getEmail());
        candidateMapData.put(JOB_CATEGORY_KEY, candidate.getJobCategory());
        candidateMapData.put(JOB_LOCATION_KEY, candidate.getJobLocation());
        candidateMapData.put(LAST_NAME_KEY, candidate.getLastName());
        candidateMapData.put(MORE_INFO_KEY, candidate.getMoreInfo());
        candidateMapData.put(NAME_KEY, candidate.getName());
        candidateMapData.put(SCOPE_KEY, candidate.getScope());
        candidateMapData.put(SKILLS_KEY, candidate.getSkillsList());
        candidateMapData.put(EDUCATION_KEY, candidate.getEducation());

        candidatesCollection.document(candidate.getEmail())
                .set(candidateMapData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        communicationProblem = true;
                    }
                });

        if(communicationProblem) {
            communicationProblem = false;
            throw new communicationExceptionWithDB();
        }
    }


}
