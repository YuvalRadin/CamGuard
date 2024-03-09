package com.example.camguard.UI.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

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
    public void deleteAllData() { repository.deleteAllData(); }
    public String[] getCredentials() { return new String[]{sharedPreferences.getString("username", ""), sharedPreferences.getString("email", "")}; }
    public Cursor getUserByName(String user){ return repository.getUserByName(user);}
    public void deleteOneRow(String row_id){ repository.deleteOneRow(row_id);}
    public boolean FindUser(String user) { return repository.FindUser(user);}
    public void DeleteMarker(String marker) { repository.DeleteMarker(marker);};
    public void DeleteAllMarkers(){ repository.DeleteAllMarkers();};




}
