package com.progya_project.pocketbookapp.Filters;

import android.widget.Filter;

import com.progya_project.pocketbookapp.ModelClasses.ModelCategory;
import com.progya_project.pocketbookapp.Adapters.AdapterCategory;

import java.util.ArrayList;

public class FilterCategory extends Filter {

    //arraylist in which we want to search
    ArrayList<ModelCategory> filterlist;
    //adapter in which filter need to be implemented
    AdapterCategory adapterCategory;

    //constructor
    public FilterCategory(ArrayList<ModelCategory> filterlist, AdapterCategory adapterCategory) {
        this.filterlist = filterlist;
        this.adapterCategory = adapterCategory;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();
        //value should not be null and empty
        if(constraint!=null && constraint.length()>0){
            //to avoid sensitivity change to uppercase or lowercase
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels=new ArrayList<>();
            for(int i=0;i<filterlist.size();i++){
                if(filterlist.get(i).getCategory().toUpperCase().contains(constraint)) {
                    //add to filtered list
                    filteredModels.add(filterlist.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else{
            results.count=filterlist.size();
            results.values=filterlist;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //apply filter changes
        adapterCategory.categoryArrayList=(ArrayList<ModelCategory>)results.values;

        //notify changes
        adapterCategory.notifyDataSetChanged();
    }
}
