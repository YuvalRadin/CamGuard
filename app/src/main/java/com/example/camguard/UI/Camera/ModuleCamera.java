package com.example.camguard.UI.Camera;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.camguard.Data.Repository.Repository;

public class ModuleCamera {

    Repository repository;
    Context context;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;
    public ModuleCamera(Context context)
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

    public int getReports(String user) { return repository.getReportsByID(repository.getIdByName(user));}

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

    public void AddReport(String id) { repository.AddReport(id);}

    public String getIdByName(String user) { return repository.getIdByName(user);}

}