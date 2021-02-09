package com.example.amrgamal.weartracker;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.amrgamal.weartracker.adapters.Drugs_Adapter;
import com.example.amrgamal.weartracker.adapters.User_Adapter;
import com.example.amrgamal.weartracker.models.Drug_Model;
import com.example.amrgamal.weartracker.models.Time;
import com.example.amrgamal.weartracker.models.WearUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class Insert_Drugs_Activity extends AppCompatActivity implements ClickListener {


    private List<Drug_Model> drugList = new ArrayList<Drug_Model>();
    private RecyclerView recyclerView;
    private Drugs_Adapter mAdapter;
    String userID;

    private DatabaseReference drugs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert__drugs_);

        recyclerView = (RecyclerView) findViewById(R.id.drugs_recycle_view);

//        drugList.add(new Drug_Model("drugName","2:30 AM"));
        userID = getIntent().getStringExtra("user_id");
        Log.v("aaaaaaaaaaa",userID);

        mAdapter = new Drugs_Adapter(drugList,this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        final List<Time> messageList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drugs").child(userID);
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                            try {
                                collectUserData((Map<String,Object>) dataSnapshot.getValue());


                        Time message = dataSnapshot.getValue(Time.class);
                        messageList.add(message);
                            }catch (Exception e){
                                e.getStackTrace();
                                Toast.makeText(Insert_Drugs_Activity.this,"لم يتم اضافه دواء حتي الان",Toast.LENGTH_LONG).show();
                            }

                        Log.v("aaaaaaaaaaaaaa",messageList.size()+"");
                        Log.v("aaaaaaaaaaaaaa",dataSnapshot.getValue()+"");
                        Log.v("aaaaaaaaaaaaaa",dataSnapshot.getKey()+"");
                        Log.v("aaaaaaaaaaaaaa",dataSnapshot.getChildrenCount()+"");
                        Log.v("aaaaaaaaaaaaaa",dataSnapshot.getChildren()+"");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });



        recyclerView.setAdapter(mAdapter);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(Insert_Drugs_Activity.this,AddDrugs.class);
                i.putExtra("user_id",userID);
                startActivity(i);


            }
        });
    }

    Calendar c;
    String drugsTime = "";
     Drug_Model finalDrug_model = new Drug_Model() ;
    private void collectUserData(Map<String,Object> users) {

        final ArrayList<Drug_Model> list = new ArrayList<>();
        final ArrayList<Drug_Model> listDrugs = new ArrayList<>();
        mAdapter = new Drugs_Adapter(listDrugs,this);
        recyclerView.setAdapter(mAdapter);

//        final ArrayList<Time> listTime = new ArrayList<>();
        Drug_Model drug_model;

//int x = 0;
        //iterate through each user, ignoring their UID
        for (final Map.Entry<String, Object> entry : users.entrySet()){
//            Map singleUser = (Map) entry.getValue();

            //Get user map
            final String time="";
//            drug_model = new Drug_Model();
//            drug_model.setdDrug(entry.getKey());
//            ArrayList<Time>t;
//            t= (ArrayList<Time>) entry.getValue();
//                drug_model.setdTime(entry.getValue()+"");



            c = Calendar.getInstance();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drugs")
                    .child(userID).child(entry.getKey());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Time time1 = dataSnapshot.getValue(Time.class);
//                    listTime.add(time1);
            Log.v("lllllllllllllllll", dataSnapshot.getKey()+"");
            Log.v("lllllllllllllllll", dataSnapshot.getValue().toString()+"");
            Log.v("lllllllllllllllll", dataSnapshot.getValue().getClass()+"");
                    Log.v("xxxxxxxxxxxxxx", "after");

                    GenericTypeIndicator<List<Time>> t = new GenericTypeIndicator<List<Time>>() {};
                    List<Time> questionList = dataSnapshot.getValue(t);
                    Log.v("xxxxxxxxxxxxxx", questionList.size()+"");
                    Log.v("xxxxxxxxxxxxxx", "before");
                    finalDrug_model = new Drug_Model();
                    drugsTime = "";
                    for (int i = 0 ; i < questionList.size() ; i++){
                        Log.v("zzzzzzzz", questionList.get(i).getMinute()+" Minute");
                        Log.v("zzzzzzzz", questionList.get(i).getHour()+" Hour");
                        c.set(Calendar.HOUR_OF_DAY, questionList.get(i).getHour());
                        c.set(Calendar.MINUTE, questionList.get(i).getMinute());

//                DateFormat date = new SimpleDateFormat("HH:mm a");
////                date.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
//                String localTime = date.format(calendar);

                        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
//                System.out.println(format.format(calendar.getTime()));

                        drugsTime +=format.format(c.getTime());
                        drugsTime += "\n";
                    }

                    finalDrug_model.setdTime(drugsTime);
                    finalDrug_model.setdDrug(entry.getKey());

                    listDrugs.add(finalDrug_model);
                    Log.v("hhhhhhhhhhhhhhhhhhh", listDrugs.get(0).getdTime());

                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


//            Log.v("lllllllllllllllll", listTime.get(0).getHour()+"");
//            Log.v("lllllllllllllllll", listTime.get(0).getMinute()+"");
//            Log.v("lllllllllllllllll", listTime.size()+"");


        }

        Log.v("hhhhhhhhhhhhhhhhhhh", "test");
        Log.v("hhhhhhhhhhhhhhhhhhh", listDrugs.size()+"");
//        Log.v("hhhhhhhhhhhhhhhhhhh", listDrugs.get(0).getdTime());
//        Log.v("hhhhhhhhhhhhhhhhhhh", listDrugs.get(0).getdDrug());


        System.out.println("*********************");



    }

    @Override
    public void onClickB(View view, String k) {

        try{
            drugs = FirebaseDatabase.getInstance().getReference().child("Drugs").child(userID).child(k);

        } catch (Exception e) {
            Toast.makeText(this," لا يوجد معلوملت لمسحها حاليا  ",Toast.LENGTH_LONG).show();
            finish();

        }


        drugs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().removeValue();
                Toast.makeText(getBaseContext()," تم مسح العنصر  ",Toast.LENGTH_LONG).show();
                mAdapter.notifyDataSetChanged();


                final List<Time> messageList = new ArrayList<>();

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drugs").child(userID);
                reference.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                try {
                                    collectUserData((Map<String,Object>) dataSnapshot.getValue());


                                    Time message = dataSnapshot.getValue(Time.class);
                                    messageList.add(message);
                                }catch (Exception e){
                                    e.getStackTrace();
                                    Toast.makeText(Insert_Drugs_Activity.this,"لم يتم اضافه دواء حتي الان",Toast.LENGTH_LONG).show();
                                }

                                Log.v("aaaaaaaaaaaaaa",messageList.size()+"");
                                Log.v("aaaaaaaaaaaaaa",dataSnapshot.getValue()+"");
                                Log.v("aaaaaaaaaaaaaa",dataSnapshot.getKey()+"");
                                Log.v("aaaaaaaaaaaaaa",dataSnapshot.getChildrenCount()+"");
                                Log.v("aaaaaaaaaaaaaa",dataSnapshot.getChildren()+"");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }



                        });


                            final ArrayList<Drug_Model> listDrugs = new ArrayList<>();
                            mAdapter = new Drugs_Adapter(listDrugs,Insert_Drugs_Activity.this);
                            recyclerView.setAdapter(mAdapter);




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
}
