package com.example.atleta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText userNameET,emailET,passET;
    private static final String TAG = "SignUpActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = database.getReference();
        userNameET=findViewById(R.id.editTextPersonName2);
        emailET=findViewById(R.id.editTextEmailAddress2);
        passET=findViewById(R.id.editTextPassword2);
        Button signupBT= findViewById(R.id.signupButton2);

        signupBT.setOnClickListener(view ->{
            createUser();
        });
    }

    private void createUser() {
        String email = emailET.getText().toString();
        String password= passET.getText().toString();
        String userName=userNameET.getText().toString();

        if(TextUtils.isEmpty(email)){
            emailET.setError("Email cannot be empty");
            emailET.requestFocus();
        }else if(TextUtils.isEmpty(password)){
            passET.setError("Password cannot be empty");
            passET.requestFocus();
        }else if(TextUtils.isEmpty(userName)){
            userNameET.setError("User Name cannot be empty");
            userNameET.requestFocus();
        }else{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //AddUser to database
                                try{
                                    writeNewUser(user.getUid(), userName, user.getEmail());
                                }catch (Exception ex){
                                    Log.d("writedatafailed",ex.toString());
                                }
                                //SwipeActivity(user);
                                Intent intent =new Intent(SignUpActivity.this,SwipeActivity.class);
                                intent.putExtra("user",user);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed. "+task.getException(),
                                        Toast.LENGTH_SHORT).show();
                               // updateUI(null);
                            }
                        }
                    });
        }

    }

    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }


}