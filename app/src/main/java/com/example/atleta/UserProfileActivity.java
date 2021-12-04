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

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfileActivity";
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userRef;
    private TextView userNameTV,emailTV,locationTV,ageTV,favTV,expTV,freqTV;
    private ImageView profileIV;
    private User user1;
    private Button backBtn;
    private String activity;

    @Override
    public void onBackPressed() {
        if(activity==null)
            startActivity(new Intent(UserProfileActivity.this,SwipeActivity.class));
        else
            startActivity(new Intent(UserProfileActivity.this,ChatActivity.class));    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userNameTV=findViewById(R.id.userNameTV1);
        emailTV=findViewById(R.id.emailTV1);
        locationTV=findViewById(R.id.locationEditTV1);
        ageTV=findViewById(R.id.ageEditTV1);
        favTV=findViewById(R.id.workoutsEditTV1);
        expTV=findViewById(R.id.experienceEditTV1);
        freqTV=findViewById(R.id.workFreqEditTV1);
        profileIV=findViewById(R.id.profileIV1);
        backBtn=findViewById(R.id.backButton1);
         activity=getIntent().getStringExtra("activity");

        backBtn.setOnClickListener(v->{

            if(activity==null)
            startActivity(new Intent(UserProfileActivity.this,SwipeActivity.class));
            else
                startActivity(new Intent(UserProfileActivity.this,ChatActivity.class));
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        emailTV.setText(user.getEmail());
        userNameTV.setText(user.getDisplayName());

        Button editButton=findViewById(R.id.editProfileBtn);
        Button signOutButton=findViewById(R.id.signOutBtn);

        signOutButton.setOnClickListener(view->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(UserProfileActivity.this,MainActivity.class));
        });

        editButton.setOnClickListener(v ->{
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        retrieveData();
    }

    private void retrieveData() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userRef = mDatabase.child("users").child(user.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    user1 = snapshot.getValue(User.class);

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
                        Glide.with(UserProfileActivity.this).load(user1.getDpURL()).into(profileIV);
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