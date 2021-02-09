package com.example.amrgamal.weartracker;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by amrga on 10/02/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "sssssssssssssss";
    @Override

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.


            Intent dialogIntent = new Intent(this, Dangerous.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);

            Log.d(TAG, "test");
            Log.d(TAG, "From: " + remoteMessage.getTo());
//            Log.d(TAG, "From: " + remoteMessage.getNotification().getBody().toString());
//            Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

    }
}