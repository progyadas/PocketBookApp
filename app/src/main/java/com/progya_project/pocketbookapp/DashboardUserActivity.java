package com.progya_project.pocketbookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardUserActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    TextView msubtitle;
    ImageButton mlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_user);

        firebaseAuth=FirebaseAuth.getInstance();

        msubtitle=findViewById(R.id.subtitleTv);
        mlogout=findViewById(R.id.logoutBtn);
        checkUser();

        //handling logout button
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(DashboardUserActivity.this,MainActivity.class));
            finish();
        }
        else{
            String email=firebaseUser.getEmail();
            msubtitle.setText(email);
        }
    }
}