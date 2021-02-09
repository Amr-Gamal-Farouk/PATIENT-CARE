package com.example.amrgamal.weartracker;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by amrga on 10/02/2018.
 */

public class LocationHandlerReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;

    public LocationHandlerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (LocationResult.hasResult(intent)) {
//            LocationResult locationResult = LocationResult.extractResult(intent);
//            Location mLocation = locationResult.getLastLocation();
//            Log.i("Intent Service", mLocation.toString());

            setupNotification(context, "test");
        }



    //Setup Notification
    private void setupNotification(Context context, String  location) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, Home_Activity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.online_icon)
                .setContentTitle("test")
                .setContentText("Lat: hhhhhhh");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setLocalOnly(false);
        mBuilder.setOngoing(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
}