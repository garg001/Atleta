package com.example.atleta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {

    private Button backBtn,submitBtn;
    private EditText reasonEt;
    private String uID;
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String reason;
    private DatabaseReference mDatabase;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        backBtn=findViewById(R.id.reportBackBtn);
        submitBtn=findViewById(R.id.submitButton);
        reasonEt=findViewById(R.id.reasonET);

        mDatabase=FirebaseDatabase.getInstance().getReference();


        user=mAuth.getCurrentUser();

        uID=getIntent().getStringExtra("uID");

        backBtn.setOnClickListener(v->{
            finish();
        });

        submitBtn.setOnClickListener(v->{
            reason=reasonEt.getText().toString();
            if(!reason.isEmpty()){
                HashMap<String,Object> reportMap=new HashMap<>();
                reportMap.put("reportedBy",user.getUid());
                reportMap.put("reason",reason);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                reportMap.put("date",dtf.format(now));
                final String currentTimestamp = String.valueOf(System.currentTimeMillis());
                mDatabase.child("reports").child(uID).child(currentTimestamp).setValue(reportMap);
                Toast.makeText(ReportActivity.this, "User reported.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
                Toast.makeText(ReportActivity.this, "Reason can't be empty.", Toast.LENGTH_SHORT).show();

        });
    }
}