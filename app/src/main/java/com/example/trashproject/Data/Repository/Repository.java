package com.example.trashproject.Data.Repository;

import android.content.Context;

import com.example.trashproject.Data.DB.MyDatabaseHelper;

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
