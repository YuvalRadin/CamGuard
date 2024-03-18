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
    public int isExist(EditText etUser, EditText etPass)
    {
        if(etUser.getText().toString().contains("@"))
        {
            if (!repository.loginUser(etUser.getText().toString(), etPass.getText().toString(), 2)) {
                return 2;
            } else
                return 0;

        }
        else {
            if (!repository.loginUser(etUser.getText().toString(), etPass.getText().toString(), 1)) {
                return 1;
            } else
                return 0;
        }
    }
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
    public void deleteAllData() { repository.deleteAllData(); }
    public void updateLocalDB() {
        FirebaseFirestore FireStore = FirebaseFirestore.getInstance();

        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    repository.userExistsNotLocal(document.getData().get("name").toString(), document.getData().get("email").toString());
                    repository.addUser(document.getData().get("name").toString(), document.getData().get("password").toString(), document.getData().get("email").toString());
                }
            }
        });

    }







}
