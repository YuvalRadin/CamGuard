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
    public void DoNotRemember()
    {
        editor.remove("username");
        editor.remove("password");
        editor.remove("Remember");
        editor.apply();
    }

    public void deleteAllData() { repository.deleteAllData(); }
    public Cursor getUserByName(String user){ return repository.getUserByName(user);}
    public void deleteOneRow(String row_id){ repository.deleteOneRow(row_id);}
    public boolean FindUser(String user) { return repository.FindUser(user);}
    public void DeleteMarkerByID(String marker) { repository.DeleteMarkerByID(marker);}
    public void DeleteMarkerByDesc(String marker) { repository.DeleteMarkerByDesc(marker);}
    public void DeleteAllMarkers(){ repository.DeleteAllMarkers();}




}
