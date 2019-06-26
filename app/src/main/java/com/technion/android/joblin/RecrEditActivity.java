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

public class RecrEditActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog dialog;
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    CardView rec_profile;
    CardView rec_card;
    ImageView profileImageView;
    TextView nameTxt;
    TextView placeTxt;
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
    ImageView mProfileEditButton;
    Context mContext;
    ImageView filter_spinner;
    ImageView mProfileBackButton;
    ImageView mProfileLogoffButton;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recr_edit);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Retrieving user information...");
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        filter_spinner = findViewById(R.id.filter_spinner);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        PopupMenu popup = new PopupMenu(RecrEditActivity.this, filter_spinner);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu_recr, popup.getMenu());
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_recr);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        rec_profile = findViewById(R.id.recr_cardprofile);
        rec_card = findViewById(R.id.rec_cardview);
        View child = LayoutInflater.from(this).inflate(R.layout.reccard_view,rec_card);
        rec_profile.addView(child);
        mContext = this;
        profileImageView = child.findViewById(R.id.profileImageView);
        nameTxt = child.findViewById(R.id.nameTxt);
        placeTxt = child.findViewById(R.id.workplaceTxt);
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
        mProfileBackButton = findViewById(R.id.recr_edit_back_button);
        mProfileEditButton = findViewById(R.id.bottom_background_recr);
        mProfileLogoffButton = findViewById(R.id.recr_logoff_button);


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


        mProfileEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                Intent intent = new Intent(RecrEditActivity.this,RecrEditPrefActivity.class);
                startActivity(intent);
            }
        });

        mProfileLogoffButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                Utils.logoutPopUp(mContext);
            }
        });

        String email = mAuth.getCurrentUser().getEmail();
//        getRecruiter(email);      We use this twice, once in onCreate and once in onStart
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

    void getRecruiter(final String email) {
        DocumentReference docRef = recruitersCollection.document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Recruiter mProfile = document.toObject(Recruiter.class);
                        GlideApp.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
                        nameTxt.setText(String.format("%s %s", mProfile.getName(), mProfile.getLastName()));
                        placeTxt.setText(mProfile.getWorkPlace());
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
                    } else {
                        Utils.errorPopUp(RecrEditActivity.this,task.getException().getMessage());
                    }
                } else {
                    Utils.errorPopUp(RecrEditActivity.this,task.getException().getMessage());
                }
            }
        });

        dialog.hide();
    }


    @Override
    protected void onStart() {
        super.onStart();

        getRecruiter(mAuth.getCurrentUser().getEmail());
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
        currentActivityReceiver = null;
        super.onPause();
    }
}

