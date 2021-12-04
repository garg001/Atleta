package com.example.atleta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BasicUserProfileActivity extends AppCompatActivity {

    private static final String TAG = "BasicUserProfileActivity";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userRef;
    private TextView userNameTV,emailTV,locationTV,ageTV,favTV,expTV,freqTV;
    private ImageView profileIV;
    private User user1;
    private ItemModel user2;
    private Button backBtn;
    private String uId;

    @Override
    public void onBackPressed() {
        if(uId!=null){
                finish();
        }
        else{
               startActivity(new Intent(BasicUserProfileActivity.this,SwipeActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_user_profile);

        Intent i = getIntent();
        user2 = (ItemModel) i.getSerializableExtra("basicUser");

        uId=getIntent().getStringExtra("basicUID");


        userNameTV=findViewById(R.id.userNameTV);
        emailTV=findViewById(R.id.emailTV);
        locationTV=findViewById(R.id.locationEditTV);
        ageTV=findViewById(R.id.ageEditTV);
        favTV=findViewById(R.id.workoutsEditTV);
        expTV=findViewById(R.id.experienceEditTV);
        freqTV=findViewById(R.id.workFreqEditTV);
        profileIV=findViewById(R.id.profileIV);
        backBtn=findViewById(R.id.backButton);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(uId!=null){
            backBtn.setOnClickListener(v->{
                finish();
            });
            userRef = mDatabase.child("users").child(uId);

        }
        else{
            backBtn.setOnClickListener(v->{
                startActivity(new Intent(BasicUserProfileActivity.this,SwipeActivity.class));
            });
            userRef = mDatabase.child("users").child(user2.getuID());
        }

        retrieveData();


    }

    private void retrieveData() {


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    user1 = snapshot.getValue(User.class);

                    emailTV.setText(user1.getUserEmail());

                    if(!(user1.getUserName().equals("")))
                        userNameTV.setText(user1.getUserName());
                    else
                        userNameTV.setText("N/A");
                    if(!user1.getLocation().equals(""))
                        locationTV.setText(user1.getLocation());
                    else
                        locationTV.setText("N/A");
                    if(!user1.getAge().equals(""))
                        ageTV.setText(user1.getAge());
                    else
                        ageTV.setText("N/A");
                    if(!user1.getFrequency().equals(""))
                        freqTV.setText(user1.getFrequency());
                    else
                        freqTV.setText("N/A");
                    if(!user1.getFavorite().equals(""))
                        favTV.setText(user1.getFavorite());
                    else
                        favTV.setText("N/A");
                    if(!user1.getExperience().equals(""))
                        expTV.setText(user1.getExperience());
                    else
                        expTV.setText("N/A");
                    if(!user1.getDpURL().equals(""))
                        Glide.with(BasicUserProfileActivity.this).load(user1.getDpURL()).into(profileIV);
                    else
                        profileIV.setImageDrawable(getResources().getDrawable(R.drawable.profile));
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