package com.example.atleta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {
    private Bundle extras;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        extras = getIntent().getExtras();
        if (extras != null) {
            user = (FirebaseUser) extras.get("user");
        }

        Button editButton=findViewById(R.id.editProfileBtn);
        Button signOutButton=findViewById(R.id.signOutBtn);

        signOutButton.setOnClickListener(view->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(UserProfileActivity.this,MainActivity.class));
        });

        editButton.setOnClickListener(v ->{
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("user",user);
            startActivity(intent);
        });
    }
}