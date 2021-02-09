package com.example.amrgamal.testwear.adapter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.amrgamal.testwear.ClickListener;
import com.example.amrgamal.testwear.R;
import com.example.amrgamal.testwear.model.Drug_Model;
import com.example.amrgamal.testwear.service.AlarmRecever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by amrga on 04/02/2018.
 */

public class Drugs_Adapter extends RecyclerView.Adapter<Drugs_Adapter.MyViewHolder>  {


    private List<Drug_Model> drugList;
    String currentId;
    ClickListener mClickListener;



    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView user_drug, user_drug_time;
        LinearLayout item_linear;
//        public Button drugs_done;



        public MyViewHolder(View view) {
            super(view);
            user_drug = (TextView) view.findViewById(R.id.user_drug);
            user_drug_time = (TextView) view.findViewById(R.id.user_drug_time);
            item_linear = (LinearLayout) view.findViewById(R.id.item_linear);


        }
    }
    public Drugs_Adapter(List<Drug_Model> drugList,String currentId,Context context,ClickListener mClickListener) {
        this.drugList = drugList;
        this.currentId = currentId;
        this.context =context;
        this.mClickListener=mClickListener;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drugs_item, parent, false);

        return new MyViewHolder(itemView);
    }
    long time;
    String data="";
    String formattedDate1;
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        time = System.currentTimeMillis();

        final Drug_Model userDrug = drugList.get(position);
        holder.user_drug.setText(userDrug.getdDrug());
        holder.user_drug_time.setText(userDrug.getdTime());
//         Picasso.with(context).load(userDrug.getUri()).into(holder.image);
        holder.item_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onClickB(view,userDrug.getdDrug(),userDrug.getdTime());
            }
        });


        SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a");
        formattedDate1 = df1.format(time);
        SimpleDateFormat df2 = new SimpleDateFormat("hh:mm a");
        String formattedDate2 = df2.format(userDrug.getMillsecond());
        Log.v("aaaaaaaaaaaaaaaa","current = "+formattedDate2);
        Log.v("aaaaaaaaaaaaaaaa","now = "+formattedDate1);

        SimpleDateFormat df3 = new SimpleDateFormat("hh");
        String formattedDate3 = df2.format(userDrug.getMillsecond());
        Log.v("ddddddddddddddd","current = "+formattedDate3);
        SimpleDateFormat df4 = new SimpleDateFormat("mm");
        String formattedDate4 = df2.format(userDrug.getMillsecond());
        Log.v("ddddddddddddddd","current = "+formattedDate4);

    }


    @Override
    public int getItemCount() {
        return drugList.size();
    }



}


