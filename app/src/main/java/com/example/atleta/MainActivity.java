package com.example.atleta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Button login= findViewById(R.id.loginButton);
        Button singup= findViewById(R.id.signUpButton);
        Button testButton=findViewById(R.id.testbutton);

        testButton.setOnClickListener(view ->{
            startActivity(new Intent(MainActivity.this,SwipeActivity.class));
        });

        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        Intent intent1 = new Intent(MainActivity.this, SignUpActivity.class);
        singup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent1);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent2=new Intent(MainActivity.this,SwipeActivity.class);
            startActivity(intent2);
        }
    }
}