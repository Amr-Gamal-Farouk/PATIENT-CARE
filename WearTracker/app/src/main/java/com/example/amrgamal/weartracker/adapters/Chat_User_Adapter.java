package com.example.amrgamal.weartracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.amrgamal.weartracker.R;
import com.example.amrgamal.weartracker.models.Chat_Model;
import java.util.List;

/**
 * Created by amrga on 02/02/2018.
 */

public class Chat_User_Adapter extends RecyclerView.Adapter<Chat_User_Adapter.MyViewHolder> {

    private List<Chat_Model> chatList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Name, lastMessage, lastMessageTime;

        public MyViewHolder(View view) {
            super(view);
            Name = (TextView) view.findViewById(R.id.mName);
            lastMessage = (TextView) view.findViewById(R.id.mMessage);
//            lastMessageTime = (TextView) view.findViewById(R.id.mTime);
        }
    }

    public Chat_User_Adapter(List<Chat_Model> chatList) {
        this.chatList = chatList;
    }

    @Override
    public Chat_User_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        return new Chat_User_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Chat_User_Adapter.MyViewHolder holder, int position) {

        Chat_Model userChat = chatList.get(position);
        holder.Name.setText(userChat.getName());
        holder.lastMessage.setText(userChat.getLastMessage());
        holder.lastMessageTime.setText(userChat.getLastMessagetime());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


}

