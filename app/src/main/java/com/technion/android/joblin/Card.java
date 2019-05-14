package com.technion.android.joblin;

import android.content.Context;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

@Layout(R.layout.card_view)
public class Card {
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

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public Card(Context context, Profile profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
    }

    @Resolve
    public void onResolved(){
        Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        nameTxt.setText(mProfile.getName());
        positionScopeTxt.setText(mProfile.getScope());
        EducationTxt.setText(mProfile.getEducation());
        fullEducationTxt.setText(mProfile.getEducation());
        SkillsTxt.setText(mProfile.getSkills());
        fullSkillsTxt.setText(mProfile.getSkills());
        locationNameTxt.setText(mProfile.getLocation());
        descTxt.setText(mProfile.getDesc());

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

    @SwipeOut
    public void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    public void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    public void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");
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
