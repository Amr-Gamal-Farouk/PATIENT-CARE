package com.example.amrgamal.testwear;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DrugImage extends AppCompatActivity {
    String drugName,userId,time;
    ImageView imageView;
    TextView user_drug,user_drug_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_image);

        Bundle intent=getIntent().getExtras();
        drugName=intent.getString("Drug");
        userId=intent.getString("userId");
        time=intent.getString("drugTime");
        imageView=(ImageView) findViewById(R.id.drug_image);
        user_drug=(TextView) findViewById(R.id.user_drug);
        user_drug_time=(TextView)  findViewById(R.id.user_drug_time);


        DatabaseReference imReference = FirebaseDatabase.getInstance().getReference("Images")
                .child(userId);
        imReference.child(drugName).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String stUri;
                        stUri = dataSnapshot.getValue().toString();
                        Uri uri = Uri.parse(stUri);
                        user_drug.setText(drugName);
                        user_drug_time.setText(time);
                        Picasso.with(imageView.getContext()).load(uri).into(imageView);


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
}
