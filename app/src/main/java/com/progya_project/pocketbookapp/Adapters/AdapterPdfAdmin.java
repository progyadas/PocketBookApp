package com.progya_project.pocketbookapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.progya_project.pocketbookapp.ModelClasses.ModelPdf;
import com.progya_project.pocketbookapp.MyApplication;
import com.progya_project.pocketbookapp.R;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>{

    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View listitem=layoutInflater.inflate(R.layout.row_admin_pdf,parent,false);
        HolderPdfAdmin viewHolder=new HolderPdfAdmin(listitem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        //get data
        ModelPdf model=pdfArrayList.get(position);
        String title=model.getTitle();
        String description=model.getDescription();
        long timestamp=model.getTimestamp();

        //convert timestamp to dd/mm/yyyy format
        String formatteddate= MyApplication.formatTimestamp(timestamp);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formatteddate);

        loadCategory(model,holder);
        loadPdfFromUrl(model,holder);
        loadPdfSize(model,holder);
    }

    private void loadPdfSize(ModelPdf model, HolderPdfAdmin holder) {
        String pdfUrl=model.getUrl();
        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes=storageMetadata.getSizeBytes();
                        double kb=bytes/1024;
                        double mb=kb/1024;
                        if(mb>=1){
                            holder.sizeTv.setText(String.format("%.2f",mb)+"MB");
                        }
                        else if(kb>=1){
                            holder.sizeTv.setText(String.format("%.2f",kb)+"MB");
                        }
                        else{
                            holder.sizeTv.setText(String.format("%.2f",bytes)+"bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadPdfFromUrl(ModelPdf model, HolderPdfAdmin holder) {

        
        
    }

    private void loadCategory(ModelPdf model, HolderPdfAdmin holder) {
        
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder{

        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,categoryTv,sizeTv,dateTv;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {

            super(itemView);
            pdfView=itemView.findViewById(R.id.pdfView);
            progressBar=itemView.findViewById(R.id.progressBar);
            titleTv=itemView.findViewById(R.id.titleTv);
            descriptionTv=itemView.findViewById(R.id.descriptionTv);
            categoryTv=itemView.findViewById(R.id.categoryTv);
            sizeTv=itemView.findViewById(R.id.sizeTv);
            dateTv=itemView.findViewById(R.id.dateTv);
            moreBtn=itemView.findViewById(R.id.moreBtn);
        }
    }

}
