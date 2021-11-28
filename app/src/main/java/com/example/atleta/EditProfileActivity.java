package com.example.atleta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private static final int IMAGE_REQUEST = 2;
    private static final int PERM_REQUEST = 101;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;
    private DatabaseReference userRef;
    private EditText nameET,locationET,ageET,favET,expET,freqET;
    private ImageView editDpIV;
    private Button saveBtn,removeDp,backBtn;
    private FirebaseAuth mAuth;
    private Bundle extras;
    private FirebaseUser user;
    private User user1;
    private Uri imageUri;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        
        mDatabase=database.getReference();
        nameET=findViewById(R.id.editTextName);
        ageET=findViewById(R.id.editTextAge);
        favET=findViewById(R.id.editTextFavorite);
        expET=findViewById(R.id.editTextExperience);
        freqET=findViewById(R.id.editTextFrequency);
        locationET=findViewById(R.id.editTextLocation);
        editDpIV=findViewById(R.id.editDpIV);
        saveBtn=findViewById(R.id.saveButton);
        removeDp=findViewById(R.id.removeDpBtn);
        backBtn=findViewById(R.id.backButton2);

        backBtn.setOnClickListener(v->{
            startActivity(new Intent(EditProfileActivity.this,UserProfileActivity.class));
        });

        retrieveData();
        
        saveBtn.setOnClickListener(v->{
            updateChanges();
        });
        editDpIV.setOnClickListener(v->{
            requestPerm();
        });
        removeDp.setOnClickListener(v->{
            removeDp();
        });
    }

    private void removeDp() {
        Toast.makeText(EditProfileActivity.this, "Profile Pic Removed. Click Save to Update.", Toast.LENGTH_SHORT).show();
        user1.setDpURL("");
    }

    private void retrieveData() {
        userRef = mDatabase.child("users").child(user.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    user1 = snapshot.getValue(User.class);
                    url=user1.getDpURL();
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

    private void requestPerm() {
        if(ContextCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This permission is required for uploading image")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(EditProfileActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PERM_REQUEST);

                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }else{
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},PERM_REQUEST);
            }
        }else{
            openImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                openImage();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode==RESULT_OK){
            imageUri=data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressBar progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        if(imageUri!=null){
            StorageReference mStorageRef= FirebaseStorage.getInstance().getReference().child("profileDPs").child(user.getUid()+"."+ getFileExtension(imageUri));
            mStorageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                    mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                             url=uri.toString();
                             progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditProfileActivity.this, "Profile Pic Uploaded. Click Save to Update.", Toast.LENGTH_SHORT).show();
                            user1.setDpURL(url);
                        }
                    });}
                    else{
                        Log.w(TAG, "uploadImg:failure", task.getException());
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(EditProfileActivity.this, "Upload failed. "+task.getException(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    private void updateChanges() {
        String name=nameET.getText().toString();
        String age=ageET.getText().toString();
        String fav=favET.getText().toString();
        String exp=expET.getText().toString();
        String freq=freqET.getText().toString();
        String location=locationET.getText().toString();
        String userId=user.getUid();
        

        if(!name.equals(""))
            user1.setUserName(name);
        if(!age.equals(""))
            user1.setAge(age);
        if(!fav.equals(""))
            user1.setFavorite(fav);
        if(!exp.equals(""))
            user1.setExperience(exp);
        if(!freq.equals(""))
            user1.setFrequency(freq);
        if(!location.equals(""))
            user1.setLocation(location);
        if(user1.getDpURL().equals("") && !(url.equals(""))){
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "onSuccess: deleted file");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: did not delete file");
                }
            });
        }

            mDatabase.child("users").child(userId).setValue(user1);
        Toast.makeText(EditProfileActivity.this,"Profile Updated.",Toast.LENGTH_SHORT).show();
    }
}