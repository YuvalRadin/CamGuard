package com.example.trashproject.repository;

import android.content.Context;

import com.example.trashproject.DB.MyDatabaseHelper;
import com.google.android.gms.maps.GoogleMap;

public class Repository {
    Context context;

    MyDatabaseHelper myDatabaseHelper;

    public Repository(Context context)
    {
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(this.context);
    }

    public MyDatabaseHelper getMyDatabaseHelper()
    {
        return myDatabaseHelper;
    }
}
