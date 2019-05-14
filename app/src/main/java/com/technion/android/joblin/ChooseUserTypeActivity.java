package com.technion.android.joblin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ChooseUserTypeActivity extends AppCompatActivity {

    private Button mCandButton;
    private Button mRecrButton;
    private String mFirstName;
    private String mLastName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_type);
        mFirstName = getIntent().getStringExtra(LoginActivity.FIRST_NAME_KEY);
        mLastName = getIntent().getStringExtra(LoginActivity.FIRST_NAME_KEY);


        mCandButton = findViewById(R.id.candidate_button);
        mRecrButton = findViewById(R.id.recruiter_button);

        mCandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseUserTypeActivity.this,CandProfPrefActivity.class);
                intent.putExtra(LoginActivity.FIRST_NAME_KEY,mFirstName);
                intent.putExtra(LoginActivity.LAST_NAME_KEY,mLastName);

                startActivity(intent);
            }
        });

        mRecrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseUserTypeActivity.this,RecrProfPrefActivity.class);
                intent.putExtra(LoginActivity.FIRST_NAME_KEY,mFirstName);
                intent.putExtra(LoginActivity.LAST_NAME_KEY,mLastName);

                startActivity(intent);

            }
        });

    }
}
