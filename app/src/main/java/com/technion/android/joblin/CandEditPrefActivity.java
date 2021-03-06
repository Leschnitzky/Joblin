package com.technion.android.joblin;


import android.app.LauncherActivity.ListItem;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.WriteBatch;
import com.thejuki.kformmaster.helper.FormBuildHelper;
import com.thejuki.kformmaster.helper.FormLayouts;
import com.thejuki.kformmaster.listener.OnFormElementValueChangedListener;
import com.thejuki.kformmaster.model.BaseFormElement;
import com.thejuki.kformmaster.model.FormButtonElement;
import com.thejuki.kformmaster.model.FormHeader;
import com.thejuki.kformmaster.model.FormLabelElement;
import com.thejuki.kformmaster.model.FormMultiLineEditTextElement;
import com.thejuki.kformmaster.model.FormPickerDateElement;
import com.thejuki.kformmaster.model.FormPickerDateElement.DateHolder;
import com.thejuki.kformmaster.model.FormPickerDropDownElement;
import com.thejuki.kformmaster.model.FormSingleLineEditTextElement;
import com.thejuki.kformmaster.model.FormSliderElement;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;
import kotlin.Unit;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.EMAIL_KEY;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

public class CandEditPrefActivity extends AppCompatActivity implements OnFormElementValueChangedListener {

