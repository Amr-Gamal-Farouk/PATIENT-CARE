package com.example.amrgamal.testwear.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amrgamal.testwear.ClickListener;
import com.example.amrgamal.testwear.DrugImage;
import com.example.amrgamal.testwear.R;
import com.example.amrgamal.testwear.adapter.Drugs_Adapter;
import com.example.amrgamal.testwear.model.Drug_Model;
import com.example.amrgamal.testwear.model.Time;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by amrga on 05/02/2018.
 */

public class DrugsFragment extends Fragment implements ClickListener {

    private RecyclerView recyclerView;
    private Drugs_Adapter mAdapter;

    private FirebaseAuth auth;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.drugs_fragment, container, false);


        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        recyclerView = (RecyclerView) view.findViewById(R.id.drugs_recycle_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        loadData();
        recyclerView.setAdapter(mAdapter);

        return view;
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
                        }catch (Exception e){
                            e.getStackTrace();
                            Toast.makeText(getContext(),"لم يتم اضافه دواء حتي الان", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });


    }

    Calendar c;
    String drugsTime = "";
    Drug_Model finalDrug_model;
    List<String> list=new ArrayList<>();
    private void collectTimeData(Map<String,Object> users) {
        final ArrayList<Drug_Model> listDrugs = new ArrayList<>();
        mAdapter = new Drugs_Adapter(listDrugs,currentUserId,getContext(),this);
        recyclerView.setAdapter(mAdapter);

        for (final Map.Entry<String, Object> entry : users.entrySet()){
            c = Calendar.getInstance();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drugs")
                    .child(currentUserId).child(entry.getKey());
            list.add(entry.getKey());
            Log.v("KKKKKKKKK__",list+"");
            Log.v("KKKKKKKKK__",entry.getKey());



            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<List<Time>> t = new GenericTypeIndicator<List<Time>>() {};
                    List<Time> questionList = dataSnapshot.getValue(t);
//                    drugsTime = "";
                    for (int i = 0 ; i < questionList.size() ; i++){
                        Log.v("KKKKKKKKK__",questionList.size()+"");
                        c.set(Calendar.HOUR_OF_DAY, questionList.get(i).getHour());
                        c.set(Calendar.MINUTE, questionList.get(i).getMinute());
                        finalDrug_model = new Drug_Model();
                        finalDrug_model.setMillsecond(c.getTimeInMillis());
                        drugsTime = "";
                        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
                        drugsTime +=format.format(c.getTime());
                        finalDrug_model.setdTime(drugsTime);
                        finalDrug_model.setdDrug(entry.getKey());
                        listDrugs.add(finalDrug_model);

                    }

                    Collections.sort(listDrugs, new Comparator<Drug_Model>() {
                        public int compare(Drug_Model o1, Drug_Model o2) {
                            if ((o1.getMillsecond())>=(o2.getMillsecond()))return 1;
                            else return -1;

                        }
                    });

                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onClickB(View view, String k,String t) {
        Intent i=new Intent(getContext(), DrugImage.class);
        i.putExtra("Drug",k);
        i.putExtra("userId",currentUserId);
        i.putExtra("drugTime",t);
        startActivity(i);

    }
}
