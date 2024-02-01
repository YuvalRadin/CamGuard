package com.example.trashproject.UI.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.EditText;

import com.example.trashproject.Data.Repository.Repository;

public class moduleLogin {

    Context context;
    Repository rp;
    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;


    public moduleLogin(Context context)
    {
        this.context = context;
        rp = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public int isExist(EditText etUser, EditText etPass)
    {
        if(etUser.getText().toString().contains("@"))
        {
            if (!rp.LoginUser(etUser.getText().toString(), etPass.getText().toString(), 2)) {
                return 2;
            } else
                return 0;

        }
        else {
            if (!rp.LoginUser(etUser.getText().toString(), etPass.getText().toString(), 1)) {
                return 1;
            } else
                return 0;
        }
    }
    public void RememberMe(boolean flag){
        editor.putBoolean("Remember", flag);
        editor.apply();
    }

    public Cursor getUserByName(String user){ return rp.getUserByName(user);}

    public void SaveUser(EditText etUser)
    {
        editor.putString("username", etUser.getText().toString());
        editor.putString("email", getUserByName(etUser.getText().toString()).getString(3));
        editor.apply();
    }
    public boolean CredentialsExist()
        {
            return sharedPreferences.contains("username");
        }






}
