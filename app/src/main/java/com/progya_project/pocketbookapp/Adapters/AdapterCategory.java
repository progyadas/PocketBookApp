package com.progya_project.pocketbookapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.progya_project.pocketbookapp.Filters.FilterCategory;
import com.progya_project.pocketbookapp.ModelClasses.ModelCategory;
import com.progya_project.pocketbookapp.R;

import java.util.ArrayList;

public class AdapterCategory extends  RecyclerView.Adapter<AdapterCategory.HolderCategory> implements Filterable {

    private Context context;
    public ArrayList<ModelCategory> categoryArrayList, filterlist;

    //instance of created filter class
    private FilterCategory filter;

    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterlist=categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View listItem=layoutInflater.inflate(R.layout.row_category,parent,false);
        HolderCategory viewHolder=new HolderCategory(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        //get data
        ModelCategory model=categoryArrayList.get(position);
        String id=model.getId();
        String category=model.getCategory();
        String uid=model.getUid();
        long timestamp=model.getTimestamp();

        //set data
        holder.mcategory.setText(category);

        holder.mdeletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"pressed",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this category?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context,"Deleting...",Toast.LENGTH_SHORT).show();
                                deleteCategory(model,holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });

    }

    private void deleteCategory(ModelCategory model, HolderCategory holder) {
        String id=model.getId();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Successfully deleted",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter= new FilterCategory(filterlist,this);
        }
        return filter;
    }

    class HolderCategory extends RecyclerView.ViewHolder{

        TextView mcategory;
        ImageButton mdeletebtn;

        public HolderCategory(@NonNull View itemView){
            super((itemView));

            mcategory=itemView.findViewById(R.id.categoryTv);
            mdeletebtn=itemView.findViewById(R.id.deleteBtn);

        }
    }

}
