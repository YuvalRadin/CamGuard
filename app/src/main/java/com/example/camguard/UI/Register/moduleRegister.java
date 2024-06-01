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

    private Context context;
    private Repository repository;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore FireStore;

    /**
     * Constructor for the moduleRegister class.
     *
     * @param context The context of the calling activity.
     */
    public moduleRegister(Context context) {
        this.context = context;
        repository = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        FireStore = FirebaseFirestore.getInstance();
    }

    /**
     * Checks if the user exists in Firebase but not locally.
     *
     * @param user  The username to check.
     * @param email The email address to check.
     * @return True if the user exists in Firebase but not locally, false otherwise.
     */
    public boolean UserExistsNotLocal(String user, String email) {
        return repository.userExistsNotLocal(user, email);
    }

    /**
     * Adds a new user to the local database.
     *
     * @param Username The username of the new user.
     * @param Email    The email address of the new user.
     * @param Password The password of the new user.
     */
    public void addUser(String Username, String Email, String Password) {
        repository.addUser(Username, Email, Password);
    }

    /**
     * Adds a new user to Firestore.
     *
     * @param user     The username of the new user.
     * @param email    The email address of the new user.
     * @param password The password of the new user.
     */
    public void AddUserToFireBase(String user, String email, String password) {
        repository.addUserToFireBase(user, email, password);
    }

    /**
     * Retrieves documents from the database based on the specified type.
     *
     * @param which    The type of documents to retrieve.
     * @param callback Callback interface for handling document retrieval.
     */
    public void retrieveDocs(int which, FirebaseHelper.DocsRetrievedListener callback) {
        repository.retrieveDocs(which, callback);
    }

    /**
     * Checks if the username and email already exist in the database.
     *
     * @param user     The username to check.
     * @param email    The email address to check.
     * @param callback Callback interface for handling credential check.
     */
    public void doesUserAndEmailExist(String user, String email, FirebaseHelper.CredentialsCheck callback) {
        repository.doesUserAndEmailExist(user, email, callback);
    }

    /**
     * Checks the validity of user input during registration.
     *
     * @param etUser                 The EditText field for the username.
     * @param etEmail                The EditText field for the email.
     * @param etPassword             The EditText field for the password.
     * @param etPasswordConfirmation The EditText field for confirming the password.
     * @return True if all input fields are valid and available, false otherwise.
     */
    public Boolean CheckUps(EditText etUser, EditText etEmail, EditText etPassword, EditText etPasswordConfirmation) {
        // Username validity checkups
        if (etUser.getText().toString().isEmpty()) {
            etUser.setError("Fill Username");
            return false;
        }
        if (etUser.getText().toString().length() < 3) {
            etUser.setError("Username must be over 3 characters");
            return false;
        }

        // Email validity checkups
        if (etEmail.getText().toString().indexOf("@") <= 1) {
            etEmail.setError("invalid email (x@)");
            return false;
        }
        if (etEmail.getText().toString().indexOf("@") != etEmail.getText().toString().lastIndexOf("@")) {
            etEmail.setError("invalid email (@@)");
            return false;
        }
        if (etEmail.getText().toString().indexOf(".") - etEmail.getText().toString().indexOf("@") <= 3) {
            etEmail.setError("invalid email (.@)");
            return false;
        }
        if (!etEmail.getText().toString().contains(".co.") && etEmail.getText().toString().indexOf(".") != etEmail.getText().toString().lastIndexOf(".")) {
            etEmail.setError("invalid email (..)");
            return false;
        }
        if (!(etEmail.getText().toString().contains(".com")) && !(etEmail.getText().toString().contains(".co."))) {
            etEmail.setError("invalid email (.com/.co)");
            return false;
        }

        // Password validity checkups
        if (etPassword.getText().toString().equals("")) {
            etPassword.setError("Fill Password");
            return false;
        }
        if (etPassword.getText().toString().length() < 3) {
            etPassword.setError("Password isn't strong enough");
            return false;
        }
        if (!(etPassword.getText().toString().equals(etPasswordConfirmation.getText().toString()))) {
            etPassword.setError("Password Confirmation does not match");
            return false;
        }

        // Username & Email availability checks
        if (!(repository.findUser(etUser.getText().toString()))) {
            etUser.setError("Username already exists");
            return false;
        }
        if (!(repository.findEmail(etEmail.getText().toString()))) {
            etEmail.setError("Email already exists");
            return false;
        }

        return true;
    }

    /**
     * Saves the user's preference for remembering login credentials.
     *
     * @param flag The boolean value indicating whether to remember login credentials or not.
     */
    public void RememberMe(boolean flag){
        editor.putBoolean("Remember", flag);
        editor.apply();
    }

    /**
     * Saves the user's username and email for future reference.
     *
     * @param etUser  The EditText field for the username.
     * @param etEmail The EditText field for the email.
     */
    public void SaveUser(EditText etUser, EditText etEmail) {
        editor.putString("username", etUser.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.apply();
    }

    /**
     * Retrieves user information by username.
     *
     * @param user The username of the user to retrieve.
     * @return A Cursor containing the user information retrieved from the database.
     */
    public Cursor getUserByName(String user) {
        return repository.getUserByName(user);
    }


}
