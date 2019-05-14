package com.technion.android.joblin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.technion.android.joblin.DatabaseAPI.Side;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.technion.android.joblin.DatabaseUtils.*;

@Layout(R.layout.reccard_view)
public class RecruiterCard {
    @View(R.id.profileImageView)
    ImageView profileImageView;

    @View(R.id.nameTxt)
    TextView nameTxt;

    @View(R.id.locationNameTxt)
    TextView locationNameTxt;

    @View(R.id.positionScopeTxt)
    TextView positionScopeTxt;

    @View(R.id.EducationTxt)
    TextView EducationTxt;

    @View(R.id.fullEducationTxt)
    TextView fullEducationTxt;

    @View(R.id.SkillsTxt)
    TextView SkillsTxt;

    @View(R.id.fullSkillsTxt)
    TextView fullSkillsTxt;

    @View(R.id.descriptionTxt)
    TextView descTxt;

    @View(R.id.moreButtonLayout)
    LinearLayout moreButtonLayout;

    @View(R.id.detailsLayout)
    GridLayout detailsLayout;

    @View(R.id.moreDescLayout)
    LinearLayout descLayout;

    @View(R.id.detailsImage)
    ImageView detailsImage;

    @View(R.id.moreDetailsTxtView)
    TextView moreDetailsTxtView;

    @View(R.id.slidingpanel)
    SlidingUpPanelLayout slidingPanel;

    private Recruiter mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private final String swiper;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    public RecruiterCard(Context context, Recruiter profile, SwipePlaceHolderView swipeView, final String swiper_Email) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
        swiper = swiper_Email;
    }

    private String getSkillsString(List<String> skills, int maxLength)
    {
        StringBuilder skillsString = new StringBuilder();
        for (String skill:skills) {
            if(skill.length()<=maxLength)
                skillsString.append(skill).append(", ");
        }
        return skillsString.toString().substring(0,skillsString.toString().length()-2);
    }

    @Resolve
    public void onResolved(){
        Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        nameTxt.setText(String.format("%s %s", mProfile.getName(), mProfile.getLastName()));
        positionScopeTxt.setText(mProfile.getRequiredScope());
        EducationTxt.setText(mProfile.getRequiredEducation());
        fullEducationTxt.setText(mProfile.getRequiredEducation());
        SkillsTxt.setText(getSkillsString(mProfile.getRequiredSkillsList(),10));
        fullSkillsTxt.setText(getSkillsString(mProfile.getRequiredSkillsList(),20));
        locationNameTxt.setText(mProfile.getJobLocation());
        descTxt.setText(mProfile.getJobDescription());

        slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(android.view.View panel, float slideOffset) {
                if(slideOffset>0) {
                    detailsImage.setRotation(180);
                    moreDetailsTxtView.setVisibility(TextView.GONE);
                }
                else {
                    detailsImage.setRotation(0);
                    moreDetailsTxtView.setVisibility(TextView.VISIBLE);
                }
            }

            @Override
            public void onPanelStateChanged(android.view.View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
    }

    @SwipeCancelState
    public void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
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
                            Utils.noMoreSwipesPopUp(mSwipeView.getContext());
                        } else {
                            addSwipeDataForCandidate(candidateMail, recruiterMail, side);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        Utils.errorPopUp(mSwipeView.getContext(),"");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Utils.errorPopUp(mSwipeView.getContext(),"");
                }
            }
        });
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
                    }
                    else {
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
                        Utils.matchPopUp(mSwipeView.getContext());
                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Transaction failure.", e);
                            Utils.errorPopUp(mSwipeView.getContext(),e.toString());
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
                            Utils.errorPopUp(mSwipeView.getContext(),e.toString());
                        }
                    });
        }

    }

    @SwipeIn
    public void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");
        candidateDoSwipe(swiper,mProfile.getEmail(),Side.RIGHT);
    }

    @SwipeOut
    public void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        candidateDoSwipe(swiper,mProfile.getEmail(),Side.LEFT);
    }

    @SwipeInState
    public void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    public void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }

}
