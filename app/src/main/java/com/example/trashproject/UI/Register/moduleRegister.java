package com.example.trashproject.UI.Register;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.example.trashproject.Data.Repository.Repository;


public class moduleRegister {

    Context context;
    Repository rp;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public moduleRegister(Context context)
    {
        this.context = context;
        rp = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
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


        //Username & Email Availability checks
        if(!(rp.FindUser(etUser.getText().toString())))
        {
            etUser.setError("Username already exists");
            return false;
        }
        if(!(rp.FindEmail(etEmail.getText().toString())))
        {
            etEmail.setError("Email already exists");
            return false;
        }


        rp.addUser(etUser.getText().toString(),etPassword.getText().toString(),etEmail.getText().toString());

        return true;
    }

    public void DeleteAllData()
    {
        rp.deleteAllData();
    }

    public void RememberMe(boolean flag){
        editor.putBoolean("Remember", flag);
        editor.apply();
    }
    public void SaveUser(EditText etUser, EditText etEmail)
    {
        editor.putString("username", etUser.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.apply();
    }


}
