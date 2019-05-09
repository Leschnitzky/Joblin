package com.technion.android.joblin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DatabaseAPI db = new DatabaseAPI();
            List<String> skillsList = new ArrayList<>(Arrays.asList("one", "two"));

            Candidate candidate = new Candidate("some_mail",
                    "some_name",
                    "some_last_name",
                    2,
                    "some_job_location",
                    40,
                    "some_education",
                    skillsList,
                    "some_more_info",
                    "some_job_category");

            db.insertCandidate(candidate);
        } catch (DatabaseAPI.communicationExceptionWithDB e) {
            Log.d("error: ", "Error general error.");
        }

        setContentView(R.layout.activity_main);
    }
}
