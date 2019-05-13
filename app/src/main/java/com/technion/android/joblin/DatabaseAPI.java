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


class DatabaseAPI {

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




    void isUserInTheDB(final String email) {
        DocumentReference docRef = usersCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    void isCandidateInDB(final String email) {
        DocumentReference docRef = candidatesCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
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
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    void insertCandidate(Candidate candidate) {
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

        WriteBatch batch = db.batch();

        DocumentReference candidateDocumentReference = candidatesCollection.document(candidate.getEmail());
        batch.set(candidateDocumentReference, candidateMapData);

        Map<String, Object> userMapData = new HashMap<>();
        userMapData.put(EMAIL_KEY, candidate.getEmail());

        DocumentReference userDocumentReference = usersCollection.document(candidate.getEmail());
        batch.set(userDocumentReference, userMapData);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    void insertRecruiter(Recruiter recruiter) {
        Map<String, Object> recruiterMapData = new HashMap<>();
        recruiterMapData.put(EMAIL_KEY, recruiter.getEmail());
        recruiterMapData.put(NAME_KEY, recruiter.getName());
        recruiterMapData.put(LAST_NAME_KEY, recruiter.getLastName());
        recruiterMapData.put(JOB_CATEGORY_KEY, recruiter.getJobCategory());
        recruiterMapData.put(JOB_LOCATION_KEY, recruiter.getJobLocation());
        recruiterMapData.put(REQUIRED_SCOPE_KEY, recruiter.getRequiredScope());
        recruiterMapData.put(REQUIRED_SKILLS_KEY, recruiter.getRequiredSkillsList());
        recruiterMapData.put(REQUIRED_EDUCATION, recruiter.getRequiredEducation());
        recruiterMapData.put(JOB_DESCRIPTION_KEY, recruiter.getJobDescription());

        WriteBatch batch = db.batch();

        DocumentReference recruiterDocumentReference = recruitersCollection.document(recruiter.getEmail());
        batch.set(recruiterDocumentReference, recruiterMapData);

        Map<String, Object> userMapData = new HashMap<>();
        userMapData.put(EMAIL_KEY, recruiter.getEmail());

        DocumentReference userDocumentReference = usersCollection.document(recruiter.getEmail());
        batch.set(userDocumentReference, userMapData);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    void insertJobCategories() {

        List<String> jobCategories = new ArrayList<>();
        jobCategories.add("Accounting");
        jobCategories.add("Computer Science");
        jobCategories.add("Education");
        jobCategories.add("Finance");
        jobCategories.add("IT");
        jobCategories.add("Media");
        jobCategories.add("Sales");

        WriteBatch batch = db.batch();
        DocumentReference jobCategoryDocumentReference;
        Map<String, Object> jobCategoryMapData = new HashMap<>();

        for(String category : jobCategories) {
            jobCategoryDocumentReference = jobCategoriesCollection.document(category);
            jobCategoryMapData.clear();
            jobCategoryMapData.put(JOB_CATEGORY_KEY, category);
            batch.set(jobCategoryDocumentReference, jobCategoryMapData);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    void insertSampleCandidate() {
        List<String> skillsList = new ArrayList<>(Arrays.asList("Java", "C++"));

        Candidate candidate = new Candidate(
                "levi.weis3@gmail.com",
                "Levi",
                "Weiss",
                25,
                "Haifa",
                "twice a week",
                "Technion",
                skillsList,
                "I like building Android apps",
                "Computer Science");

        insertCandidate(candidate);
    }

    void insertSampleRecruiter() {
        List<String> skillsList = new ArrayList<>(Arrays.asList("Java", "C++"));

        Recruiter recruiter = new Recruiter(
                "levi.weiss@gmail.com",
                "Levi",
                "Weiss",
                "Computer Science",
                "twice a week",
                "Tel Aviv",
                "Building apps for android",
                "CS",
                skillsList);

        insertRecruiter(recruiter);
    }

    void addSwipeDataForRecruiter(String recruiterMail, String candidateMail, Side side) {
        addSwipeData(recruitersCollection, candidatesCollection, recruiterMail, candidateMail, side);
    }

    void addSwipeDataForCandidate(String candidateMail, String recruiterMail, Side side) {
        addSwipeData(candidatesCollection, recruitersCollection, candidateMail, recruiterMail, side);
    }

    void addSwipeData(CollectionReference firstCollection,
                      CollectionReference secondCollection,
                      String firstMail,
                      String secondMail,
                      Side side) {

        String sideString;
        if(side == Side.RIGHT) {
            sideString = "right";
        } else {
            sideString = "left";
        }

        final Map<String, Object> recruiterSwipesMapData = new HashMap<>();
        recruiterSwipesMapData.put(EMAIL_KEY, secondMail);
        recruiterSwipesMapData.put(SIDE_KEY, sideString);

        final Map<String, Object> candidateSwipesMapData = new HashMap<>();

        final Map<String, Object> recruiterMatchesMapData = new HashMap<>();
        recruiterMatchesMapData.put(EMAIL_KEY, firstMail);

        final Map<String, Object> candidateMatchesMapData = new HashMap<>();
        candidateMatchesMapData.put(EMAIL_KEY, secondMail);

        if(side == Side.RIGHT) {
            final DocumentReference swipeDocRefOfRecruiter = firstCollection.document(firstMail).collection(SWIPES_KEY).document(secondMail);
            final DocumentReference swipeDocRefOfCandidate = secondCollection.document(secondMail).collection(SWIPES_KEY).document(firstMail);
            final DocumentReference matchDocRefOfRecruiter = firstCollection.document(firstMail).collection(MATCHES_KEY).document(secondMail);
            final DocumentReference matchDocRefOfCandidate = secondCollection.document(secondMail).collection(MATCHES_KEY).document(firstMail);
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshotSwipeCandidate = transaction.get(swipeDocRefOfCandidate);
                    DocumentSnapshot snapshotSwipeRecruiter = transaction.get(swipeDocRefOfRecruiter);
                    if(snapshotSwipeCandidate.exists()) {
                        if(snapshotSwipeCandidate.get(SIDE_KEY).equals("right")) {
                            transaction.set(matchDocRefOfRecruiter, candidateMatchesMapData);
                            transaction.set(matchDocRefOfCandidate, recruiterMatchesMapData);
                        }
                        transaction.update(swipeDocRefOfCandidate, candidateSwipesMapData);
                    } else {
                        transaction.set(swipeDocRefOfCandidate, candidateSwipesMapData);
                    }

                    if(snapshotSwipeRecruiter.exists()) {
                        transaction.update(swipeDocRefOfRecruiter, recruiterSwipesMapData);
                    } else {
                        transaction.set(swipeDocRefOfRecruiter, recruiterSwipesMapData);
                    }

                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Transaction success!");

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                }
            });
        } else {
            firstCollection.document(firstMail).collection(SWIPES_KEY).document(secondMail)
                    .set(recruiterSwipesMapData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }

    }

    public void initializeDBWithSomeData() {
        initializeDBWithRecruiters();
        initializeDBWithCandidates();
        initializeDBWithSwipes();
        insertJobCategories();
    }

//    public void initializeDBWithUsers() {
//        insertUser("gre4f@gmail.com");
//        insertUser("si7s@gmail.com");
//        insertUser("john3@gmail.com");
//        insertUser("bar@gmail.com");
//        insertUser("levi.weiss3@gmail.com");
//        insertUser("diego@gmail.com");
//        insertUser("macho@gmail.com");
//        insertUser("asaf@gmail.com");
//    }

    public void initializeDBWithRecruiters() {
        Recruiter recruiter;
        List<String> skillsList;

        skillsList = new ArrayList<>(Arrays.asList("Java", "C++"));
        recruiter = new Recruiter("gre4f@gmail.com",
                "Gregory",
                "Weiss",
                "Computer Science",
                "Three times a week",
                "Tel Aviv",
                "Building apps for android",
                "High School graduate",
                skillsList);

        insertRecruiter(recruiter);

        recruiter = new Recruiter("si7s@gmail.com",
                "Simha",
                "Golan",
                "Media",
                "Three times a week",
                "Ramat Gan",
                "Speaking with people about arts",
                "PHD",
                skillsList);

        insertRecruiter(recruiter);

        recruiter = new Recruiter("john3@gmail.com",
                "John",
                "Buka",
                "IT",
                "Three times a week",
                "Eilat",
                "Configuration of IT services in windows",
                "None",
                skillsList);

        insertRecruiter(recruiter);

        recruiter = new Recruiter("bar@gmail.com",
                "Bar",
                "Jim",
                "Accountant",
                "Three times a week",
                "Ashdod",
                "Accounting for a big company",
                "Graduate in Accounting",
                skillsList);

        insertRecruiter(recruiter);
    }

    public void initializeDBWithCandidates() {
        Candidate candidate;
        List<String> skillsList;

        skillsList = new ArrayList<>(Arrays.asList("Java", "C++"));
        candidate = new Candidate(
                "levi.weiss3@gmail.com",  "Levi",  "Weiss",
                25, "Haifa",  "40%", "Technion",
                skillsList, "I like building Android apps",
                "Computer Science");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Bash", "TCSH"));
        candidate = new Candidate(
                "diego@gmail.com",  "Diego",  "Maradona",
                33, "Eilat",  "Three times a week", "Tel Aviv University",
                skillsList, "I am best person for cyber security",
                "IT");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Bash", "Windows"));
        candidate = new Candidate(
                "macho@gmail.com",  "Macho",  "Pacho",
                33, "Jaffa",  "Three times a week", "Ben Gurion University",
                skillsList, "I am friendly personal",
                "IT");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Team work", "Tax professional"));
        candidate = new Candidate(
                "asaf@gmail.com",  "Asaf",  "Granit",
                39, "Jaffa",  "Three times a week", "IDC",
                skillsList, "I am looking to work in dynamic place with good people",
                "Accounting");

        insertCandidate(candidate);
    }

    public void initializeDBWithSwipes() {
        addSwipeDataForRecruiter("gre4f@gmail.com", "levi.weiss3@gmail.com", Side.RIGHT);
        addSwipeDataForRecruiter("john3@gmail.com", "macho@gmail.com", Side.LEFT);
        addSwipeDataForRecruiter("bar@gmail.com", "asaf@gmail.com", Side.RIGHT);
        addSwipeDataForCandidate("levi.weiss3@gmail.com", "gre4f@gmail.com", Side.RIGHT);
        addSwipeDataForCandidate("macho@gmail.com", "john3@gmail.com", Side.RIGHT);
        addSwipeDataForCandidate("asaf@gmail.com", "bar@gmail.com", Side.RIGHT);
    }

}
