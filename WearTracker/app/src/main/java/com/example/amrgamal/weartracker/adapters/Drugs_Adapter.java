package com.example.amrgamal.weartracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amrgamal.weartracker.ClickListener;
import com.example.amrgamal.weartracker.Insert_Drugs_Activity;
import com.example.amrgamal.weartracker.R;
import com.example.amrgamal.weartracker.models.Drug_Model;

import java.security.AccessControlContext;
import java.util.List;

/**
 * Created by amrga on 04/02/2018.
 */

public class Drugs_Adapter extends RecyclerView.Adapter<Drugs_Adapter.MyViewHolder>  {

    ClickListener mClickListener;
    private List<Drug_Model> drugList;
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView user_drug, user_drug_time;
        LinearLayout item_linear;


        View mView;


        public MyViewHolder(View view) {
            super(view);
            item_linear = (LinearLayout) view.findViewById(R.id.item_linear);
            user_drug = (TextView) view.findViewById(R.id.user_drug);
            user_drug_time = (TextView) view.findViewById(R.id.user_drug_time);
            this.mView=view;
        }
    }
    public Drugs_Adapter(List<Drug_Model> drugList,ClickListener mClickListener) {
        this.drugList = drugList;
        this.mClickListener = mClickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drugs_item, parent, false);

        return new Drugs_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Drug_Model userDrug = drugList.get(position);
        holder.user_drug.setText(userDrug.getdDrug());
        holder.user_drug_time.setText(userDrug.getdTime());
        holder.item_linear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onClickB(view,userDrug.getdDrug());
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return drugList.size();
    }


}


