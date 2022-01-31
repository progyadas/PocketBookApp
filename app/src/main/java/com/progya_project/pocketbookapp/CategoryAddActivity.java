package com.progya_project.pocketbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryAddActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    Button msubmitbtn;
    EditText mcategory;
    ImageButton mbackbtn;

    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        msubmitbtn=findViewById(R.id.submitBtn);
        mcategory=findViewById(R.id.categoryEt);
        mbackbtn=findViewById(R.id.backBtn);

        mbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        msubmitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private void validateData() {
        category=mcategory.getText().toString().trim();

        if(TextUtils.isEmpty(category)){
            Toast.makeText(this,"Please enter category",Toast.LENGTH_SHORT).show();
        }
        else{
            addCategory();
        }
    }

    private void addCategory() {
        progressDialog.setMessage("Adding Category...");
        progressDialog.show();

        long timestamp=System.currentTimeMillis();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("category",""+category);
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("timestamp",timestamp);

        //set data to database
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //data adding successful
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this,"Category added successfully...",Toast.LENGTH_SHORT).show();                       finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }
}