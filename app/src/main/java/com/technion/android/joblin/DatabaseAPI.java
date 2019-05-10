package com.technion.android.joblin;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class DatabaseAPI {

    class communicationExceptionWithDB extends Exception {}
    class recruiterDoesNotExists extends Exception {}
    class candidateDoesNotExists extends Exception {}

    enum Side {
        LEFT, RIGHT;
    }

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
    private final String REQUIRED_AGE_KEY = "required age";
    private final String REQUIRED_SCOPE_KEY = "required scope";
    private final String JOB_DESCRIPTION_KEY = "job description";

    private final String SWIPES_KEY = "swipes";
    private final String SIDE_KEY = "side";

    private FirebaseFirestore db;
    private static final String TAG = "DatabaseAPI";
    private CollectionReference candidatesCollection;
    private CollectionReference recruitersCollection;
    private CollectionReference jobCategoriesCollection;

    private boolean isUserInDatabase = false;
    private boolean communicationProblem = false;

    DatabaseAPI() {
        db = FirebaseFirestore.getInstance();
        String candidatesCollectionName = "candidates";
        String recruitersCollectionName = "recruiters";
        String jobCategoriesCollectionName = "job categories";
        candidatesCollection = db.collection(candidatesCollectionName);
        recruitersCollection = db.collection(recruitersCollectionName);
        jobCategoriesCollection = db.collection(jobCategoriesCollectionName);
    }

    boolean isUserInTheDB(String email) throws communicationExceptionWithDB {
        if(isCandidateInDB(email)) {
            return true;
        }

        return isRecruiterInDB(email);
    }

    boolean isCandidateInDB(final String email) throws communicationExceptionWithDB {
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
                Log.e(TAG, "Error getting documents.", task.getException());
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

    boolean isRecruiterInDB(final String email) throws communicationExceptionWithDB {
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
                Log.e(TAG, "Error getting documents.", task.getException());
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
            Log.e(TAG, "Error writing document", e);
            communicationProblem = true;
            }
        });

        if(communicationProblem) {
            communicationProblem = false;
            throw new communicationExceptionWithDB();
        }
    }

    void insertRecruiter(Recruiter recruiter) throws communicationExceptionWithDB {
        Map<String, Object> recruiterMapData = new HashMap<>();
        recruiterMapData.put(EMAIL_KEY, recruiter.getEmail());
        recruiterMapData.put(NAME_KEY, recruiter.getName());
        recruiterMapData.put(LAST_NAME_KEY, recruiter.getLastName());
        recruiterMapData.put(JOB_CATEGORY_KEY, recruiter.getJobCategory());
        recruiterMapData.put(JOB_LOCATION_KEY, recruiter.getJobLocation());
        recruiterMapData.put(REQUIRED_AGE_KEY, recruiter.getRequiredAge());
        recruiterMapData.put(REQUIRED_SCOPE_KEY, recruiter.getRequiredScope());
        recruiterMapData.put(JOB_DESCRIPTION_KEY, recruiter.getJobDescription());

        recruitersCollection.document(recruiter.getEmail())
        .set(recruiterMapData)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error writing document", e);
                communicationProblem = true;
            }
        });

        if(communicationProblem) {
            communicationProblem = false;
            throw new communicationExceptionWithDB();
        }
    }

    void insertJobCategories() throws communicationExceptionWithDB {
        List<String> jobCategories = new ArrayList<>();
        jobCategories.add("Accounting");
        jobCategories.add("Computer Science");
        jobCategories.add("Education");
        jobCategories.add("Finance");
        jobCategories.add("IT");
        jobCategories.add("Media");
        jobCategories.add("Sales");

        Map<String, Object> jobCategoryMapData = new HashMap<>();
        for(String category : jobCategories) {
            jobCategoryMapData.clear();
            jobCategoryMapData.put(JOB_CATEGORY_KEY, category);
            jobCategoriesCollection.document(category)
            .set(jobCategoryMapData)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error writing document", e);
                communicationProblem = true;
                }
            });

            if(communicationProblem) {
                communicationProblem = false;
                throw new communicationExceptionWithDB();
            }
        }
    }

    void insertSampleCandidate() throws communicationExceptionWithDB {
        List<String> skillsList = new ArrayList<>(Arrays.asList("Java", "C++"));

        Candidate candidate = new Candidate(
                "levi.weis3@gmail.com",
                "Levi",
                "Weiss",
                25,
                "Haifa",
                40,
                "Technion",
                skillsList,
                "I like building Android apps",
                "Computer Science");

        insertCandidate(candidate);
    }

    void insertSampleRecruiter() throws communicationExceptionWithDB {

        Recruiter recruiter = new Recruiter(
                "levi.weiss@gmail.com",
                "Levi",
                "Weiss",
                "Computer Science",
                26,
                60,
                "Tel Aviv",
                "Building apps for android");

        insertRecruiter(recruiter);
    }

    void addSwipeDataForRecruiter(String recruiterMail, String candidateMail, Side side)
            throws communicationExceptionWithDB {

        String sideString;
        if(side == Side.RIGHT) {
            sideString = "right";
        } else {
            sideString = "left";
        }

        Map<String, Object> recruiterSwipesMapData = new HashMap<>();
        recruiterSwipesMapData.put(EMAIL_KEY, candidateMail);
        recruiterSwipesMapData.put(SIDE_KEY, sideString);

        recruitersCollection.document(recruiterMail).collection(SWIPES_KEY).document(candidateMail)
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
            communicationProblem = true;
            }
        });

        if(communicationProblem) {
            communicationProblem = false;
            throw new communicationExceptionWithDB();
        }
    }

    void addSwipeDataForCandidate(String candidateMail, String recruiterMail, Side side)
            throws communicationExceptionWithDB {

        String sideString;
        if(side == Side.RIGHT) {
            sideString = "right";
        } else {
            sideString = "left";
        }

        Map<String, Object> candidateSwipesMapData = new HashMap<>();
        candidateSwipesMapData.put(EMAIL_KEY, candidateMail);
        candidateSwipesMapData.put(SIDE_KEY, sideString);

        candidatesCollection.document(candidateMail).collection(SWIPES_KEY).document(recruiterMail)
        .set(candidateSwipesMapData)
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
            communicationProblem = true;
            }
        });

        if(communicationProblem) {
            communicationProblem = false;
            throw new communicationExceptionWithDB();
        }
    }

    public void initializeDBWithSomeData() throws communicationExceptionWithDB {
        initializeDBWithRecruiters();
        initializeDBWithCandidates();
        initializeDBWithSwipes();
        insertJobCategories();
    }

    public void initializeDBWithRecruiters() throws communicationExceptionWithDB {
        Recruiter recruiter;

        recruiter = new Recruiter("gre4f@gmail.com","Gregory", "Weiss",
                "Computer Science", 36, 60,
                "Tel Aviv", "Building apps for android");

        insertRecruiter(recruiter);

        recruiter = new Recruiter("si7s@gmail.com","Simha", "Golan",
                "Media", 16, 100,
                "Ramat Gan", "Speaking with people about arts");

        insertRecruiter(recruiter);

        recruiter = new Recruiter("john3@gmail.com","John", "Buka",
                "IT", 20, 50,
                "Eilat", "Configuration of IT services in windows");

        insertRecruiter(recruiter);

        recruiter = new Recruiter("bar@gmail.com","Bar", "Jim",
                "Accountant", 40, 100,
                "Ashdod", "Accounting for a big company");

        insertRecruiter(recruiter);
    }

    public void initializeDBWithCandidates() throws communicationExceptionWithDB {
        Candidate candidate;
        List<String> skillsList;

        skillsList = new ArrayList<>(Arrays.asList("Java", "C++"));
        candidate = new Candidate(
                "levi.weiss3@gmail.com",  "Levi",  "Weiss",
                25, "Haifa",  40, "Technion",
                skillsList, "I like building Android apps",
                "Computer Science");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Bash", "TCSH"));
        candidate = new Candidate(
                "diego@gmail.com",  "Diego",  "Maradona",
                33, "Eilat",  40, "Tel Aviv University",
                skillsList, "I am best person for cyber security",
                "IT");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Bash", "Windows"));
        candidate = new Candidate(
                "macho@gmail.com",  "Macho",  "Pacho",
                33, "Jaffa",  40, "Ben Gurion University",
                skillsList, "I am friendly personal",
                "IT");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Team work", "Tax professional"));
        candidate = new Candidate(
                "asaf@gmail.com",  "Asaf",  "Granit",
                39, "Jaffa",  100, "IDC",
                skillsList, "I am looking to work in dynamic place with good people",
                "Accounting");

        insertCandidate(candidate);

    }

    public void initializeDBWithSwipes() throws communicationExceptionWithDB {

    }

}
