package com.technion.android.joblin;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aminography.redirectglide.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.technion.android.joblin.CanMainActivity.candSuperLiked;
import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.EMAIL_KEY;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.MATCHES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.NUMBER_OF_SUPER_LIKES_LEFT_KEY;
import static com.technion.android.joblin.DatabaseUtils.NUMBER_OF_SWIPES_LEFT_KEY;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.SIDE_KEY;
import static com.technion.android.joblin.DatabaseUtils.SWIPES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.Side;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

@Layout(R.layout.reccard_view)
public class RecruiterCard {
    @View(R.id.profileImageView)
    ImageView profileImageView;

    @View(R.id.nameTxt)
    TextView nameTxt;

    @View(R.id.workplaceTxt)
    TextView placeTxt;

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

    @View(R.id.distanceTxt)
    TextView distanceTxt;

    private Recruiter mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private final String swiper;
    private final Location swiper_loc;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);
    private Boolean noMoreSuperLikes = false;

    public RecruiterCard(Context context, Recruiter profile, SwipePlaceHolderView swipeView,
                         final String swiper_Email, final Location swiper_location) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
        swiper = swiper_Email;
        swiper_loc = swiper_location;
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
        GlideApp.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        nameTxt.setText(String.format("%s %s", mProfile.getName(), mProfile.getLastName()));
        placeTxt.setText(mProfile.getWorkPlace());
        positionScopeTxt.setText(mProfile.getRequiredScope());
        EducationTxt.setText(mProfile.getRequiredEducation());
        fullEducationTxt.setText(mProfile.getRequiredEducation());
        SkillsTxt.setText(getSkillsString(mProfile.getRequiredSkillsList(),10));
        fullSkillsTxt.setText(getSkillsString(mProfile.getRequiredSkillsList(),20));

        float[] distance = new float[1];
        /*
        GeoPoint jobPoint = Utils.getPoint(mContext,mProfile.getJobLocation());
        */
        GeoPoint jobPoint = new GeoPoint(mProfile.getL().get(0),mProfile.getL().get(1));
        if(swiper_loc!=null) {
            Location.distanceBetween(swiper_loc.getLatitude(), swiper_loc.getLongitude(),
                    jobPoint.getLatitude(), jobPoint.getLongitude(), distance);
            distanceTxt.setText(Math.round(distance[0] / 1000) + " km away");
        }
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
    }

    void candidateDoSwipe(final String candidateMail, final String recruiterMail, final Side side) {

        DocumentReference docRef = candidatesCollection.document(candidateMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Candidate candidate = document.toObject(Candidate.class);
                        if((side == Side.RIGHT) && (candidate.getNumberOfSuperLikesLeft() == 0) && candSuperLiked) {
                            noMoreSuperLikes = true;
                            Utils.noMoreSuperLikesPopUp(mSwipeView.getContext());
                            mSwipeView.undoLastSwipe();
                        } else if((side == Side.RIGHT) && (candidate.getNumberOfSwipesLeft() == 0)) {
                            Utils.noMoreSwipesPopUp(mSwipeView.getContext());
                            mSwipeView.undoLastSwipe();
                        } else {
                            addSwipeDataForCandidate(candidateMail, recruiterMail, side);
                        }
                    } else {
                        Utils.errorPopUp(mSwipeView.getContext(),"");
                    }
                } else {
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

        if(noMoreSuperLikes){
            noMoreSuperLikes = false;
            return;
        } else {

            String sideString;
            if (side == Side.RIGHT) {
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

            if (side == Side.RIGHT) {
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
                        if (snapshotSwipeSecond.exists()) {
                            if (snapshotSwipeSecond.get(SIDE_KEY).equals("right")) {
                                transaction.set(matchDocRefOfFirst, secondMatchesMapData);
                                transaction.set(matchDocRefOfSecond, firstMatchesMapData);
                                isMatch = true;
                            }
                            transaction.update(swipeDocRefOfSecond, secondSwipesMapData);
                        } else {
                            transaction.set(swipeDocRefOfSecond, secondSwipesMapData);
                            transaction.delete(swipeDocRefOfSecond);
                        }

                        if (snapshotSwipeFirst.exists()) {
                            transaction.update(swipeDocRefOfFirst, firstSwipesMapData);
                        } else {
                            transaction.set(swipeDocRefOfFirst, firstSwipesMapData);
                        }

                        if (snapshotMainFirst.exists()) {
                            long numberOfSwipesLeft = snapshotMainFirst.getLong(NUMBER_OF_SWIPES_LEFT_KEY);
                            transaction.update(mainDocRefOfFirst, NUMBER_OF_SWIPES_LEFT_KEY, numberOfSwipesLeft - 1);
                            if(candSuperLiked){
                                long numberOfSuperLikes = snapshotMainFirst.getLong(NUMBER_OF_SUPER_LIKES_LEFT_KEY);
                                transaction.update(mainDocRefOfFirst, NUMBER_OF_SUPER_LIKES_LEFT_KEY, numberOfSuperLikes - 1);
                                candSuperLiked = false;
                            }
                        }

                        return isMatch;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isMatch) {
//                    if(isMatch && !candSuperLiked) {
//                        Utils.matchPopUp(mSwipeView.getContext(),"recruiter");
//                    }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Transaction failure.", e);
                                Utils.errorPopUp(mSwipeView.getContext(), e.toString());
                            }
                        });
            } else {
                firstCollection.document(firstMail).collection(SWIPES_COLLECTION_NAME).document(secondMail)
                        .set(firstSwipesMapData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                                Utils.errorPopUp(mSwipeView.getContext(), e.toString());
                            }
                        });
            }
        }

    }

    public void swipeRightOnCandidate(String candidateMail, String recruiterMail) {
        addSwipeData(recruitersCollection, candidatesCollection, recruiterMail, candidateMail, Side.RIGHT);
    }

    @SwipeIn
    public void onSwipeIn(){
        if(candSuperLiked) {
            candidateDoSwipe(swiper,mProfile.getEmail(),Side.RIGHT);
            swipeRightOnCandidate(swiper,mProfile.getEmail());
        } else {
            candidateDoSwipe(swiper,mProfile.getEmail(),Side.RIGHT);
        }
    }

    @SwipeOut
    public void onSwipedOut(){
        candidateDoSwipe(swiper,mProfile.getEmail(),Side.LEFT);
    }

    @SwipeInState
    public void onSwipeInState(){
    }

    @SwipeOutState
    public void onSwipeOutState(){
    }

}
