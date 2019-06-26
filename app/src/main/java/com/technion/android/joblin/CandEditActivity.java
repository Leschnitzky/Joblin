package com.technion.android.joblin;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aminography.redirectglide.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

public class CandEditActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog dialog;
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);


    CardView can_profile;
    CardView can_card;
    ImageView profileImageView;
    TextView nameTxt;
    TextView locationNameTxt;
    TextView positionScopeTxt;
    TextView EducationTxt;
    TextView fullEducationTxt;
    TextView SkillsTxt;
    TextView fullSkillsTxt;
    TextView descTxt;
    LinearLayout moreButtonLayout;
    GridLayout detailsLayout;
    LinearLayout descLayout;
    ImageView detailsImage;
    TextView moreDetailsTxtView;
    SlidingUpPanelLayout slidingPanel;
    ImageView mProfileBackButton;
    ImageView mProfileEditButton;
    ImageView mProfileLogoffButton;
    Context mContext;
    ImageView filter_spinner;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cand_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cand);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        filter_spinner = findViewById(R.id.filter_spinner);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        PopupMenu popup = new PopupMenu(CandEditActivity.this, filter_spinner);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu_cand, popup.getMenu());
        int filter_method = sharedPrefs.getInt(getResources().getString(R.string.saved_filtering_method), 0);
        popup.getMenu().getItem(filter_method).setChecked(true);
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                item.setChecked(!item.isChecked());
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.saved_filtering_method),item.getOrder());
                editor.apply();
                return true;
            }
        });
        filter_spinner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();//showing popup menu
            }
        });

        can_profile = findViewById(R.id.can_cardprofile);
        can_card = findViewById(R.id.can_cardview);
        View child = LayoutInflater.from(this).inflate(R.layout.cancard_view,can_card);
        can_profile.addView(child);
        mContext = this;
        profileImageView = child.findViewById(R.id.profileImageView);
        nameTxt = child.findViewById(R.id.nameTxt);
        locationNameTxt = child.findViewById(R.id.locationNameTxt);
        positionScopeTxt = child.findViewById(R.id.positionScopeTxt);
        EducationTxt = child.findViewById(R.id.EducationTxt);
        fullEducationTxt = child.findViewById(R.id.fullEducationTxt);
        SkillsTxt = child.findViewById(R.id.SkillsTxt);
        fullSkillsTxt = child.findViewById(R.id.fullSkillsTxt);
        descTxt = child.findViewById(R.id.descriptionTxt);
        moreButtonLayout = child.findViewById(R.id.moreButtonLayout);
        detailsLayout = child.findViewById(R.id.detailsLayout);
        descLayout = child.findViewById(R.id.moreDescLayout);
        detailsImage = child.findViewById(R.id.detailsImage);
        moreDetailsTxtView = child.findViewById(R.id.moreDetailsTxtView);
        slidingPanel = child.findViewById(R.id.slidingpanel);
        mProfileBackButton = findViewById(R.id.profile_back_button);
        mProfileEditButton = findViewById(R.id.bottom_background_cand);
        mProfileLogoffButton = findViewById(R.id.cand_logoff_button);

        Log.d("FIRST_BUTTON",mProfileBackButton.toString());

        mProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager.getInstance(mContext).
                        unregisterReceiver(currentActivityReceiver);
                dialog.dismiss();
                currentActivityReceiver = null;
                finish();
            }
        });

        Log.d("SECOND_BUTTON",mProfileEditButton.toString());

        mProfileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CandEditActivity.this,CandEditPrefActivity.class);
                startActivity(intent);
            }
        });

        mProfileLogoffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logoutPopUp(mContext);
            }
        });


        String email = mAuth.getCurrentUser().getEmail();
//        getCandidate(email);
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

    void getCandidate(final String email) {
        DocumentReference docRef = candidatesCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Candidate mProfile = document.toObject(Candidate.class);
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
                        dialog.hide();
                    } else {
                        dialog.hide();
                        Utils.errorPopUp(CandEditActivity.this,task.getException().getMessage());
                    }
                } else {
                    dialog.hide();
                    Utils.errorPopUp(CandEditActivity.this,task.getException().getMessage());
                }
            }
        });
        dialog.hide();

    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog = new ProgressDialog(CandEditActivity.this);
        dialog.setMessage("Retrieving user information...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();
        getCandidate(mAuth.getCurrentUser().getEmail());
    }

    private BroadcastReceiver currentActivityReceiver;

    @Override
    protected void onResume() {
        super.onResume();

        currentActivityReceiver = new CurrentActivityReceiver(this);
        LocalBroadcastManager.getInstance(this).
                registerReceiver(currentActivityReceiver, CurrentActivityReceiver.CURRENT_ACTIVITY_RECEIVER_FILTER);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).
                unregisterReceiver(currentActivityReceiver);
        dialog.dismiss();
        currentActivityReceiver = null;
        super.onPause();
    }

}
