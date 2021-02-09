package com.example.amrgamal.testwear.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amrgamal.testwear.GetTimeAgo;
import com.example.amrgamal.testwear.OnListFragmentInteractionListener;
import com.example.amrgamal.testwear.R;
import com.example.amrgamal.testwear.model.Messages;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


/**
 * Created by amrga on 04/02/2018.
 */

public class Message_Adapter2 extends RecyclerView.Adapter<Message_Adapter2.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mType;

    private final OnListFragmentInteractionListener mListener;




    Context context;

    public Message_Adapter2(List<Messages> mMessageList, Context context, OnListFragmentInteractionListener mListener) {
        this.mListener=mListener;
        this.context = context;
        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView messageText,mTime;
//        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);
            mView=view;


            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            mTime = (TextView) view.findViewById(R.id.time_text_layout);

//            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);



        }


    }
    String type;
    GetTimeAgo getTimeAgo = new GetTimeAgo();
    long lastTime;
    int arr[] = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,
            R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9};
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        final String from_user = c.getFrom();
        String message_type = c.getType();

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (null != mListener) {

                }
            }
        });





        mType = FirebaseDatabase.getInstance().getReference().child("Type");
        mType.addValueEventListener(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                type = dataSnapshot.child(from_user).getValue().toString();
                Log.v("kkkkkkkkkkkkkkkkkkkkkkkkkkkk",type);
                Log.v("kkkkkkkkkkkkkkkkkkkkkkkkkkkk",from_user);


                mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(type).child(from_user);

                mUserDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("name").getValue().toString();
//                String image = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.displayName.setText(name);

//                Picasso.with(viewHolder.profileImage.getContext()).load(image)
//                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());



            lastTime = c.getTime();

            viewHolder.mTime.setText(getTimeAgo.getTimeAgo(lastTime, context));


            viewHolder.messageImage.setVisibility(View.INVISIBLE);



        } else {
            lastTime = c.getTime();

            viewHolder.mTime.setText(getTimeAgo.getTimeAgo(lastTime, context));

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            try {
                viewHolder.messageImage.setBackgroundResource(arr[Integer.parseInt(c.getMessage())]);

            }catch (Exception e){}
//            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
//                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }






}
