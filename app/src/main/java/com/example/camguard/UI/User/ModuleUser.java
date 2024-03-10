package com.example.camguard.UI.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.example.camguard.Data.Repository.Repository;

public class ModuleUser {

    Repository repository;
    Context context;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;
    public ModuleUser(Context context)
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
        editor.remove("email");
        editor.remove("Remember");
        editor.apply();
    }
    public Cursor getUserByName(String user){ return repository.getUserByName(user);}
    public boolean DoesRemember()
    {
        return sharedPreferences.getBoolean("Remember", false);
    }
    public void UpdateUser(String id, String name, String pass, String email) { repository.UpdateUser(id, name, pass, email);}
    public void UpdateFireStoreUser(String user, String upUser, String upEmail, String upPass) { repository.UpdateFireStoreUser(user,upUser,upEmail,upPass);}
    public void UpdateSharedPreference(String name, String email) { editor.putString("username", name); editor.putString("email", email); editor.apply();}

}
