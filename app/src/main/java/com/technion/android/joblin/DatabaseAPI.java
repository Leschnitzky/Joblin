package com.technion.android.joblin;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DatabaseAPI {
    FirebaseFirestore db;
    private static final String TAG = "DatabaseAPI";


    public DatabaseAPI() {
        this.db = FirebaseFirestore.getInstance();
        String candidatesCollectionName = "candidates";
        CollectionReference candidatesCollection = db.collection(candidatesCollectionName);
        String recruitersCollectionName = "recruiters";
        CollectionReference recruitersCollection = db.collection(recruitersCollectionName);
    }



}
