package com.example.amrgamal.testwear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amrgamal.testwear.adapter.Drugs_Adapter;
import com.example.amrgamal.testwear.model.Drug_Model;
import com.example.amrgamal.testwear.model.PrevModel;
import com.example.amrgamal.testwear.model.Time;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NextDrugs extends AppCompatActivity {

    TextView prevDrugs;
    Button addTime;
    final ArrayList<Drug_Model> listDrugs = new ArrayList<>();
    String currentUserId;
    private FirebaseAuth auth;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    ArrayList<PrevModel> prevList = new ArrayList<>();
    int cTimeInMinute =0;
    int m =0;
    int dTime = 0;
    String tTime,tDrug;
    String data = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_drugs);

        auth = FirebaseAuth.getInstance();
        prevDrugs = (TextView) findViewById(R.id.prevDrugs);
        addTime = (Button) findViewById(R.id.addTime);
        addTime.setEnabled(false);
        sharedpreferences = getSharedPreferences("checkBTN", Context.MODE_PRIVATE);
        editor= sharedpreferences.edit();


        currentUserId = auth.getCurrentUser().getUid();


        addTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("pppppppppppppp","cTimeInMinute = "+cTimeInMinute);
                Log.v("qqqqqqqqqqqqq","1 = ");

                editor.putInt("dTime", m);
                editor.commit();
                addTime.setEnabled(false);
                addTime.setText("تم تاكيد اخذ الدواء");



                try {
                    Log.v("qqqqqqqqqqqqq","2 = ");

                    DatabaseReference mCheckType = FirebaseDatabase.getInstance().getReference().child("Report").child(currentUserId);
                    mCheckType.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.v("qqqqqqqqqqqqq","3 = ");

                            data="";
                            if (!String.valueOf(dataSnapshot.getValue()).equals(null)) {

                                data = String.valueOf(dataSnapshot.getValue());
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy E hh:mm a z");
                            String currentDateandTime = sdf.format(new Date());
                            data="تم اخذ دواء "+tDrug+" في التاريخ  "+currentDateandTime+"\n";
//                            data+="تم اخذ دواء "+tDrug+" في التاريخ  "+currentDateandTime+"\n";

                            Log.v("ssssssss","ssssss");
                            data.replace("null","");
                            DatabaseReference mDeviceType = FirebaseDatabase.getInstance()
                                    .getReference().child("Report").child(currentUserId);
                            mDeviceType.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.v("qqqqqqqqqqqqq","4 = ");

//                                    startActivity(new Intent(NextDrugs.this,HomeActivity.class));
//                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
                        }
                    });
                } catch (Exception e){}

                Log.v("qqqqqqqqqqqqq","5 = ");

            }
        });

        loadData();
    }



    final List<Time> messageList = new ArrayList<>();
    public void loadData(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drugs").child(currentUserId);
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        try {
                            collectTimeData((Map<String,Object>) dataSnapshot.getValue());


                            Time message = dataSnapshot.getValue(Time.class);
                            messageList.add(message);
                            Log.v("nnnnnnnnnnnnnnnnnn",message.getHour()+":"+message.getMinute());
                            Log.v("nnnnnnnnnnnnnnnnnn","kjhh");
                        }catch (Exception e){
                            e.getStackTrace();
                            Toast.makeText(NextDrugs.this,"لم يتم اضافه دواء حتي الان", Toast.LENGTH_LONG).show();
                        }

                        Log.v("fffffffffffff",messageList.size()+"");
                        Log.v("fffffffffffff",dataSnapshot.getValue()+"");
                        Log.v("fffffffffffff",dataSnapshot.getKey()+"");
                        Log.v("fffffffffffff",dataSnapshot.getChildrenCount()+"");
                        Log.v("fffffffffffff",dataSnapshot.getChildren()+"");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


    }

    PrevModel prevModel;
    private void collectTimeData(Map<String,Object> users) {



//        final ArrayList<Drug_Model> list = new ArrayList<>();
        final ArrayList<Drug_Model> listDrugs = new ArrayList<>();



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



//            c = Calendar.getInstance();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drugs")
                    .child(currentUserId).child(entry.getKey());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Time time1 = dataSnapshot.getValue(Time.class);
//                    listTime.add(time1);
                    Log.v("fffffffffffff", dataSnapshot.getKey()+"");
                    Log.v("fffffffffffff", dataSnapshot.getValue().toString()+"");
                    Log.v("fffffffffffff", dataSnapshot.getValue().getClass()+"");
                    Log.v("fffffffffffff", "after");

                    GenericTypeIndicator<List<Time>> t = new GenericTypeIndicator<List<Time>>() {};
                    List<Time> questionList = dataSnapshot.getValue(t);
                    Log.v("fffffffffffff", questionList.size()+"");
                    Log.v("fffffffffffff", "before");
//                    drugsTime = "";
                    for (int i = 0 ; i < questionList.size() ; i++){

                        prevModel = new PrevModel();
                        Log.v("iiiiiiiiiii", questionList.get(i).getMinute()+" Minute");
                        Log.v("iiiiiiiiiii", questionList.get(i).getHour()+" Hour");
                        Log.v("iiiiiiiiiii", dataSnapshot.getKey());

                        prevModel.setDrugName(dataSnapshot.getKey());
                        prevModel.setTimeInMinute((questionList.get(i).getHour()*60)+questionList.get(i).getMinute());

                        prevList.add(prevModel);

                    }

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    cTimeInMinute = hour*60 +minute;




                    Collections.sort(prevList, new Comparator<PrevModel>() {
                        public int compare(PrevModel o1, PrevModel o2) {
                            if ((o1.getTimeInMinute())>=(o2.getTimeInMinute()))return 1;
                            else return -1;

                        }
                    });


                    Log.v("sssssssssss",prevList.size()+"");
                    prevDrugs.setText(prevList.size()+" num");
                    String n = "";
                    m = 0;
                    if (cTimeInMinute < prevList.get(0).getTimeInMinute()){
                        try {
                            m = prevList.get(prevList.size()-1).getTimeInMinute();
                            n = prevList.get(prevList.size()-1).getDrugName();


                            SimpleDateFormat sdf = new SimpleDateFormat("mm");

                            try {
                                Date dt = sdf.parse(m+"");
                                sdf = new SimpleDateFormat("HH:mm");
                                System.out.println(sdf.format(dt));

                                prevDrugs.setText(n+"\n"+sdf.format(dt));

                                tTime = sdf.format(dt)+"";
                                tDrug = prevList.get(0).getDrugName();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }catch (Exception e){

                            prevDrugs.setText("لم يتم اضافه ادويه حتي الان");
                        }
                    } else {
                        for (int i = 0; i < prevList.size(); i++) {

                            if (cTimeInMinute > prevList.get(i).getTimeInMinute()){
                                m = prevList.get(i).getTimeInMinute();

                                n = prevList.get(i).getDrugName();
                                tDrug = prevList.get(i).getDrugName();
                            }else
                                break;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("mm");

                        try {
                            Date dt = sdf.parse(m+"");
                            sdf = new SimpleDateFormat("HH:mm");
                            System.out.println(sdf.format(dt));

                            prevDrugs.setText(n+"\n"+sdf.format(dt));
                            tTime = sdf.format(dt)+"";

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    Collections.sort(listDrugs, new Comparator<Drug_Model>() {
                        public int compare(Drug_Model o1, Drug_Model o2) {
                            if ((o1.getMillsecond())>=(o2.getMillsecond()))return 1;
                            else return -1;

                        }
                    });
//                    checkPrev(listDrugs);



                    dTime = sharedpreferences.getInt("dTime", 0);

                    Log.v("pppppppppppppp","dTime = "+dTime);
                    Log.v("pppppppppppppp","m = "+m);

                    if (dTime != m){
                        addTime.setEnabled(true);
                        addTime.setText(" تاكيد اخذ الدواء");
                    }else {
                        addTime.setEnabled(false);
                        addTime.setText("تم تاكيد اخذ الدواء");
                    }


//                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


//            Log.v("lllllllllllllllll", listTime.get(0).getHour()+"");
//            Log.v("lllllllllllllllll", listTime.get(0).getMinute()+"");
//            Log.v("lllllllllllllllll", listTime.size()+"");


        }

        Log.v("fffffffffffff", "test");
        Log.v("fffffffffffff", listDrugs.size()+"");
//        Log.v("hhhhhhhhhhhhhhhhhhh", listDrugs.get(0).getdTime());
//        Log.v("hhhhhhhhhhhhhhhhhhh", listDrugs.get(0).getdDrug());


        System.out.println("*********************");




//        checkPrev(listDrugs);
    }

    private void checkPrev(List<Drug_Model> ss) {

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);



        SimpleDateFormat format = new SimpleDateFormat("hh:mm");

        Log.v("bbbbbbbbbbb current",format.format(calendar.getTime()));

        Log.v("bbbbbbbbbbb",ss.size()+"");
        for (int i = 0 ; i < ss.size() ; i++){
            Log.v("bbbbbbbbbbb",ss.get(i).getdTime());

        }
    }
}
