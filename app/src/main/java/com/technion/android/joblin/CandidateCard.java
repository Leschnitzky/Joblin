package com.technion.android.joblin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.EMAIL_KEY;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.MATCHES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.NUMBER_OF_SUPER_LIKES_LEFT_KEY;
import static com.technion.android.joblin.DatabaseUtils.NUMBER_OF_SWIPES_LEFT_KEY;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.SIDE_KEY;
import static com.technion.android.joblin.DatabaseUtils.SWIPES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TAG;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.Side;
import static com.technion.android.joblin.RecMainActivity.recrSuperLiked;

@Layout(R.layout.cancard_view)
public class CandidateCard {
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

    private Candidate mProfile;
    private Boolean noMoreSuperLikes = false;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private final String swiper;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    public CandidateCard(Context context, Candidate profile, SwipePlaceHolderView swipeView, final String swiper_Email) {
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
        GlideApp.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        Integer age = Utils.getAge(mProfile.getBirthday().toDate(), Timestamp.now().toDate());
        nameTxt.setText(String.format("%s %s, %s", mProfile.getName(), mProfile.getLastName(), age.toString()));
        positionScopeTxt.setText(mProfile.getScope());
        EducationTxt.setText(mProfile.getEducation());
        fullEducationTxt.setText(mProfile.getEducation());
        SkillsTxt.setText(getSkillsString(mProfile.getSkillsList(),10));
        fullSkillsTxt.setText(getSkillsString(mProfile.getSkillsList(),20));
        locationNameTxt.setText(mProfile.getJobLocation());
        descTxt.setText(mProfile.getMoreInfo());

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

    void recruiterDoSwipe(final String recruiterMail, final String candidateMail, final Side side) {
        DocumentReference docRef = recruitersCollection.document(recruiterMail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Recruiter recruiter = document.toObject(Recruiter.class);
                        if((side == Side.RIGHT) && (recruiter.getNumberOfSuperLikesLeft() == 0) && recrSuperLiked) {
                            noMoreSuperLikes = true;
                            Utils.noMoreSuperLikesPopUp(mSwipeView.getContext());
                            mSwipeView.undoLastSwipe();
                        }else if((side == Side.RIGHT) && (recruiter.getNumberOfSwipesLeft() == 0)) {
                            Utils.noMoreSwipesPopUp(mSwipeView.getContext());
                            mSwipeView.undoLastSwipe();
                        }
                        else {
                            addSwipeDataForRecruiter(recruiterMail, candidateMail, side);
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

    void addSwipeDataForRecruiter(String recruiterMail, String candidateMail, Side side) {
        addSwipeData(recruitersCollection, candidatesCollection, recruiterMail, candidateMail, side);
    }

    void addSwipeData(CollectionReference firstCollection,
                      CollectionReference secondCollection,
                      String firstMail,
                      String secondMail,
                      Side side) {
        if(noMoreSuperLikes) {
            noMoreSuperLikes = false;
            return;
        } else {
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

                        if(snapshotMainFirst.exists()) {
                            long numberOfSwipesLeft = snapshotMainFirst.getLong(NUMBER_OF_SWIPES_LEFT_KEY);
                            transaction.update(mainDocRefOfFirst, NUMBER_OF_SWIPES_LEFT_KEY, numberOfSwipesLeft - 1);
                            if(recrSuperLiked){
                                long numberOfSuperLikes = snapshotMainFirst.getLong(NUMBER_OF_SUPER_LIKES_LEFT_KEY);
                                transaction.update(mainDocRefOfFirst, NUMBER_OF_SUPER_LIKES_LEFT_KEY, numberOfSuperLikes - 1);
                                recrSuperLiked = false;
                            }
                        }

                        return isMatch;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isMatch) {
//                    if(isMatch && !recrSuperLiked) {
//                        Utils.matchPopUp(mSwipeView.getContext(),"candidate");
//                    }
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
    }

    @SwipeIn
    public void onSwipeIn(){
        if(recrSuperLiked) {
            recruiterDoSwipe(swiper,mProfile.getEmail(),Side.RIGHT);
            swipeRightOnRecruiter(swiper,mProfile.getEmail());
        } else {
            recruiterDoSwipe(swiper,mProfile.getEmail(),Side.RIGHT);
        }
    }

    @SwipeOut
    public void onSwipedOut(){
        recruiterDoSwipe(swiper,mProfile.getEmail(),Side.LEFT);
    }

    @SwipeInState
    public void onSwipeInState(){
    }

    @SwipeOutState
    public void onSwipeOutState(){
    }


    // ################# SUPER LIKE FUNCTIONALITY ##########################



    public void swipeRightOnRecruiter(String recruiterMail, String candidateMail) {
        addSwipeData(candidatesCollection, recruitersCollection, candidateMail, recruiterMail, Side.RIGHT);
    }

}
