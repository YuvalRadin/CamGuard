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

    public void DoNotRemember()
    {
        editor.remove("username");
        editor.remove("password");
        editor.remove("Remember");
        editor.apply();
    }

    public boolean DoesRemember()
    {
        return sharedPreferences.getBoolean("Remember", false);
    }

    public String[] getCredentials() { return new String[]{sharedPreferences.getString("username", ""), sharedPreferences.getString("email", "")}; }



}
