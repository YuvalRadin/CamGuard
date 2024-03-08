package com.example.camguard.UI.Admin;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.camguard.Data.Repository.Repository;

public class ModuleAdmin {

    Repository repository;
    Context context;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;
    public ModuleAdmin(Context context)
    {
        this.context = context;
        repository = new Repository(context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean CredentialsExist()
    {
        return sharedPreferences.contains("username");
    }
    public String[] getCredentials() { return new String[]{sharedPreferences.getString("username", ""), sharedPreferences.getString("email", "")}; }

}
