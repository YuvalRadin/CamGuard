package com.example.camguard.UI.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.EditText;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.FireBase.FirebaseHelper;
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


    public void getMyMarkers(FirebaseHelper.markersGotten callback) { repository.getMyMarkers(callback);}
    public void deleteMarkerByID(String marker) { repository.deleteMarkerByID(marker); }

    public void checkUserAndEmailExistence(String user, String email , FirebaseHelper.CredentialsCheck callback) {repository.checkUserAndEmailExistence(user,email,callback); }
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
    public void updateUser(String id, String name, String pass, String email) { repository.updateUser(id, name, pass, email);}
    public void updateFireStoreUser(String user, String upUser, String upEmail, String upPass) { repository.updateFireStoreUser(user,upUser,upEmail,upPass);}
    public void updateSharedPreference(String name, String email) { editor.putString("username", name); editor.putString("email", email); editor.apply();}

    public Boolean CheckUps(EditText etUser, EditText etEmail, EditText etPassword)
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
        if(!etEmail.getText().toString().contains(".co.")&&etEmail.getText().toString().indexOf(".") != etEmail.getText().toString().lastIndexOf("."))
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


        //Username & Email Availability checks
        if(!(repository.findUser(etUser.getText().toString())) && !etUser.getText().toString().equals(CurrentUser.getName()))
        {
            etUser.setError("Username already exists");
            return false;
        }
        if(!(repository.findEmail(etEmail.getText().toString())) && !etEmail.getText().toString().equals(CurrentUser.getEmail()))
        {
            etEmail.setError("Email already exists");
            return false;
        }

        return true;
    }
}
