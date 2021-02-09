package com.example.amrgamal.weartracker.fragments;

/**
 * Created by amrga on 01/02/2018.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.amrgamal.weartracker.GetTimeAgo;
import com.example.amrgamal.weartracker.R;
import com.example.amrgamal.weartracker.adapters.Chat_User_Adapter;
import com.example.amrgamal.weartracker.adapters.User_Adapter;
import com.example.amrgamal.weartracker.models.Chat_Model;
import com.example.amrgamal.weartracker.models.WearUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Chat_Fragment extends Fragment{

    private RecyclerView recyclerView;
    private Chat_User_Adapter mAdapter;

    private FirebaseAuth auth;
    String currentUserId;
    private DatabaseReference mGetData;
    private DatabaseReference mUsersDatabase;

    public Chat_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.chat_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.chat_recycle_view);

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Child");
        mGetData = FirebaseDatabase.getInstance().getReference().child("");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);


        return view;
    }


    private void collectUserData(Map<String,Object> users) {

        ArrayList<Chat_Model> list = new ArrayList<>();
        Chat_Model chatModel;
        GetTimeAgo getTimeAgo = new GetTimeAgo();
        long lastTime;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            chatModel = new Chat_Model();
            chatModel.setName(singleUser.get("name").toString());
            chatModel.setKey(entry.getKey());
            lastTime = Long.parseLong(singleUser.get("time").toString());
            chatModel.setLastMessage("hello");
            chatModel.setLastMessagetime(getTimeAgo.getTimeAgo(lastTime, getContext()));
            list.add(chatModel);

            mAdapter = new Chat_User_Adapter(list);

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);


        }
    }


    @Override
    public void onStart() {
        super.onStart();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Child").child(currentUserId);
        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        try {
                            collectUserData((Map<String, Object>) dataSnapshot.getValue());
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }


}
