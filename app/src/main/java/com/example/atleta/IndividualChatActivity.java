package com.example.atleta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IndividualChatActivity extends AppCompatActivity {

    private static final String TAG = "IndividualChatActivity";
    private ImageView backBtn, sendBtn, profilePic;
    private TextView nameTV;
    private EditText messageEditText;
    private DatabaseReference mDatabase, userRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private String chatKey;
    private RecyclerView chattingRecyclerView;
    private List<ChatList> chatLists = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime=true;
    private long lastSeenMessage=0;
    private boolean active;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_chat_activity);

        mDatabase = database.getReference();
        userRef = mDatabase.child("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        backBtn = findViewById(R.id.indChatBackBtn);
        nameTV = findViewById(R.id.indChatName);
        messageEditText = findViewById(R.id.messageEditText);
        sendBtn = findViewById(R.id.sendBtn);
        profilePic = findViewById(R.id.indChatProfilePic);

        final String getName = getIntent().getStringExtra("name");
        final String getProfilePic = getIntent().getStringExtra("profile_pic");
        final String getUID = getIntent().getStringExtra("uid");
        chatKey = getIntent().getStringExtra("chat_key");

        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        nameTV.setText(getName);
        Glide.with(IndividualChatActivity.this).load(getProfilePic).into(profilePic);

        profilePic.setOnClickListener(v->{
            Intent intent1 = new Intent(IndividualChatActivity.this, BasicUserProfileActivity.class);
            intent1.putExtra("basicUID",getUID);
            startActivity(intent1);
        });

        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(IndividualChatActivity.this));
        chatAdapter=new ChatAdapter(chatLists,IndividualChatActivity.this);
        chattingRecyclerView.setAdapter(chatAdapter);


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.child("chat").child(chatKey).hasChild("messages")){
                        chatLists.clear();
                        for(DataSnapshot messagesSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()){
                            if(messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("uID")){
                                final String messageTimestamps=messagesSnapshot.getKey();
                                final String getUId=messagesSnapshot.child("uID").getValue(String.class);
                                final String getMsg=messagesSnapshot.child("msg").getValue(String.class);

                                Timestamp timestamp=new Timestamp(Long.parseLong(messageTimestamps));
                                Date date=new Date(timestamp.getTime());
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                SimpleDateFormat simpleTimeFormat=new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                                ChatList chatList=new ChatList(getUId,getName,getMsg,simpleDateFormat.format(date),simpleTimeFormat.format(date));
                                chatLists.add(chatList);
                                long getLastSeen=0;

                                try {
                                    getLastSeen= Long.parseLong( snapshot.child("chat").child(chatKey).child("lastSeen").child(user.getUid()).getValue(String.class));                                }
                                catch (Exception e){
                                    Log.d(TAG,e.toString());
                                    getLastSeen=0;
                                }

                                if(loadingFirstTime || Long.parseLong(messageTimestamps)> getLastSeen){

                                    loadingFirstTime=false;
                                    lastSeenMessage= Long.parseLong(messageTimestamps);

                                    mDatabase.child("chat").child(chatKey).child("lastSeen").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(active)
                                            mDatabase.child("chat").child(chatKey).child("lastSeen").child(user.getUid()).setValue(lastSeenMessage);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(IndividualChatActivity.this, "Error " + error, Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "error: " + error);
                                        }
                                    });
                                    chatAdapter.updateChatList(chatLists);
                                    chattingRecyclerView.scrollToPosition(chatLists.size() -1);
                                }

                            }

                        }
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(IndividualChatActivity.this, "Error " + error, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error: " + error);
            }
        });


        sendBtn.setOnClickListener(v -> {
            final String getTxtMessage = messageEditText.getText().toString();
            if (!getTxtMessage.isEmpty()) {
                final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                mDatabase.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("msg").setValue(getTxtMessage);
                mDatabase.child("chat").child(chatKey).child("messages").child(currentTimestamp).child("uID").setValue(user.getUid());

                messageEditText.setText("");
            }
        });

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(IndividualChatActivity.this,ChatActivity.class));
        });

    }
}