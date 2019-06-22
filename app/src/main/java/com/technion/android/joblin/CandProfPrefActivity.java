package com.technion.android.joblin;


import android.app.LauncherActivity.ListItem;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
import com.thejuki.kformmaster.model.FormPickerDropDownElement;
import com.thejuki.kformmaster.model.FormSingleLineEditTextElement;
import com.thejuki.kformmaster.model.FormSliderElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Unit;

import static com.technion.android.joblin.DatabaseUtils.CANDIDATES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.EMAIL_KEY;
import static com.technion.android.joblin.DatabaseUtils.JOB_CATEGORIES_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.RECRUITERS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

public class CandProfPrefActivity extends AppCompatActivity implements OnFormElementValueChangedListener {

    private FormBuildHelper formBuilder = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference candidatesCollection = db.collection(CANDIDATES_COLLECTION_NAME);
    CollectionReference recruitersCollection = db.collection(RECRUITERS_COLLECTION_NAME);
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);
    CollectionReference jobCategoriesCollection = db.collection(JOB_CATEGORIES_COLLECTION_NAME);

    Intent thisIntent;
    ProgressDialog dialog;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        dialog = new ProgressDialog(CandProfPrefActivity.this);
        thisIntent = getIntent();
        setupForm();
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
        MaxDistance,
        Category,
        Scope,
        DescTitle,
        Desc,
        Submit,
        PlacesElement
    }

    void insertCandidate(Candidate candidate) {
//        Map<String, Object> candidateMapData = new HashMap<>();
//        candidateMapData.put(AGE_KEY, candidate.getAge());
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
                dialog.hide();
                Intent intent = new Intent(CandProfPrefActivity.this,CanMainActivity.class);
                startActivity(intent);

                finish();
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
        FormPlacesAutoCompleteViewBinder vb = new FormPlacesAutoCompleteViewBinder(this,formBuilder,null,null);
        formBuilder.registerCustomViewBinder(vb.getViewBinder());
        formBuilder.addFormElements(elements);
    }



    private void addCandInfo(List<BaseFormElement<?>> elements) {

        // info
        elements.add(new FormHeader("Personal Details"));

        // name
        FormSingleLineEditTextElement name = new FormSingleLineEditTextElement(Tag.Name.ordinal());
        name.setTitle("First Name");
        name.setHint("Enter first name here");
        name.setValue(thisIntent.getStringExtra(LoginActivity.FIRST_NAME_KEY));
        name.setCenterText(true);
        name.setRequired(true);
        elements.add(name);

        // last name
        FormSingleLineEditTextElement lastname = new FormSingleLineEditTextElement(Tag.LastName.ordinal());
        lastname.setTitle("Last Name");
        lastname.setHint("Enter last name here");
        lastname.setValue(thisIntent.getStringExtra(LoginActivity.LAST_NAME_KEY));
        lastname.setCenterText(true);
        lastname.setRequired(true);
        elements.add(lastname);

        // birthday
        FormPickerDateElement birthdate = new FormPickerDateElement(Tag.BirthDate.ordinal());
        birthdate.setTitle("Date of birth");
        birthdate.setHint("Click here to pick date");
        birthdate.setCenterText(true);
        birthdate.setRequired(true);
        elements.add(birthdate);

        // education
        FormSingleLineEditTextElement education = new FormSingleLineEditTextElement(Tag.Education.ordinal());
        education.setTitle("Education");
        education.setHint("Enter education here");
        education.setCenterText(true);
        education.setRequired(true);
        elements.add(education);

        // skills
        FormLabelElement skills = new FormLabelElement();
        skills.setTitle("Skills: (One at least)");
        skills.setCenterText(true);
        elements.add(skills);

        // skills1
        FormSingleLineEditTextElement skill1 = new FormSingleLineEditTextElement(Tag.Skill1.ordinal());
        skill1.setTitle("Skill 1");
        skill1.setHint("Enter skill here");
        skill1.setCenterText(true);
        skill1.setRequired(true);
        elements.add(skill1);

        // skills2
        FormSingleLineEditTextElement skill2 = new FormSingleLineEditTextElement(Tag.Skill2.ordinal());
        skill2.setTitle("Skill 2");
        skill2.setHint("Enter skill here");
        skill2.setCenterText(true);
        elements.add(skill2);

        // skills3
        FormSingleLineEditTextElement skill3 = new FormSingleLineEditTextElement(Tag.Skill3.ordinal());
        skill3.setTitle("Skill 3");
        skill3.setHint("Enter skill here");
        skill3.setCenterText(true);
        elements.add(skill3);
    }


    private void addPreferences(List<BaseFormElement<?>> elements) {

        // preferences
        elements.add(new FormHeader("Job Preferences"));

        // location
        FormPlacesAutoCompleteElement location = new FormPlacesAutoCompleteElement(Tag.Location.ordinal());
        location.setTitle("Location");
        location.setHint("Enter location here");
        location.setPlaceFields(Collections.singletonList(Place.Field.NAME));
        location.setCenterText(true);
        location.setRequired(true);
        location.setAutocompleteActivityMode(AutocompleteActivityMode.OVERLAY);
        elements.add(location);

        // max distance
        FormSliderElement slider = new FormSliderElement(Tag.MaxDistance.ordinal());
        slider.setTitle("Maximum Distance");
        slider.setValue(50);
        slider.setMin(1);
        slider.setMax(100);
        slider.setIncrementBy(1);
        elements.add(slider);

        // category
        FormPickerDropDownElement<ListItem> dropDown = new FormPickerDropDownElement<>(Tag.Category.ordinal());
        dropDown.setTitle("Category");
        dropDown.setDialogTitle("Category");

        List<String> jobCategories = new ArrayList<>();
        jobCategories.add("Accounting");
        jobCategories.add("Computer Science");
        jobCategories.add("Education");
        jobCategories.add("Finance");
        jobCategories.add("IT");
        jobCategories.add("Media");
        jobCategories.add("Sales");

        dropDown.setArrayAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, jobCategories));
        dropDown.setHint("Click here to choose");
        dropDown.setCenterText(true);
        dropDown.setRequired(true);
        elements.add(dropDown);

        // scope
        FormPickerDropDownElement<ListItem> scope = new FormPickerDropDownElement<>(Tag.Scope.ordinal());
        List<String> scopesList = new ArrayList<>();
        scopesList.add("20-30%");
        scopesList.add("40-50%");
        scopesList.add("60-70%");
        scopesList.add("80-90%");
        scopesList.add("Once a Week");
        scopesList.add("Twice a Week");
        scopesList.add("3 Times a Week");
        scopesList.add("4 Times a Week");
        scopesList.add("5 Times a Week");
        scopesList.add("Full Time");

        scope.setArrayAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item, scopesList));
        scope.setTitle("Scope");
        scope.setDialogTitle("Scope");
        scope.setHint("Click here to choose");
        scope.setCenterText(true);
        scope.setRequired(true);
        elements.add(scope);
    }

    private void addDescription(List<BaseFormElement<?>> elements) {

        // description title
        elements.add(new FormHeader("About me"));

        // description
        FormMultiLineEditTextElement description = new FormMultiLineEditTextElement(Tag.Desc.ordinal());
        description.setMaxLines(6);
        description.setHint("Enter description here");
        description.setDisplayTitle(false);
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
        BaseFormElement scope = elements.get(Tag.Scope.ordinal());
        BaseFormElement location = elements.get(Tag.Location.ordinal());
        BaseFormElement desc = elements.get(Tag.Desc.ordinal());
        BaseFormElement education = elements.get(Tag.Education.ordinal());
        BaseFormElement skill1 = elements.get(Tag.Skill1.ordinal());
        BaseFormElement maxDistance = elements.get(Tag.MaxDistance.ordinal());

        List<String> skills = new ArrayList<>();
        submit.getValueObservers().add((newValue, element) -> {
            boolean min_age = false;
            if(birthdate.isValid())
                min_age = Utils.getAge(birthdate.getValue().getTime(),Timestamp.now().toDate())>=13;
            if(formBuilder.isValidForm() && min_age) {
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.setInverseBackgroundForced(false);
                dialog.show();
                Timestamp birthday = new Timestamp(birthdate.getValue().getTime());
                skills.add(skill1.getValueAsString());
                if(!elements.get(Tag.Skill2.ordinal()).getValueAsString().isEmpty())
                    skills.add(elements.get(Tag.Skill2.ordinal()).getValueAsString());
                if(!elements.get(Tag.Skill3.ordinal()).getValueAsString().isEmpty())
                    skills.add(elements.get(Tag.Skill3.ordinal()).getValueAsString());

                Candidate cand = new Candidate(
                        mAuth.getCurrentUser().getEmail(),
                        name.getValueAsString(),
                        lastname.getValueAsString(),
                        thisIntent.getStringExtra(LoginActivity.URI_KEY),
                        birthday,
                        location.getValueAsString(),
                        scope.getValueAsString(),
                        education.getValueAsString(),
                        skills,
                        desc.getValueAsString(),
                        category.getValueAsString(),
                        (int) maxDistance.getValue()
                );
                insertCandidate(cand);
            }
            else
            {
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
                    scope.setError("Scope is required");
                if(!skill1.isValid())
                    skill1.setError("At least one skill");
            }
            return Unit.INSTANCE;
        });
        elements.add(submit);
    }

    @Override
    public void onValueChanged(BaseFormElement<?> formElement) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Tag.Location.ordinal()) {
            FormPlacesAutoCompleteElement placesElement = formBuilder.getFormElement(Tag.Location.ordinal());
            placesElement.handleActivityResult(formBuilder, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
    }
}