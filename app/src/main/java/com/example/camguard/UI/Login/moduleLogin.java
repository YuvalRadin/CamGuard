package com.example.camguard.UI.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.Repository.Repository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.Current;

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
    public void addUser(String Username, String Password, String Email) { rp.addUser(Username,Password,Email);}
    public boolean UserExistsNotLocal(String user, String email) { return rp.UserExistsNotLocal(user,email); }
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
    public String[] getCredentials() { return new String[]{sharedPreferences.getString("username", ""), sharedPreferences.getString("email", "")}; }
    public String getIdByName(String user) { return rp.getIdByName(user);}
    public boolean isExist(String user) { return !rp.FindUser(user);}
    public void deleteAllData() { rp.deleteAllData(); }
    public void updateLocalDB() {
        FirebaseFirestore FireStore = FirebaseFirestore.getInstance();

        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    rp.UserExistsNotLocal(document.getData().get("name").toString(), document.getData().get("email").toString());
                    rp.addUser(document.getData().get("name").toString(), document.getData().get("password").toString(), document.getData().get("email").toString());
                }
            }
        });

    }







}
