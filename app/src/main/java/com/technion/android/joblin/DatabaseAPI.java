package com.technion.android.joblin;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class DatabaseAPI {

    enum Side {
        LEFT, RIGHT
    }

    static final String BIRTHDAY_KEY = "birthday";
    static final String EMAIL_KEY = "email";
    static final String JOB_CATEGORY_KEY = "jobCategory";
    static final String JOB_LOCATION_KEY = "jobLocation";
    static final String LAST_NAME_KEY = "lastName";
    static final String MORE_INFO_KEY = "moreInfo";
    static final String NAME_KEY = "name";
    static final String SCOPE_KEY = "scope";
    static final String SKILLS_KEY = "skillList";
    static final String EDUCATION_KEY = "education";
    static final String NUMBER_OF_SWIPES_LEFT_KEY = "numberOfSwipesLeft";
    static final String REQUIRED_EDUCATION = "requiredEducation";
    static final String REQUIRED_SCOPE_KEY = "requiredScope";
    static final String REQUIRED_SKILLS_KEY = "requiredSkillsList";
    static final String JOB_DESCRIPTION_KEY = "jobDescription";
    static final String SIDE_KEY = "side";
    static final String IMAGE_URL_KEY = "imageUrl";

    static final String SWIPES_COLLECTION_NAME = "Swipes";
    static final String MATCHES_COLLECTION_NAME = "Matches";
    static final String CANDIDATES_COLLECTION_NAME = "Candidates";
    static final String RECRUITERS_COLLECTION_NAME = "Recruiters";
    static final String JOB_CATEGORIES_COLLECTION_NAME = "JobCategories";
    static final String USERS_COLLECTION_NAME = "Users";

    private static final String TAG = "DatabaseAPI";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);


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

    void getCandidate(final String email) {
        DocumentReference docRef = candidatesCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Candidate candidate = document.toObject(Candidate.class);


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
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
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Recruiter recruiter = document.toObject(Recruiter.class);


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
//        Map<String, Object> candidateMapData = new HashMap<>();
//        candidateMapData.put(BIRTHDAY_KEY, candidate.getBirthday());
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

        DocumentReference candidateDocumentReference = candidatesCollection.document(candidate.getEmail());
        batch.set(candidateDocumentReference, candidate);

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
//        Map<String, Object> recruiterMapData = new HashMap<>();
//        recruiterMapData.put(EMAIL_KEY, recruiter.getEmail());
//        recruiterMapData.put(NAME_KEY, recruiter.getName());
//        recruiterMapData.put(LAST_NAME_KEY, recruiter.getLastName());
//        recruiterMapData.put(JOB_CATEGORY_KEY, recruiter.getJobCategory());
//        recruiterMapData.put(JOB_LOCATION_KEY, recruiter.getJobLocation());
//        recruiterMapData.put(REQUIRED_SCOPE_KEY, recruiter.getRequiredScope());
//        recruiterMapData.put(REQUIRED_SKILLS_KEY, recruiter.getRequiredSkillsList());
//        recruiterMapData.put(REQUIRED_EDUCATION, recruiter.getRequiredEducation());
//        recruiterMapData.put(JOB_DESCRIPTION_KEY, recruiter.getJobDescription());

        WriteBatch batch = db.batch();

        DocumentReference recruiterDocumentReference = recruitersCollection.document(recruiter.getEmail());
        batch.set(recruiterDocumentReference, recruiter);

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

    void insertSampleCandidate() {
        List<String> skillsList = new ArrayList<>(Arrays.asList("Java", "C++"));

        Candidate candidate = new Candidate(
                "levi.weis3@gmail.com",
                "Levi",
                "Weiss",
                "http://image",
                new Timestamp(new Date(96,10,5)),
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
                "http://image",
                "Levis",
                "Computer Science",
                "twice a week",
                "Tel Aviv",
                "Building apps for android",
                "CS",
                skillsList);

        insertRecruiter(recruiter);
    }

    void recruiterDoSwipe(final String recruiterMail, final String candidateMail, final Side side) {
        DocumentReference docRef = recruitersCollection.document(recruiterMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Recruiter recruiter = document.toObject(Recruiter.class);
                        if((side == Side.RIGHT) && (recruiter.getNumberOfSwipesLeft() == 0)) {
                            Log.d(TAG, "number of swipes is 0");
                            //TODO: message for no more swipes
                        } else {
                            addSwipeDataForRecruiter(recruiterMail, candidateMail, side);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    //TODO: add general toast for failure.
                }
            }
        });
    }

    void candidateDoSwipe(final String candidateMail, final String recruiterMail, final Side side) {

        DocumentReference docRef = candidatesCollection.document(candidateMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Candidate candidate = document.toObject(Candidate.class);
                        if((side == Side.RIGHT) && (candidate.getNumberOfSwipesLeft() == 0)) {
                            Log.d(TAG, "number of swipes is 0");
                            //TODO: message for no more swipes
                        } else {
                            addSwipeDataForCandidate(candidateMail, recruiterMail, side);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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

        final Map<String, Object> firstSwipesMapData = new HashMap<>();
        firstSwipesMapData.put(EMAIL_KEY, secondMail);
        firstSwipesMapData.put(SIDE_KEY, sideString);

        final Map<String, Object> secondSwipesMapData = new HashMap<>();

        final Map<String, Object> firstMatchesMapData = new HashMap<>();
        firstMatchesMapData.put(EMAIL_KEY, firstMail);

        final Map<String, Object> secondMatchesMapData = new HashMap<>();
        secondMatchesMapData.put(EMAIL_KEY, secondMail);

        if(side == Side.RIGHT) {
            final DocumentReference mainDocRefOfFirst = firstCollection.document(firstMail);
            final DocumentReference swipeDocRefOfFirst = firstCollection.document(firstMail).collection(SWIPES_COLLECTION_NAME).document(secondMail);
            final DocumentReference swipeDocRefOfSecond = secondCollection.document(secondMail).collection(SWIPES_COLLECTION_NAME).document(firstMail);
            final DocumentReference matchDocRefOfFirst = firstCollection.document(firstMail).collection(MATCHES_COLLECTION_NAME).document(secondMail);
            final DocumentReference matchDocRefOfSecond = secondCollection.document(secondMail).collection(MATCHES_COLLECTION_NAME).document(firstMail);
            db.runTransaction(new Transaction.Function<Boolean>() {
                @Override
                public Boolean apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshotMainFirst = transaction.get(mainDocRefOfFirst);
                    DocumentSnapshot snapshotSwipeSecond = transaction.get(swipeDocRefOfSecond);
                    DocumentSnapshot snapshotSwipeFirst = transaction.get(swipeDocRefOfFirst);
                    boolean isMatch = false;
                    if(snapshotSwipeSecond.exists()) {
                        if(snapshotSwipeSecond.get(SIDE_KEY).equals("right")) {
                            transaction.set(matchDocRefOfFirst, secondMatchesMapData);
                            transaction.set(matchDocRefOfSecond, firstMatchesMapData);
                            isMatch = true;
                        }
                        transaction.update(swipeDocRefOfSecond, secondSwipesMapData);
                    } else {
                        transaction.set(swipeDocRefOfSecond, secondSwipesMapData);
                        transaction.delete(swipeDocRefOfSecond);
                    }

                    if(snapshotSwipeFirst.exists()) {
                        transaction.update(swipeDocRefOfFirst, firstSwipesMapData);
                    } else {
                        transaction.set(swipeDocRefOfFirst, firstSwipesMapData);
                    }

                    Log.d(TAG, "DocumentSnapshot data: " + snapshotMainFirst.getData());
                    if(snapshotMainFirst.exists()) {
                        long numberOfSwipesLeft = snapshotMainFirst.getLong(NUMBER_OF_SWIPES_LEFT_KEY);
                        transaction.update(mainDocRefOfFirst, NUMBER_OF_SWIPES_LEFT_KEY, numberOfSwipesLeft - 1);
                    }

                    return isMatch;
                }
            }).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean isMatch) {
                    Log.d(TAG, "Transaction success!");
                    if(isMatch) {
                        Log.d(TAG, "It is a match!");
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                    //TODO: add general toast for failure.
                }
            });
        } else {
            firstCollection.document(firstMail).collection(SWIPES_COLLECTION_NAME).document(secondMail)
                    .set(firstSwipesMapData)
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
                            //TODO: add general toast for failure.
                        }
                    });
        }

    }

    public void initializeDBWithSomeData() {
        initializeDBWithRecruiters();
        initializeDBWithCandidates();
        initializeDBWithSwipes();
        initializeJobCategories();
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
                "https://www.lafollette.wisc.edu/images/facultystaff/highres/Nemet-Gregory-2016.jpg",
                "Mama mia",
                "Computer Science",
                "Three times a week",
                "Tel Aviv",
                "Building apps for android",
                "High School graduate",
                skillsList);

        insertRecruiter(recruiter);

        recruiter = new Recruiter("limor@gmail.com",
                "Limor",
                "Snonit",
                "https://www.lafollette.wisc.edu/images/facultystaff/highres/Nemet-Gregory-2016.jpg",
                "Computer Science",
                "Goologolo",
                "Full time",
                "Haifa",
                "Building apps for ios",
                "Kindergarden",
                skillsList);

        insertRecruiter(recruiter);

        recruiter = new Recruiter("libat@gmail.com",
                "Libat",
                "Yogev",
                "https://www.lafollette.wisc.edu/images/facultystaff/highres/Nemet-Gregory-2016.jpg",
                "Intel",
                "Computer Science",
                "twice a week",
                "Carmiel",
                "it's a secret job that can only be known when you have the special skills",
                "Magic from Hogwarts",
                new ArrayList<String> (Arrays.asList("Java","C#","abra kadabra")));

        insertRecruiter(recruiter);

        recruiter = new Recruiter("si7s@gmail.com",
                "Simha",
                "Golan",
                "https://1qxya61uvyue18mpsx3zc8om-wpengine.netdna-ssl.com/wp-content/uploads/sites/2/2017/02/lola.jpg",
                "Photomap",
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
                "https://pbs.twimg.com/profile_images/972872769019850753/YTxFZF2x_400x400.jpg",
                "Megabob",
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
                "https://1qxya61uvyue18mpsx3zc8om-wpengine.netdna-ssl.com/wp-content/uploads/sites/2/2017/02/lola.jpg",
                "PCB",
                "Accounting",
                "Three times a week",
                "Ashdod",
                "Accounting for a big company",
                "Graduate in Accounting",
                skillsList);

        insertRecruiter(recruiter);

        recruiter = new Recruiter("Dani@gmail.com",
                "Dani",
                "Mizrahi",
                "https://1qxya61uvyue18mpsx3zc8om-wpengine.netdna-ssl.com/wp-content/uploads/sites/2/2017/02/lola.jpg",
                "Melanox",
                "Accounting",
                "Three times a week",
                "Eilat",
                "Accounting for a big company",
                "Graduate in Accounting",
                skillsList);

        insertRecruiter(recruiter);
    }

    public void initializeDBWithCandidates() {
        Candidate candidate;
        List<String> skillsList;

        skillsList = new ArrayList<>(Arrays.asList("Java", "C++"));

        candidate = new Candidate("gabi@gmail.com",
                "Gabi",
                "Gavrieli",
                "https://pbs.twimg.com/profile_images/972872769019850753/YTxFZF2x_400x400.jpg",
                new Timestamp(new Date(90,4,5)),
                "Tel Aviv",
                "Full Time",
                "High School",
                new ArrayList<String> (Arrays.asList("Java","C#")),
                "I know how to code, code is my life, more than anything in the world.",
                "Computer Science");

        insertCandidate(candidate);

        candidate = new Candidate("moriel@gmail.com",
                "Moriel",
                "Shlomiel",
                "https://media.npr.org/assets/img/2015/11/24/ajeup0ayctw4ztltklrnuvtm-y4xulezgneawbqw4cs_custom-7aa29347d5da230c6101168c71549a7399302d0c-s1100-c15.jpg",
                new Timestamp(new Date(2001,3,12)),
                "Beer Sheva",
                "20 hours per month",
                "Ort Barude",
                new ArrayList<String> (Arrays.asList("C","Assembly","Writing songs")),
                "Hello, it's me\n I was wondering if after all these years you'd like to meet",
                "Computer Science");

        insertCandidate(candidate);

        candidate = new Candidate("levi.weiss3@gmail.com",
                "Levi",
                "Weiss",
                "https://1qxya61uvyue18mpsx3zc8om-wpengine.netdna-ssl.com/wp-content/uploads/sites/2/2017/02/lola.jpg",
                new Timestamp(new Date(90,4,5)),
                "Haifa",
                "Twice a week",
                "Technion",
                skillsList,
                "I like building Android apps",
                "Computer Science");

        insertCandidate(candidate);

        candidate = new Candidate("caleb@walla.com",
                "Caleb",
                "Smorchi",
                "https://pbs.twimg.com/profile_images/972872769019850753/YTxFZF2x_400x400.jpg",
                new Timestamp(new Date(90,4,5)),
                "Salem",
                "Night Shifts",
                "Witches School of Salem",
                new ArrayList<String> (Arrays.asList("abra kadabra","whoofoo","escaping")),
                "I'm a witch and i'm proud of it. i can spell any programming language i want.",
                "Computer Science");

        insertCandidate(candidate);

        candidate = new Candidate("hotty@walla.com",
                "Hatich",
                "Esh megil shesh",
                "https://1qxya61uvyue18mpsx3zc8om-wpengine.netdna-ssl.com/wp-content/uploads/sites/2/2017/02/lola.jpg",
                new Timestamp(new Date(90,4,5)),
                "Jerusalem",
                "75% misra",
                "Haward Univerity",
                new ArrayList<String> (Arrays.asList("C#","angry birds")),
                "Only Iphones. If you make android, swipe left!",
                "Computer Science");

        insertCandidate(candidate);

        candidate = new Candidate("guy@walla.com",
                "Guy",
                "Menhel",
                "https://avatars1.githubusercontent.com/u/28152692?s=400&v=4",
                new Timestamp(new Date(90,4,5)),
                "Haifa",
                "Full Time",
                "Technion instituation",
                new ArrayList<String> (Arrays.asList("Java","Good at jokes")),
                "I have a great idea for an android app, it's called...\n 'remedicene'\n ~mind blown~",
                "Computer Science");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Bash", "TCSH"));
        candidate = new Candidate("diego@gmail.com",
                "Diego",
                "Maradona",
                "https://1qxya61uvyue18mpsx3zc8om-wpengine.netdna-ssl.com/wp-content/uploads/sites/2/2017/02/lola.jpg",
                new Timestamp(new Date(90,4,5)),
                "Eilat",
                "Three times a week",
                "Tel Aviv University",
                skillsList,
                "I am best person for cyber security",
                "IT");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Bash", "Windows"));
        candidate = new Candidate("macho@gmail.com",
                "Macho",
                "Pacho",
                "https://1qxya61uvyue18mpsx3zc8om-wpengine.netdna-ssl.com/wp-content/uploads/sites/2/2017/02/lola.jpg",
                new Timestamp(new Date(90,4,5)),
                "Jaffa",
                "Three times a week",
                "Ben Gurion University",
                skillsList,
                "I am friendly personal",
                "IT");

        insertCandidate(candidate);

        skillsList = new ArrayList<>(Arrays.asList("Team work", "Tax professional"));
        candidate = new Candidate("asaf@gmail.com",
                "Asaf",
                "Granit",
                "https://1qxya61uvyue18mpsx3zc8om-wpengine.netdna-ssl.com/wp-content/uploads/sites/2/2017/02/lola.jpg",
                new Timestamp(new Date(90,4,5)),
                "Jaffa",
                "Three times a week",
                "IDC",
                skillsList,
                "I am looking to work in dynamic place with good people",
                "Accounting");

        insertCandidate(candidate);
    }

    public void initializeDBWithSwipes() {
        recruiterDoSwipe("john3@gmail.com", "macho@gmail.com", Side.LEFT);
        recruiterDoSwipe("bar@gmail.com", "asaf@gmail.com", Side.RIGHT);
        recruiterDoSwipe("gre4f@gmail.com", "levi.weiss3@gmail.com", Side.RIGHT);
        recruiterDoSwipe("libat@gmail.com", "levi.weiss3@gmail.com", Side.RIGHT);
        recruiterDoSwipe("john3@gmail.com", "leschinskysergey@gmail.com", Side.RIGHT);
        recruiterDoSwipe("libat@gmail.com", "leschinskysergey@gmail.com", Side.RIGHT);

        candidateDoSwipe("levi.weiss3@gmail.com", "gre4f@gmail.com", Side.RIGHT);
//        candidateDoSwipe("levi.weiss3@gmail.com", "libat@gmail.com", Side.RIGHT);
        candidateDoSwipe("hotty@walla.com", "gre4f@gmail.com", Side.RIGHT);
        candidateDoSwipe("macho@gmail.com", "john3@gmail.com", Side.RIGHT);
        candidateDoSwipe("asaf@gmail.com", "bar@gmail.com", Side.RIGHT);
        candidateDoSwipe("leschinskysergey@gmail.com", "john3@gmail.com", Side.RIGHT);
        candidateDoSwipe("leschinskysergey@gmail.com", "libat@gmail.com", Side.RIGHT);

    }

    void initializeJobCategories() {

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

    void getRecruitersForSwipingScreen_MainFunction(final String candidateMail) {
        getRecruitersForSwipingScreen_CollectDataAboutCandidate(candidateMail);
    }

    void getRecruitersForSwipingScreen_CollectDataAboutCandidate(final String candidateMail) {
        DocumentReference docRef = candidatesCollection.document(candidateMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String candidateJobCategory = (String) document.get(JOB_CATEGORY_KEY);
                        getRecruitersForSwipingScreen_FindRelevantRecruiters(candidateMail, candidateJobCategory);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    void getRecruitersForSwipingScreen_FindRelevantRecruiters(final String candidateMail,
                                                              final String candidateJobCategory) {

        recruitersCollection
                .whereEqualTo(JOB_CATEGORY_KEY, candidateJobCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Recruiter> listOfRecruiters = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Recruiter recruiter = document.toObject(Recruiter.class);
                                listOfRecruiters.add(recruiter);
                            }
                            getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped(candidateMail, listOfRecruiters);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    void getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped(final String candidateMail,
                                                                                  final List<Recruiter> listOfRecruiters) {

        candidatesCollection.document(candidateMail).collection(SWIPES_COLLECTION_NAME).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listOfRecruitersMailStrings = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                listOfRecruitersMailStrings.add(document.getId());
                            }
                            getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped_Final(listOfRecruiters, listOfRecruitersMailStrings);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    void getRecruitersForSwipingScreen_FindRelevantRecruitersWithoutAlreadySwiped_Final(final List<Recruiter> listOfRecruiters,
                                                                                        final List<String> listOfRecruitersMailStrings) {

        List<Recruiter> finalListOfRecruiters = new ArrayList<>();
        for(Recruiter recruiter : listOfRecruiters) {
            String recruiterMail = recruiter.getEmail();
            if(! listOfRecruitersMailStrings.contains(recruiterMail)) {
                Recruiter recruiterToAdd = new Recruiter(recruiter);
                finalListOfRecruiters.add(recruiterToAdd);
            }
        }

        getRecruitersForSwipingScreen(finalListOfRecruiters);

    }

    void getRecruitersForSwipingScreen(List<Recruiter> listOfRecruiters) {

    }


    void getCandidatesForSwipingScreen_MainFunction(final String recruiterMail) {
        getCandidatesForSwipingScreen_CollectDataAboutRecruiter(recruiterMail);
    }

    void getCandidatesForSwipingScreen_CollectDataAboutRecruiter(final String recruiterMail) {
        DocumentReference docRef = recruitersCollection.document(recruiterMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String recruiterJobCategory = (String) document.get(JOB_CATEGORY_KEY);
                        getCandidatesForSwipingScreen_FindRelevantCandidates(recruiterMail, recruiterJobCategory);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    void getCandidatesForSwipingScreen_FindRelevantCandidates(final String recruiterMail,
                                                              final String recruiterJobCategory) {

        candidatesCollection
                .whereEqualTo(JOB_CATEGORY_KEY, recruiterJobCategory)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Candidate> listOfCandidates = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Candidate candidate = document.toObject(Candidate.class);
                                listOfCandidates.add(candidate);
                            }
                            getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped(recruiterMail, listOfCandidates);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    void getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped(final String recruiterMail,
                                                                                  final List<Candidate> listOfCandidates) {

        recruitersCollection.document(recruiterMail).collection(SWIPES_COLLECTION_NAME).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listOfCandidatesMailStrings = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                listOfCandidatesMailStrings.add(document.getId());
                            }
                            getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped_Final(listOfCandidates, listOfCandidatesMailStrings);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    void getCandidatesForSwipingScreen_FindRelevantCandidatesWithoutAlreadySwiped_Final(final List<Candidate> listOfCandidates,
                                                                                        final List<String> listOfCandidatesMailStrings) {

        List<Candidate> finalListOfCandidates = new ArrayList<>();
        for(Candidate candidate : listOfCandidates) {
            String candidateMail = candidate.getEmail();
            if(! listOfCandidatesMailStrings.contains(candidateMail)) {
                Candidate candidateToAdd = new Candidate(candidate);
                finalListOfCandidates.add(candidateToAdd);
            }
        }

        getCandidatesForSwipingScreen(finalListOfCandidates);
    }

    void getCandidatesForSwipingScreen(List<Candidate> listOfCandidates) {

    }

}
