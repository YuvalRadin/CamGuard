package com.example.camguard.UI.Register;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.Data.Repository.Repository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.atomic.AtomicBoolean;


public class moduleRegister {

    Context context;
    Repository repository;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseFirestore FireStore;
    public moduleRegister(Context context)
    {
        this.context = context;
        repository = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        FireStore = FirebaseFirestore.getInstance();
    }
    public boolean UserExistsNotLocal(String user, String email) { return repository.userExistsNotLocal(user,email); }
    public void addUser(String Username, String Email, String Password) { repository.addUser(Username,Email,Password);}
    public void AddUserToFireBase(String user, String email, String password) { repository.addUserToFireBase(user,email,password);}

    public void doesUserAndEmailExist(String user, String email, FirebaseHelper.CredentialsCheck callback) { repository.doesUserAndEmailExist(user, email, callback);}
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
        if(!(etPassword.getText().toString().equals(etPasswordConfirmation.getText().toString())))
        {
            etPassword.setError("Password Confirmation does not match");
            return false;
        }


        //Username & Email Availability checks
        if(!(repository.findUser(etUser.getText().toString())))
        {
            etUser.setError("Username already exists");
            return false;
        }
        if(!(repository.findEmail(etEmail.getText().toString())))
        {
            etEmail.setError("Email already exists");
            return false;
        }

        return true;
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
    public Cursor getUserByName(String user){ return repository.getUserByName(user);}


}
