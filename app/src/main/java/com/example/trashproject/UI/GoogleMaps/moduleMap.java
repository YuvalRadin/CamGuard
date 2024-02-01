package com.example.trashproject.UI.GoogleMaps;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.trashproject.Data.Repository.Repository;

public class moduleMap {

    Context context;
    Repository rp;
    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;


    public moduleMap(Context context)
    {
        this.context = context;
        rp = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


}
