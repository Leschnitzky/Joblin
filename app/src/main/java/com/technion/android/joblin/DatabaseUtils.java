package com.technion.android.joblin;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseUtils {
    enum Side {
        LEFT, RIGHT
    }

    private static final String AGE_KEY = "age";
    private static final String EMAIL_KEY = "email";
    private static final String JOB_CATEGORY_KEY = "job category";
    private static final String JOB_LOCATION_KEY = "job location";
    private static final String LAST_NAME_KEY = "last name";
    private static final String MORE_INFO_KEY = "more info";
    private static final String NAME_KEY = "name";
    private static final String SCOPE_KEY = "scope";
    private static final String SKILLS_KEY = "skills";
    private static final String EDUCATION_KEY = "education";
    private static final String REQUIRED_EDUCATION = "required education";
    private static final String REQUIRED_SCOPE_KEY = "required scope";
    private static final String REQUIRED_SKILLS_KEY = "required skills";
    private static final String JOB_DESCRIPTION_KEY = "job description";

    private static final String SWIPES_KEY = "swipes";
    private static final String SIDE_KEY = "side";
    private static final String MATCHES_KEY = "matches";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "DatabaseAPI";

    private static final String candidatesCollectionName = "candidates";
    private CollectionReference candidatesCollection = db.collection(candidatesCollectionName);

    private static final String recruitersCollectionName = "recruiters";
    private CollectionReference recruitersCollection = db.collection(recruitersCollectionName);

    private static final String jobCategoriesCollectionName = "job categories";
    private CollectionReference jobCategoriesCollection = db.collection(jobCategoriesCollectionName);

    private static final String usersCollectionName = "users";
    private CollectionReference usersCollection = db.collection(usersCollectionName);
}
