package com.example.trashproject.UI.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.EditText;

import com.example.trashproject.Data.Repository.Repository;

public class moduleLogin {

    Context context;
    Repository rp;

    public moduleLogin(Context context)
    {
        this.context = context;
        rp = new Repository(this.context);
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
        public void RememberMe(EditText etUser){
            SharedPreferences sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", etUser.getText().toString());
            editor.putString("email", getUserByName(etUser.getText().toString()).getString(3));
            editor.apply();
        }

    public Cursor getUserByName(String user){ return rp.getUserByName(user);}



}
