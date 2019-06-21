package com.technion.android.joblin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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

        //Should display pop up
        String title = intent.getStringExtra("Title");
        String body = intent.getStringExtra("Body");

        String[] splitted = title.split(" ");
        //A new message
        if( splitted[splitted.length - 1].equals("message"))  {
            // Messages shouldn't recieve a pop-up
        } else {
            if(RecMainActivity.recrSuperLiked || CanMainActivity.candSuperLiked){
            } else {
                Utils.newMatchPopUp(receivingActivity,title,splitted[0]);
            }
        }
    }
}