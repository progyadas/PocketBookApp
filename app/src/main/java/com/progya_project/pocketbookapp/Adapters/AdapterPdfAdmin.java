package com.progya_project.pocketbookapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.progya_project.pocketbookapp.Filters.FilterPdfAdmin;
import com.progya_project.pocketbookapp.ModelClasses.ModelPdf;
import com.progya_project.pocketbookapp.MyApplication;
import com.progya_project.pocketbookapp.R;

import java.util.ArrayList;

import static com.progya_project.pocketbookapp.Constants.MAX_BYTES_PDF;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    private FilterPdfAdmin filter;
    private static final String  TAG="PDF_ADAPTER_TAG";

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList= pdfArrayList;
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
                        Log.d(TAG,"onSuccess: "+model.getTitle()+" "+bytes);
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
                        //Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.d(TAG,"onFailure: "+e.getMessage());
                    }
                });

    }

    private void loadPdfFromUrl(ModelPdf model, HolderPdfAdmin holder) {
        String pdfurl=model.getUrl();
        StorageReference ref=FirebaseStorage.getInstance().getReferenceFromUrl(pdfurl);
        ref.getBytes(MAX_BYTES_PDF)
            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Log.d(TAG,"onSuccess: "+model.getTitle()+"successfully received the file");
                    holder.pdfView.fromBytes(bytes)
                            .pages(0)//only the first page will be shown
                            .spacing(0)
                            .swipeHorizontal(false)
                            .enableSwipe(false)
                            .onError(new OnErrorListener() {
                                @Override
                                public void onError(Throwable t) {
                                    holder.progressBar.setVisibility(View.INVISIBLE);
                                    Log.d(TAG,"onError: "+t.getMessage());
                                }
                            })
                            .onPageError(new OnPageErrorListener() {
                                @Override
                                public void onPageError(int page, Throwable t) {
                                    holder.progressBar.setVisibility(View.INVISIBLE);
                                    Log.d(TAG,"onPageError: "+t.getMessage());
                                }
                            })
                            .onLoad(new OnLoadCompleteListener() {
                                @Override
                                public void loadComplete(int nbPages) {
                                    holder.progressBar.setVisibility(View.INVISIBLE);
                                    Log.d(TAG,"loadComplete: pdf loaded");
                                }
                            })
                            .load();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    Log.d(TAG,"onFailure: failed getting file from url due to "+e.getMessage());
                }
            });
        
        
    }

    private void loadCategory(ModelPdf model, HolderPdfAdmin holder) {
        //get category using categoryid
        String categoryId=model.getCategoryId();
        //Log.d(TAG,categoryId);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category=""+snapshot.child("category").getValue();
                        //Log.d(TAG,category);
                        holder.categoryTv.setText(category);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterPdfAdmin(filterList,this);
        }
        return filter;
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
