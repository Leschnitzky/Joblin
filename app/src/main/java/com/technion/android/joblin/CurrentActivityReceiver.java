package com.technion.android.joblin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

public class CurrentActivityReceiver extends BroadcastReceiver {
    private static final String TAG = CurrentActivityReceiver.class.getSimpleName();
    public static final String CURRENT_ACTIVITY_ACTION = "current.activity.action";
    public static final IntentFilter CURRENT_ACTIVITY_RECEIVER_FILTER = new IntentFilter(CURRENT_ACTIVITY_ACTION);

    private Activity receivingActivity;

    public CurrentActivityReceiver(Activity activity) {
        this.receivingActivity = activity;
    }

    @Override
    public void onReceive(Context sender, Intent intent) {
        Log.v(TAG, "onReceive: finishing:" + receivingActivity.getClass().getSimpleName());

        //Should display pop up
        String title = intent.getStringExtra("Title");
        String body = intent.getStringExtra("Body");
        Toast.makeText(receivingActivity,body, Toast.LENGTH_SHORT).show();
    }
}