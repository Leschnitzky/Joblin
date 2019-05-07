package com.technion.android.joblin;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class RecrEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recr_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_recr);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }
}
