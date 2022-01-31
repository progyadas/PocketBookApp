package com.progya_project.pocketbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button mregisterbtn;
    private ImageButton mbackbtn;
    private EditText mname,memail,mpassword,mconpassword;
    private String name,email,password,conpassword;

    //Firebase
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        mregisterbtn=findViewById(R.id.registerBtn);
        mbackbtn=findViewById(R.id.backBtn);
        mname=findViewById(R.id.nameEt);
        memail=findViewById(R.id.emailEt);
        mpassword=findViewById(R.id.passwordEt);
        mconpassword=findViewById(R.id.cnfrmPasswordEt);

        //handling register button click
        mregisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        //handling back button click
        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void validateData() {
        name=mname.getText().toString().trim();
        email=memail.getText().toString().trim();
        password=mpassword.getText().toString().trim();
        conpassword=mconpassword.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Invalid Email Pattern...!",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(conpassword)){

            Toast.makeText(this, "Confirm password", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(conpassword)){

            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
        }
        else{
            registerUser();
        }
    }

    private void registerUser() {
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //account creation success
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //account creation failure
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info...");
        long timestamp=System.currentTimeMillis();

        String uid=firebaseAuth.getUid();

        //store data in database
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("name",name);
        hashMap.put("email",email);
        hashMap.put("profileImage","");
        hashMap.put("userType","user");  //can be of 2 types: admin or user. admin will be made manually by changing the value in firebase realtime database
        hashMap.put("timestamp",timestamp);

        //set data to database
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //data adding successful
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,"Account created",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this,DashboardUserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


}