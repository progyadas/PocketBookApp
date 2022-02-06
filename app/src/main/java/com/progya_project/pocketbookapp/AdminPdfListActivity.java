package com.progya_project.pocketbookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.progya_project.pocketbookapp.Adapters.AdapterPdfAdmin;
import com.progya_project.pocketbookapp.ModelClasses.ModelPdf;

import java.util.ArrayList;

public class AdminPdfListActivity extends AppCompatActivity {

    private ArrayList<ModelPdf> pdfArrayList;
    private AdapterPdfAdmin adapterPdfAdmin;
    private  String categoryId,categoryTitle;
    private static final String TAG="PDF_LIST_TAG";
    RecyclerView recyclerView;
    ImageButton backbtn;
    EditText search;
    TextView subtitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pdf_list);
        Intent intent=getIntent();

        backbtn=findViewById(R.id.backBtn);
        categoryId=intent.getStringExtra("categoryId");
        categoryTitle=intent.getStringExtra("categoryTitle");
        recyclerView=(RecyclerView) findViewById(R.id.bookRv);
        search=findViewById(R.id.searchEt);
        subtitle=findViewById(R.id.subtitleTv);

        subtitle.setText(categoryTitle);

        loadPdfList();

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterPdfAdmin.getFilter().filter(s);
                }
                catch (Exception e){
                    Log.d(TAG,"onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadPdfList() {
        pdfArrayList=new ArrayList<>(); //init list before adding data
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelPdf model=ds.getValue(ModelPdf.class);
                            pdfArrayList.add(model);
                            Log.d(TAG,"onDataChange: "+model.getId()+" "+model.getTitle());
                        }
                        adapterPdfAdmin=new AdapterPdfAdmin(AdminPdfListActivity.this,pdfArrayList);
                        recyclerView.setAdapter(adapterPdfAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}