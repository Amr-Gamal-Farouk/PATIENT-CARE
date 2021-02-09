package com.example.amrgamal.weartracker.adapters;

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.amrgamal.weartracker.R;
import com.example.amrgamal.weartracker.fragments.User_Fragment;
import com.example.amrgamal.weartracker.models.User_Model;
import com.example.amrgamal.weartracker.models.WearUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amrga on 02/02/2018.
 */

public class User_Adapter  extends RecyclerView.Adapter<User_Adapter.MyViewHolder> {

    private List<WearUser> userList;
    private List<String > userKeys=new ArrayList<>();
    private final User_Fragment.OnListFragmentInteractionListener mListener;
    private final Context context;




    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Name, Location, Time;
        public final View mView;
        public MyViewHolder(View view) {
            super(view);
            mView=view;
            Name = (TextView) view.findViewById(R.id.user_name);
            Location = (TextView) view.findViewById(R.id.user_location);
            Time = (TextView) view.findViewById(R.id.user_location_time);
        }
    }

    public User_Adapter(List<WearUser> userList, Context context, User_Fragment.OnListFragmentInteractionListener mListener,String k) {
        this.userList = userList;
        this.context = context;
        this.mListener = mListener;

//        initUserID(k);
    }

    private void initUserID(String k) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Child").child(k);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    userKeys.add(child.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);

        return new MyViewHolder(itemView);
    }
    int p;
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        WearUser user = userList.get(position);
        holder.Name.setText(user.getName());
        Log.v("vvvvvvvvvvvvvvvvvvvv",user.getAdress());
        Log.v("vvvvvvvvvvvvvvvvvvvv",user.getName());
        Log.v("vvvvvvvvvvvvvvvvvvvv",user.getTime());
        holder.Location.setText(user.getAdress());
        holder.Time.setText(user.getTime());
        userKeys.add(user.getKey());



        holder.mView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    p = userKeys.size()-position-1;
                    mListener.onListClick(view,userKeys.get(position));
                }
            }
        });


    }



    @Override
    public int getItemCount() {
        return userList.size();
    }


}
