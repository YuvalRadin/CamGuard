package com.example.camguard.Data.Repository;

import android.content.Context;
import android.database.Cursor;

import com.example.camguard.Data.DB.MyDatabaseHelper;

public class Repository {
    Context context;

    MyDatabaseHelper myDatabaseHelper;

    public Repository(Context context)
    {
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(this.context);
    }

    public boolean FindUser(String user) { return myDatabaseHelper.FindUser(user);}
    public boolean FindEmail(String email) { return myDatabaseHelper.FindEmail(email);}

    public boolean LoginUser(String user, String password, int EmailLogin) { return myDatabaseHelper.LoginUser(user, password, EmailLogin); }

    public void addUser(String Username, String Email, String Password) { myDatabaseHelper.addUser(Username, Email, Password);}

    public void deleteAllData() { myDatabaseHelper.deleteAllData(); }

    public Cursor getUserByName(String user){ return myDatabaseHelper.getUserByName(user);}

    public int getReportsByID(String ID) { return myDatabaseHelper.getReportsByID(ID);}

    public String getIdByName(String user) { return myDatabaseHelper.getIdByName(user);}

    public String getNameByEmail(String email) { return myDatabaseHelper.getNameByEmail(email);}

    public void AddReport(String id) { myDatabaseHelper.AddReport(id);}



}
