package com.progya_project.pocketbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.progya_project.pocketbookapp.ModelClasses.ModelCategory;
import com.progya_project.pocketbookapp.Adapters.AdapterCategory;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    TextView msubtitle,categoryrv;
    EditText search;
    ImageButton mlogout;
    Button mcategorybtn;
    FloatingActionButton mpdfbtn;
    RecyclerView recyclerView;

    private ArrayList<ModelCategory> categoryArrayList;

    private AdapterCategory adapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        firebaseAuth=FirebaseAuth.getInstance();

        msubtitle=findViewById(R.id.subtitleTv);
        mlogout=findViewById(R.id.logoutBtn);
        mcategorybtn=findViewById(R.id.addCategoryBtn);
        search=findViewById(R.id.searchEt);
        mpdfbtn=findViewById(R.id.addpdf);
        //categoryrv=findViewById(R.id.categories);
        recyclerView=(RecyclerView)findViewById(R.id.categories);
        checkUser();
        loadCategories();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //called when user type each letter
                try{
                    adapterCategory.getFilter().filter(s);
                }
                catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handling logout button
        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        //handle add category button
        mcategorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this,CategoryAddActivity.class));
            }
        });

        //start pdf add screen
        mpdfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this,PdfAddActivity.class));
            }
        });

    }

    private void loadCategories() {

        categoryArrayList=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelCategory model=ds.getValue(ModelCategory.class);

                    categoryArrayList.add(model);
                }
                //setup adapter
                adapterCategory=new AdapterCategory(DashboardAdminActivity.this,categoryArrayList);
                //set adapter to recyclerview
                recyclerView.setAdapter(adapterCategory);
               // recyclerView.setLayoutManager(new LinearLayoutManager(DashboardAdminActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            startActivity(new Intent(DashboardAdminActivity.this,MainActivity.class));
            finish();
        }
        else{
            String email=firebaseUser.getEmail();
            msubtitle.setText(email);
        }
    }
}