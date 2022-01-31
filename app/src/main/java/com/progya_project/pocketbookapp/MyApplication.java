package com.progya_project.pocketbookapp;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //to create a static method to convert timestamp into dd/mm/yyyy format so that we can use it everywhere in the project
    public static final String formatTimestamp(long timestamp){
        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        String date= DateFormat.format("dd/mm/yyyy",cal).toString();
        return date;
    }
}
