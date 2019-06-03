package com.technion.android.joblin;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import static com.technion.android.joblin.DatabaseUtils.TOKENS_COLLECTION_NAME;
import static com.technion.android.joblin.DatabaseUtils.TOKEN_KEY;
import static com.technion.android.joblin.DatabaseUtils.USERS_COLLECTION_NAME;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int BROADCAST_NOTIFICATION_ID = 1;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection(USERS_COLLECTION_NAME);

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationBody = "";
        String notificationTitle = "";
        String notificationData = "";
        try{
            notificationData = remoteMessage.getData().toString();
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }catch (NullPointerException e){
            Log.e(TAG, "onMessageReceived: NullPointerException: " + e.getMessage() );
        }
        Log.d(TAG, "onMessageReceived: data: " + notificationData);
        Log.d(TAG, "onMessageReceived: notification body: " + notificationBody);
        Log.d(TAG, "onMessageReceived: notification title: " + notificationTitle);


//        String dataType = remoteMessage.getData().get(getString(R.string.data_type));
//        if(dataType.equals(getString(R.string.direct_message))){
//            Log.d(TAG, "onMessageReceived: new incoming message.");
//            String title = remoteMessage.getData().get(getString(R.string.data_title));
//            String message = remoteMessage.getData().get(getString(R.string.data_message));
//            String messageId = remoteMessage.getData().get(getString(R.string.data_message_id));
//            sendMessageNotification(title, message, messageId);
//     }
    }

    /**
     * Build a push notification for a chat message
     * @param title
     * @param message
     */
    private void sendMessageNotification(String title, String message, String messageId){

        // Once message recieved build a message to be sent to the current system tray
        // Or in our case display a match animation

    }


    private int buildNotificationId(String id){
        Log.d(TAG, "buildNotificationId: building a notification id.");

        int notificationId = 0;
        for(int i = 0; i < 9; i++){
            notificationId = notificationId + id.charAt(0);
        }
        Log.d(TAG, "buildNotificationId: id: " + id);
        Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
        return notificationId;
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        addTokenData(mAuth.getCurrentUser().getEmail(),s);
    }

    public void addTokenData(String email, String token) {
        Map<String, Object> userToken = new HashMap<>();
        userToken.put(TOKEN_KEY, token);

        usersCollection.document(email).collection(TOKENS_COLLECTION_NAME).document(token).set(userToken)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


}