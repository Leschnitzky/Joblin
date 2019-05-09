package com.technion.android.joblin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Animate;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

@Layout(R.layout.card_view)
public class Card {
    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameTxt)
    private TextView nameTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    @View(R.id.positionScopeTxt)
    private TextView positionScopeTxt;

    @View(R.id.EducationTxt)
    private TextView EducationTxt;

    @View(R.id.fullEducationTxt)
    private TextView fullEducationTxt;

    @View(R.id.SkillsTxt)
    private TextView SkillsTxt;

    @View(R.id.fullSkillsTxt)
    private TextView fullSkillsTxt;

    @View(R.id.descriptionTxt)
    private TextView descTxt;

    @View(R.id.moreButtonLayout)
    private LinearLayout moreButtonLayout;

    @View(R.id.moreDescLayout)
    private LinearLayout descLayout;

    @View(R.id.detailsButton)
    private ImageButton detailsButton;

    @View(R.id.moreDetailsTxtView)
    private TextView moreDetailsTxtView;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private boolean opened = false;

    public Card(Context context, Profile profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        nameTxt.setText(mProfile.getName());
        positionScopeTxt.setText(mProfile.getScope());
        EducationTxt.setText(mProfile.getEducation());
        fullEducationTxt.setText(mProfile.getEducation());
        SkillsTxt.setText(mProfile.getSkills());
        fullSkillsTxt.setText(mProfile.getSkills());
        locationNameTxt.setText(mProfile.getLocation());
        descTxt.setText(mProfile.getDesc());
    }

    @Click(R.id.detailsButton)
    public void OnClick()
    {
        if(!opened){
            descLayout.animate().translationYBy(-1600).start();
            moreButtonLayout.animate().translationYBy(-800).setListener((new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(descLayout.getVisibility()==LinearLayout.GONE) {
                        descLayout.setVisibility(LinearLayout.VISIBLE);
                        moreDetailsTxtView.setVisibility(TextView.GONE);
                    }
                }
            })).start();
            detailsButton.setRotation(180);
        } else {
            descLayout.animate().translationYBy(1600).start();
            moreButtonLayout.animate().translationYBy(800).setListener((new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if(descLayout.getVisibility()==LinearLayout.VISIBLE) {
                        descLayout.setVisibility(LinearLayout.GONE);
                        moreDetailsTxtView.setVisibility(TextView.VISIBLE);
                    }
                }
            })).start();
            detailsButton.setRotation(0);
        }
        opened = !opened;
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }

}
