package com.example.amrgamal.testwear.Fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amrgamal.testwear.OnListFragmentInteractionListener;
import com.example.amrgamal.testwear.R;
import com.example.amrgamal.testwear.adapter.Image_Adapter;
import com.example.amrgamal.testwear.adapter.Message_Adapter;
import com.example.amrgamal.testwear.model.Image;
import com.example.amrgamal.testwear.model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by amrga on 05/02/2018.
 */

public class ChatFragment extends Fragment implements OnListFragmentInteractionListener {


    private String mChatUser;
//        private Toolbar mChatToolbar;
//   OnListFragmentInteractionListener mListener;

    private DatabaseReference mRootRef;
    private DatabaseReference mParent;

//    private TextView mTitleView;
//    private TextView mMessagesListastSeenView;
    //        private CircleImageView mProfileImage;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;

    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;

    private RecyclerView mMessagesList;
    private RecyclerView messages_image;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private  List<Image> imageList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private Message_Adapter mAdapter;
    private Image_Adapter iAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;

    OnListFragmentInteractionListener mListener;

    //New Solution
    private int itemPos = 0;

    private String mLastKey = "";
    private String mPrevKey = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.chat_fragment, container, false);


        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mChatAddBtn = (ImageButton) view.findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) view.findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) view.findViewById(R.id.chat_message_view);

        initImageList();

        mAdapter = new Message_Adapter(messagesList,getContext(),mListener);
        iAdapter = new Image_Adapter(imageList,getContext(), this);

        mMessagesList = (RecyclerView) view.findViewById(R.id.messages_list);
        messages_image = (RecyclerView) view.findViewById(R.id.messages_image);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(getContext());

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        messages_image.setHasFixedSize(true);
        messages_image.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));

        mMessagesList.setAdapter(mAdapter);
        mMessagesList.setNestedScrollingEnabled(false);
        messages_image.setAdapter(iAdapter);

        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mParent = FirebaseDatabase.getInstance().getReference().child("Parent").child(mCurrentUserId);
        mParent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChatUser = String.valueOf(dataSnapshot.getValue());

                mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);

                loadMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



//        mTitleView.setText(userName);

//            mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    String online = dataSnapshot.child("online").getValue().toString();
////                    String image = dataSnapshot.child("image").getValue().toString();
//
//                    if(online.equals("true")) {
//
//                        mLastSeenView.setText("Online");
//
//                    } else {
//
//                        GetTimeAgo getTimeAgo = new GetTimeAgo();
//
//                        long lastTime = Long.parseLong(online);
//
//                        String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());
//
//                        mLastSeenView.setText(lastSeenTime);
//
//                    }
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });


        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    if (!dataSnapshot.hasChild(mChatUser)) {

                        Map chatAddMap = new HashMap();
                        chatAddMap.put("seen", false);
                        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                        Map chatUserMap = new HashMap();
                        chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                        chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);

                        mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null) {

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }

                            }
                        });

                    }

                }catch (Exception e){}


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });



//        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent galleryIntent = new Intent();
//                galleryIntent.setType("image/*");
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
//
//            }
//        });



        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();


            }
        });


        return view;
    }

    private void initImageList() {

        Image image;
        int arr[] = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,
                R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9};
        for (int i = 0 ; i < 9 ; i ++){
            image = new Image();
            image.setDirect(arr[i]);
            image.setLink(i);

            imageList.add(image);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
//
//            Uri imageUri = data.getData();
//
//            final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
//            final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;
//
//            DatabaseReference user_message_push = mRootRef.child("messages")
//                    .child(mCurrentUserId).child(mChatUser).push();
//
//            final String push_id = user_message_push.getKey();
//
//
//            StorageReference filepath = mImageStorage.child("message_images").child( push_id + ".jpg");
//
//            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                    if(task.isSuccessful()){
//
//                        String download_url = task.getResult().getDownloadUrl().toString();
//
//
//                        Map messageMap = new HashMap();
//                        messageMap.put("message", download_url);
//                        messageMap.put("seen", false);
//                        messageMap.put("type", "image");
//                        messageMap.put("time", ServerValue.TIMESTAMP);
//                        messageMap.put("from", mCurrentUserId);
//
//                        Map messageUserMap = new HashMap();
//                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
//                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
//
//                        mChatMessageView.setText("");
//
//                        mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//
//                                if(databaseError != null){
//
//                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());
//
//                                }
//
//                            }
//                        });
//
//
//                    }
//
//                }
//            });
//
//        }
//
//    }

    private void loadMoreMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                Messages message = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messagesList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }


                Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);

                mLinearLayout.scrollToPositionWithOffset(10, 0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessages() {

        DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size() - 1);

                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {


        String message = mChatMessageView.getText().toString();

        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });

        }
    }
    private void sendImage(int p) {

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages")
                    .child(mCurrentUserId).child(mChatUser).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", p+"");
            messageMap.put("seen", false);
            messageMap.put("type", "image");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
            mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
            mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if(databaseError != null){

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }

                }
            });

        }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }

    }

    @Override
    public boolean onListClick(View v, int i) {
        try {
            sendImage(i);
        }catch (Exception e){
            Toast.makeText(getContext(),"لا يوجد اتصال بالانترنت",Toast.LENGTH_LONG).show();
        }
        return false;
    }


//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
//        }
//    }

}