    private FormBuildHelper formBuilder = null;
    FirebaseFirestore db;
    CollectionReference candidatesCollection,recruitersCollection,usersCollection,jobCategoriesCollection;
    Intent thisIntent;
    ProgressDialog dialog;
    FirebaseAuth mAuth;
    Candidate candidate;
    String[] locationParts;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dialog = new ProgressDialog(CandEditPrefActivity.this);
        db = FirebaseFirestore.getInstance();
        mContext = this;
        candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
        recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
        usersCollection = db.collection(USERS_COLLECTION_NAME);
        jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);
        mAuth = FirebaseAuth.getInstance();
        thisIntent = getIntent();
        getCandidate(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        Places.initialize(this, "AIzaSyBz1HHQ4v-4wifOcikbPGOqetSzt2vSFPY");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private enum Tag {
        Info,
        Name,
        LastName,
        BirthDate,
        Education,
        Skills,
        Skill1,
        Skill2,
        Skill3,
        Pref,
        Location,
        Radius,
        Category,
        Scope,
        DescTitle,
        Desc,
        Submit,
        Cancel
    }

    void insertCandidate(Candidate candidate) {

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
                dialog.hide();
                LocalBroadcastManager.getInstance(mContext).
                        unregisterReceiver(currentActivityReceiver);
                currentActivityReceiver = null;
                finish();
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
                    if (Objects.requireNonNull(document).exists()) {
                        candidate = document.toObject(Candidate.class);
                        setupForm();
                    }
                }
            }
        });
    }

    private void setupForm() {

        FormLayouts formLayouts = new FormLayouts();

        formBuilder = new FormBuildHelper(this, this, (RecyclerView)findViewById(R.id.recyclerView), true, formLayouts);
        List<BaseFormElement<?>> elements = new ArrayList<>();
        addCandInfo(elements);
        addPreferences(elements);
        addDescription(elements);
        addButtons(elements);
        FormPlacesAutoCompleteViewBinder vb = new FormPlacesAutoCompleteViewBinder(this, formBuilder,null,null);
        CustomViewBinder vc = new CustomViewBinder(this,formBuilder,null);
        formBuilder.registerCustomViewBinder(vb.getViewBinder());
        formBuilder.registerCustomViewBinder(vc.getViewBinder());
        formBuilder.addFormElements(elements);
    }

    private void addCandInfo(List<BaseFormElement<?>> elements) {

        elements.add(new FormHeader("Personal Details"));

        FormSingleLineEditTextElement name = new FormSingleLineEditTextElement(Tag.Name.ordinal());
        name.setTitle("First Name");
        name.setHint("Enter first name here");
        name.setValue(candidate.getName());
        name.setCenterText(true);
        name.setRequired(true);
        name.setEnabled(false);
        elements.add(name);

        FormSingleLineEditTextElement lastname = new FormSingleLineEditTextElement(Tag.LastName.ordinal());
        lastname.setTitle("Last Name");
        lastname.setHint("Enter last name here");
        lastname.setValue(candidate.getLastName());
        lastname.setCenterText(true);
        lastname.setRequired(true);
        lastname.setEnabled(false);
        elements.add(lastname);

        FormPickerDateElement birthdate = new FormPickerDateElement(Tag.BirthDate.ordinal());
        birthdate.setTitle("Date of birth");
        birthdate.setHint("Click here to pick date");
        birthdate.setValue(new DateHolder(candidate.getBirthday().toDate(),
                new SimpleDateFormat("dd/MM/yyyy", Locale.US)));
        birthdate.setCenterText(true);
        birthdate.setRequired(true);
        elements.add(birthdate);

        FormSingleLineEditTextElement education = new FormSingleLineEditTextElement(Tag.Education.ordinal());

        education.setTitle("Education");
        education.setHint("Enter education here");
        education.setValue(candidate.getEducation());
        education.setCenterText(true);
        education.setRequired(true);
        elements.add(education);

        FormLabelElement skills = new FormLabelElement();
        skills.setTitle("Skills: (One at least)");
        skills.setCenterText(true);
        elements.add(skills);

        List<String> skill_list = candidate.getSkillsList();

        FormSingleLineEditTextElement skill1 = new FormSingleLineEditTextElement(Tag.Skill1.ordinal());

        skill1.setTitle("Skill 1");
        skill1.setHint("Enter skill here");
        skill1.setValue(skill_list.get(0));
        skill1.setCenterText(true);
        skill1.setRequired(true);
        elements.add(skill1);

        FormSingleLineEditTextElement skill2 = new FormSingleLineEditTextElement(Tag.Skill2.ordinal());

        skill2.setTitle("Skill 2");
        skill2.setHint("Enter skill here");
        if(skill_list.size()>1)
            skill2.setValue(skill_list.get(1));
        skill2.setCenterText(true);
        elements.add(skill2);

        FormSingleLineEditTextElement skill3 = new FormSingleLineEditTextElement(Tag.Skill3.ordinal());

        skill3.setTitle("Skill 3");
        skill3.setHint("Enter skill here");
        if(skill_list.size()>2)
            skill3.setValue(skill_list.get(2));
        skill3.setCenterText(true);
        elements.add(skill3);
    }

    private void addPreferences(List<BaseFormElement<?>> elements) {

        elements.add(new FormHeader("Job Preferences"));

        FormPlacesAutoCompleteElement location = new FormPlacesAutoCompleteElement(Tag.Location.ordinal());
        location.setTitle("Location");
        location.setHint("Enter location here");
        List<Field> fields = new ArrayList<>();
        fields.add(Field.NAME);
        fields.add(Field.LAT_LNG);
        location.setPlaceFields(fields);
        location.setCenterText(true);
        location.setRequired(true);
        location.setValue(candidate.getJobLocation());
        location.setAutocompleteActivityMode(AutocompleteActivityMode.OVERLAY);
        elements.add(location);

        FormSliderElement slider = new FormSliderElement(Tag.Radius.ordinal());
        slider.setValue(candidate.getMaxDistance());
        slider.setTitle("Distance (KM)");
        slider.setMin(0);
        slider.setMax(100);
        slider.setSteps(5);
        slider.setRequired(true);
        elements.add(slider);

        FormPickerDropDownElement<ListItem> dropDown = new FormPickerDropDownElement<>(Tag.Category.ordinal());
        dropDown.setTitle("Category");
        dropDown.setDialogTitle("Category");

        List<String> jobCategories = new ArrayList<>();
        jobCategories.add( "Accounting");
        jobCategories.add("Computer Science");
        jobCategories.add("Education");
        jobCategories.add("Finance");
        jobCategories.add("IT");
        jobCategories.add("Media");
        jobCategories.add("Sales");

        dropDown.setArrayAdapter(new ArrayAdapter<>(this,R.layout.
                support_simple_spinner_dropdown_item,jobCategories));
        dropDown.setHint("Click here to choose");

        dropDown.setCenterText(true);
        dropDown.setValue(candidate.getJobCategory());
        dropDown.setRequired(true);
        elements.add(dropDown);

        // scope
        Map<String,Integer> timesAweek = new HashMap<>();
        timesAweek.put("Once a week",1);
        timesAweek.put("Twice a week",2);
        timesAweek.put("3 Times a week",3);
        timesAweek.put("4 Times a week",4);
        timesAweek.put("Full Time",5);
        Map<String,Integer> precentage = new HashMap<>();
        precentage.put("20-30%", 20);
        precentage.put("40-50%", 40);
        precentage.put("60-70%", 60);
        precentage.put("80-90%", 80);
        precentage.put("Full Time", 100);
        FormCustomElement scope = new FormCustomElement(Tag.Scope.ordinal());
        scope.setTitle("Scope");
        scope.setHint("Enter here");
        if(candidate.getScope().contains("week"))
            scope.setValue(timesAweek.get(candidate.getScope()));
        else {
            scope.setUnit(1);
            scope.setValue(precentage.get(candidate.getScope()));
        }
        scope.setCenterText(true);
        scope.setRequired(true);
        elements.add(scope);
    }

    private void addDescription(List<BaseFormElement<?>> elements) {
        elements.add(new FormHeader("About me"));
        FormMultiLineEditTextElement description = new FormMultiLineEditTextElement(Tag.Desc.ordinal());

        description.setMaxLines(6);
        description.setHint("Enter description here");
        description.setDisplayTitle(false);
        description.setValue(candidate.getMoreInfo());
        description.setCenterText(true);
        description.setRequired(true);
        elements.add(description);
    }

    private void addButtons(List<BaseFormElement<?>> elements) {
        FormButtonElement submit = new FormButtonElement(Tag.Submit.ordinal());
        submit.setValue("Submit");
        submit.setBackgroundColor(R.color.colorPrimaryDark);
        submit.setValueTextColor(Color.WHITE);

        BaseFormElement name = elements.get(Tag.Name.ordinal());
        BaseFormElement lastname = elements.get(Tag.LastName.ordinal());
        FormPickerDateElement birthdate = (FormPickerDateElement)elements.get(Tag.BirthDate.ordinal());
        BaseFormElement category = elements.get(Tag.Category.ordinal());
        FormCustomElement scope = (FormCustomElement) elements.get(Tag.Scope.ordinal());
        BaseFormElement location = elements.get(Tag.Location.ordinal());
        BaseFormElement radius = elements.get(Tag.Radius.ordinal());
        BaseFormElement desc = elements.get(Tag.Desc.ordinal());
        BaseFormElement education = elements.get(Tag.Education.ordinal());
        BaseFormElement skill1 = elements.get(Tag.Skill1.ordinal());

        List<String> skills = new ArrayList<>();
        submit.getValueObservers().add((newValue, element) -> {
            boolean min_age = Utils.getAge(Objects.requireNonNull(birthdate.getValue()).getTime(),Timestamp.now().toDate())>=13;
            if(formBuilder.isValidForm() && min_age) {
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.setInverseBackgroundForced(false);
                dialog.show();
                Timestamp birthday = new Timestamp(Objects.requireNonNull(birthdate.getValue().getTime()));
                skills.add(skill1.getValueAsString());
                if(!elements.get(Tag.Skill2.ordinal()).getValueAsString().isEmpty())
                    skills.add(elements.get(Tag.Skill2.ordinal()).getValueAsString());
                if(!elements.get(Tag.Skill3.ordinal()).getValueAsString().isEmpty())
                    skills.add(elements.get(Tag.Skill3.ordinal()).getValueAsString());

                Candidate cand = new Candidate(
                        Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(),
                        name.getValueAsString(),
                        lastname.getValueAsString(),
                        Objects.requireNonNull(mAuth.getCurrentUser().getPhotoUrl()).toString(),
                        birthday,
                        locationParts!=null ? locationParts[0] : location.getValueAsString(),
                        Integer.parseInt(radius.getValueAsString()),
                        scope.getFinalValue(),
                        education.getValueAsString(),
                        skills,
                        desc.getValueAsString(),
                        category.getValueAsString());
                cand.setNumberOfSwipesLeft(candidate.getNumberOfSwipesLeft());
                insertCandidate(cand);
                GeoFirestore geoFirestore = new GeoFirestore(candidatesCollection);
                if(locationParts!=null) {
                    geoFirestore.setLocation(mAuth.getCurrentUser().getEmail(),
                            new GeoPoint(Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2])));
                }
                else
                    geoFirestore.setLocation(mAuth.getCurrentUser().getEmail(),
                            new GeoPoint(candidate.getL().get(0),candidate.getL().get(1)));
            }
            else
            {
                Utils.badDetailsPopup(CandEditPrefActivity.this);
                if(!name.isValid())
                    name.setError("Name is required");
                if(!lastname.isValid())
                    lastname.setError("Last name is required");
                if(!birthdate.isValid())
                    birthdate.setError("Date of birth is required");
                if(!min_age)
                    birthdate.setError("Minimum age is 13");
                if(!education.isValid())
                    education.setError("Education is required");
                if(!category.isValid())
                    category.setError("Category is required");
                if(!desc.isValid())
                    desc.setError("Description is required");
                if(!location.isValid())
                    location.setError("Location is required");
                if(!scope.isValid())
                    scope.setError("Scope is required.\n Please check its 20-100% or 1-5 days a week.");
                if(!skill1.isValid())
                    skill1.setError("At least one skill");
            }
            return Unit.INSTANCE;
        });
        elements.add(submit);
    }

    @Override
    public void onBackPressed()
    {
        new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Your changes won't be saved")
                .setConfirmText("Yes")
                .setConfirmClickListener(new OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        LocalBroadcastManager.getInstance(mContext).
                                unregisterReceiver(currentActivityReceiver);
                        currentActivityReceiver = null;
                        finish();
                    }
                })
                .setCancelText("No")
                .show();
    }

    @Override
    public void onValueChanged(@NotNull BaseFormElement<?> formElement) {
        if(formElement.getTag()== Tag.Location.ordinal())
        {
            locationParts = formElement.getValueAsString().split(";");
            formElement.setValue(locationParts[0]);
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data) {
        if (requestCode == Tag.Location.ordinal()) {
            FormPlacesAutoCompleteElement placesElement = formBuilder.getFormElement(Tag.Location.ordinal());
            placesElement.handleActivityResult(formBuilder, resultCode, data);
        }
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
        dialog.dismiss();
        super.onPause();
    }

}