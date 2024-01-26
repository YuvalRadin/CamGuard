package com.example.trashproject.UI.Register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trashproject.RegisterActivity;
import com.example.trashproject.UI.GoogleMaps.FragmentsActivity;
import com.example.trashproject.repository.Repository;


public class modleRegister {

    Context context;
    Repository rp;

    public modleRegister(Context context)
    {
        this.context = context;
        rp = new Repository(this.context);
    }

    public void SharedPrefrences(EditText etUser, EditText etEmail, EditText etPassword)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Main",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", etUser.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.putString("password", etPassword.getText().toString());
    }


    public Boolean CheckUps(EditText etUser, EditText etEmail, EditText etPassword, EditText etPasswordConfirmation)
    {
        // username validity checkups
        if(etUser.getText().toString().isEmpty())
        {
            etUser.setError("Fill Username");
            return false;
        }
        if(etUser.getText().toString().length() < 3)
        {
            etUser.setError("Username must be over 3 characters");
            return false;
        }

        // email validity checkups
        if(etEmail.getText().toString().indexOf("@") <= 1)
        {
            etEmail.setError("invalid email (x@)");
            return false;
        }
        if(etEmail.getText().toString().indexOf("@") != etEmail.getText().toString().lastIndexOf("@"))
        {
            etEmail.setError("invalid email (@@)");
            return false;
        }
        if(etEmail.getText().toString().indexOf(".") - etEmail.getText().toString().indexOf("@") <= 3)
        {
            etEmail.setError("invalid email (.@)");
            return false;
        }
        if(etEmail.getText().toString().indexOf(".") != etEmail.getText().toString().lastIndexOf("."))
        {
            etEmail.setError("invalid email (..)");
            return false;
        }
        if(!(etEmail.getText().toString().contains(".com")) && !(etEmail.getText().toString().contains(".co.")))
        {
            etEmail.setError("invalid email (.com/.co)");
            return false;
        }



        //password validity checkups
        if(etPassword.getText().toString().equals(""))
        {
            etPassword.setError("Fill Password");
            return false;
        }
        if(etPassword.getText().toString().length() < 3)
        {
            etPassword.setError("Password isn't strong enough");
            return false;
        }
        if(!(etPassword.getText().toString().equals(etPasswordConfirmation.getText().toString())))
        {
            etPassword.setError("Password Confirmation does not match");
            return false;
        }
        if(!(rp.getMyDatabaseHelper().FindUser(etUser.getText().toString())))
        {
            etUser.setError("Username already exists");
            return false;
        }

        rp.getMyDatabaseHelper().addUser(etUser.getText().toString(),etPassword.getText().toString(),etEmail.getText().toString());
        etPassword.setText("");
        etPasswordConfirmation.setText("");
        etUser.setText("");
        etEmail.setText("");


        return true;
    }

    public void DeleteAllData()
    {
        rp.getMyDatabaseHelper().deleteAllData();
    }
}
