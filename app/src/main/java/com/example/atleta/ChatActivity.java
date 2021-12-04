package com.example.atleta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG ="ChatActivity" ;
    private List<MessagesList> messagesLists=new ArrayList<>();
    private DatabaseReference mDatabase,userRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private User user1;
    private ImageView profileIV;
    private RecyclerView messagesRecyclerView;
    private Button backBtn;
    private MessagesAdapter adapter;
    private int unseenMessages=0;
    private String lastMessage="",chatKey="";
    private boolean dataSet=false;
    private long getMessageKey,getLastSeenMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);

        profileIV=findViewById(R.id.userProfileImageView);
        messagesRecyclerView=findViewById(R.id.messagesRecyclerView);
        backBtn=findViewById(R.id.chatBackButton);

        backBtn.setOnClickListener(v->{
            startActivity(new Intent(ChatActivity.this,SwipeActivity.class));
        });

        profileIV.setOnClickListener(v->{
            Intent intent=new Intent(ChatActivity.this,UserProfileActivity.class);
            intent.putExtra("activity","Chat");
            startActivity(intent);
        });

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        mDatabase = database.getReference();
        userRef = mDatabase.child("users");
        retrieveData();

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this ));



        adapter=new MessagesAdapter(ChatActivity.this);
        messagesRecyclerView.setAdapter(adapter);

        addList();

    }

    private void addList() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesLists.clear();
                unseenMessages=0;
                lastMessage="";
                chatKey="";
                for(DataSnapshot data : snapshot.getChildren()){
                    User user2=data.getValue(User.class);

                    if(!(user2.getuID().equals(user.getUid()))){


                        mDatabase.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                int getChatCounts=(int)snapshot2.getChildrenCount();
                                if(getChatCounts>0){
                                    for(DataSnapshot snapshot1: snapshot2.getChildren()){
                                         String getKey=snapshot1.getKey();
                                        chatKey=getKey;

                                        if(snapshot1.hasChild("user_1") && snapshot1.hasChild("user_2")){
                                             String getUserOne=snapshot1.child("user_1").getValue(String.class);
                                             String getUserTwo=snapshot1.child("user_2").getValue(String.class);

                                            if((getUserOne.equals(user2.getuID()) && getUserTwo.equals(user.getUid())) || (getUserOne.equals(user.getUid()) && getUserTwo.equals(user2.getuID()))){
                                                lastMessage="";
                                                unseenMessages=0;
                                                try {
                                                    getLastSeenMessage = snapshot2.child(chatKey).child("lastSeen").child(user.getUid()).getValue(Long.class);
                                                }
                                                catch(Exception e){
                                                    Log.d(TAG,e.toString());
                                                    getLastSeenMessage=0;
                                                }

                                                for(DataSnapshot chatDataSnapshot:snapshot1.child("messages").getChildren()){



                                                    try{
                                                        getMessageKey= Long.parseLong(chatDataSnapshot.getKey());
                                                    }
                                                    catch (Exception e){
                                                        Log.d(TAG,e.toString());
                                                        getMessageKey=0;
                                                    }

                                                    if(getMessageKey>getLastSeenMessage){
                                                        unseenMessages++;
                                                    }

                                                    lastMessage=chatDataSnapshot.child("msg").getValue(String.class);



                                                }
                                                MessagesList messagesList=new MessagesList(user2.getUserName(),user2.getDpURL(),lastMessage,chatKey,unseenMessages,user2.getuID());
                                                messagesLists.add(messagesList);
                                                adapter.setMessagesLists(messagesLists);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }

                                    }
                                }


                                }



                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ChatActivity.this , "Error "+error, Toast.LENGTH_SHORT).show();
                                Log.d(TAG,"addList error: "+error);
                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this , "Error "+error, Toast.LENGTH_SHORT).show();
                Log.d(TAG,"ChatActivity error: "+error);
            }
        });

    }


    private void retrieveData() {
        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    user1 = snapshot.getValue(User.class);

                    if(!user1.getDpURL().equals(""))
                        Glide.with(ChatActivity.this).load(user1.getDpURL()).into(profileIV);
                }
                catch (Exception ex){
                    Log.d(TAG, ex.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
    }
}