package com.example.amrgamal.weartracker.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amrgamal.weartracker.GetTimeAgo;
import com.example.amrgamal.weartracker.R;
import com.example.amrgamal.weartracker.models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;


//import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

/**
 * Created by amrga on 04/02/2018.
 */

public class Message_Adapter extends RecyclerView.Adapter<Message_Adapter.MessageViewHolder>{


    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mType;
    Context context;

    public Message_Adapter(List<Messages> mMessageList,Context context) {
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

        public TextView messageText,mTime;
//        public CircleImageView profileImage;
        public TextView displayName;
        public TextView time_text_layout;
        public ImageView messageImage;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            mTime = (TextView) view.findViewById(R.id.time_text_layout);
//            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            time_text_layout = (TextView) view.findViewById(R.id.time_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

        }
    }
    String type;
    GetTimeAgo getTimeAgo = new GetTimeAgo();
    long lastTime;
    int x;

    int arr[] = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,
            R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9};
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages c = mMessageList.get(i);

        final String from_user = c.getFrom();
        String message_type = c.getType();


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


            Picasso.with(context).load(R.drawable.empty).into(viewHolder.messageImage);
//            viewHolder.messageImage.setVisibility(View.INVISIBLE);



        } else {
            lastTime = c.getTime();

            viewHolder.mTime.setText(getTimeAgo.getTimeAgo(lastTime, context));

            viewHolder.messageText.setText("");
//            viewHolder.messageText.setVisibility(View.INVISIBLE);
            try {
//                viewHolder.messageImage.setBackgroundResource(arr[Integer.parseInt(c.getMessage())]);

                Picasso.with(context).load(arr[Integer.parseInt(c.getMessage())]).into(viewHolder.messageImage);

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
