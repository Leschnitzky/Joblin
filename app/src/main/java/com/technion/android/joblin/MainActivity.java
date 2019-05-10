package com.technion.android.joblin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DatabaseAPI db = new DatabaseAPI();
            db.addSwipeDataForCandidate("some_mail", "levi.weiss3@gmail.com", DatabaseAPI.Side.LEFT);
        } catch (DatabaseAPI.communicationExceptionWithDB e) {
            Log.e("error: ", "Error general error.");
        }

        setContentView(R.layout.activity_main);
    }


}
