package com.example.amrgamal.weartracker;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class Dangerous extends AppCompatActivity {
    MediaPlayer mp;
    private FirebaseAuth auth;
    private DatabaseReference mCheckType;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangerous);

        textView = (TextView) findViewById(R.id.textView);
        auth = FirebaseAuth.getInstance();
        final String currentUserId = auth.getCurrentUser().getUid();

        mCheckType = FirebaseDatabase.getInstance().getReference().child("Ding");

        mCheckType.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                 String n = String.valueOf(dataSnapshot.getValue().toString());
                 String m="";
                 for (int i=1;i<n.length()/2;i++){
                     m+=n.charAt(i);
                 }
                 textView.setText(n);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
            }
        });

        mp = MediaPlayer.create(this, R.raw.dang_alarm);
        mp.start();


        Button one = (Button) this.findViewById(R.id.button1);
        one.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                mp.stop();
                finish();
            }
        });



    }
}
