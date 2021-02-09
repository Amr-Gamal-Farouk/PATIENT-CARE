package com.example.amrgamal.weartracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Drugs_Report extends AppCompatActivity {

    TextView textView;
    private DatabaseReference drugs;
    String userID;

    Button deleteReportButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drugs_reports);

        deleteReportButton=(Button) findViewById(R.id.report_drugs_clear) ;
        userID = getIntent().getStringExtra("user_id");
        Log.v("aaaaaaaa",userID+"");
        textView=(TextView) findViewById(R.id.text_report);
        textView.setText("Drugs");
        try{
            drugs = FirebaseDatabase.getInstance().getReference().child("Report").child(userID);

        } catch (Exception e) {
            Toast.makeText(this," لا يوجد معلوملت لعرضها حاليا  ",Toast.LENGTH_LONG).show();
            finish();

        }


        deleteReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try {


                    drugs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            dataSnapshot.getRef().removeValue();
                            textView.setText("");
                            Toast.makeText(view.getContext()," تم مسح المعلومات  ",Toast.LENGTH_LONG).show();




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }catch (Exception e){
                    Toast.makeText(view.getContext()," لا يوجد معلومات لمسحها ",Toast.LENGTH_LONG).show();
                }



            }
        });

        try {

            drugs.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        textView.setText(dataSnapshot.getValue().toString() + "");
                    }catch (Exception e){
                        Toast.makeText(getBaseContext()," لا يوجد معلوملت لعرضها حاليا  ",Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            Toast.makeText(this," لا يوجد معلوملت لعرضها حاليا  ",Toast.LENGTH_LONG).show();
        }


    }
}
