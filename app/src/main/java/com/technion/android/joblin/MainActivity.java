package com.technion.android.joblin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseAPI db = new DatabaseAPI();
//        db.addSwipeDataForRecruiter("bar@gmail.com", "asaf@gmail.com", DatabaseAPI.Side.RIGHT);
        db.initializeDBWithSomeData();
        setContentView(R.layout.activity_main);
    }


}