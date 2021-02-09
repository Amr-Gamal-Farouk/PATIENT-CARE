package com.example.amrgamal.testwear.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;

import com.example.amrgamal.testwear.HomeActivity;
import com.example.amrgamal.testwear.NextDrugs;
import com.example.amrgamal.testwear.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by master on 14/10/2016.
 */
public class AlarmRecever extends BroadcastReceiver {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int lanSettings;
    NotificationCompat.Builder builder;
    int alarm_num=0;

    boolean b = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarm_num=0;
//        alarm_num = intent.getIntExtra("alarm_num",0);
//        alarm_num = intent.getIntExtra("alarm_num",0);
//        Log.v("aaaaaaaaa",alarm_num+"");
//        Log.v("aaaaaaaaa","test");
//
//        alarm_num = intent.getIntExtra("aaa",0);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        List<PendingIntent> notification = new ArrayList();

        sharedpreferences = context.getSharedPreferences("check", Context.MODE_PRIVATE);
        editor= sharedpreferences.edit();
//
////        alarm_num = sharedpreferences.getInt("num", 0);
        lanSettings = sharedpreferences.getInt("chTime", 0);
//        for (int i=0 ; i<alarm_num ; i++){
//            notification.set(i , PendingIntent.getActivity(context,i,new Intent(context,MainActivity.class),0));
//            Log.v("aaaaaaaaa","for");
//        }


        while (b){

            try {
                notification.set(alarm_num , PendingIntent.getActivity(context,alarm_num,new Intent(context,NextDrugs.class),0));
                alarm_num++;
            }catch (Exception e){

                b = false;
            }

        }

//        PendingIntent notification = PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0);

        if (lanSettings == 0){
            editor.putInt("chTime", 1);
            editor.commit();
            builder = new NotificationCompat.Builder(context)
                    .setContentTitle("اشعارات")
                    .setContentText("تم تشغيل خاصيه الاشعارات")
                    .setSmallIcon(R.mipmap.ic_launcher);
        }else {


            builder = new NotificationCompat.Builder(context)
                    .setContentTitle("الدواء")
                    .setContentText("الان موعد الدواء تاكد من اخذ الدواء")
                    .setSmallIcon(R.mipmap.ic_launcher);
        }
        for (int i=0 ; i<alarm_num ; i++){
            builder.setContentIntent(notification.get(i));
        }
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        manager.cancel(1);
        manager.notify(1,builder.build());
//        MediaPlayer mp= MediaPlayer.create(context, R.raw.voice);
//        mp.start();
    }
}