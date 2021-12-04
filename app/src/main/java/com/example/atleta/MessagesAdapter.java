package com.example.atleta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessagesList> messagesLists;
    private final Context context;



    public MessagesAdapter(Context context) {
        messagesLists = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {
        MessagesList list2=messagesLists.get(position);
        if(!list2.getImgURL().isEmpty()){
            Glide.with(context).load(list2.getImgURL()).into(holder.profilePic);
        }
        holder.name.setText(list2.getName());
        holder.lastMessage.setText(list2.getLastMessage());

        if(list2.getUnseenMessage()==0){
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        }
        else{
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(list2.getUnseenMessage()+"");
            holder.lastMessage.setTextColor(Color.parseColor("#49BEFF"));

        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,IndividualChatActivity.class);
                intent.putExtra("name", list2.getName());
                intent.putExtra("profile_pic",list2.getImgURL());
                intent.putExtra("chat_key",list2.getChatKey());
                intent.putExtra("uid",list2.getuID());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilePic;
        private TextView name;
        private TextView lastMessage;
        private TextView unseenMessages;
        private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic=itemView.findViewById(R.id.profilePic);
            name=itemView.findViewById(R.id.chatNameTV);
            lastMessage=itemView.findViewById(R.id.lastMessageTV);
            unseenMessages=itemView.findViewById(R.id.unseenMessages);
            rootLayout=itemView.findViewById(R.id.rootLayout);
        }
    }

    public List<MessagesList> getMessagesLists() {
        return messagesLists;
    }

    public void setMessagesLists(List<MessagesList> messagesLists) {
        this.messagesLists = messagesLists;
    }
    public void updateMessagesList(List<MessagesList> messagesLists){
        this.messagesLists=messagesLists;
        notifyDataSetChanged();
    }
}
