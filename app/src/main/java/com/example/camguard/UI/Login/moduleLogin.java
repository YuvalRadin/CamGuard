package com.example.camguard.UI.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.Data.Repository.Repository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class moduleLogin {

    Context context;
    Repository repository;
    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;


    public moduleLogin(Context context)
    {
        this.context = context;
        repository = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void doesUserExist(String user, String password, FirebaseHelper.SearchComplete callback) { repository.doesUserExist(user, password, callback);}

    public void addUser(String Username, String Password, String Email) { repository.addUser(Username,Password,Email);}
    public boolean UserExistsNotLocal(String user, String email) { return repository.userExistsNotLocal(user,email); }

    public void RememberMe(boolean flag){
        editor.putBoolean("Remember", flag);
        editor.apply();
    }
    public Cursor getUserByName(String user){ return repository.getUserByName(user);}

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
    public String[] getCredentials() { return new String[]{sharedPreferences.getString("username", ""), sharedPreferences.getString("email", "")}; }
    public String getIdByName(String user) { return repository.getIdByName(user);}
    public boolean isExist(String user) { return !repository.findUser(user);}








}
