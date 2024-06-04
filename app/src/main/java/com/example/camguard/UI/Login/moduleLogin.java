package com.example.camguard.UI.Login;

import android.app.ProgressDialog;
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

    private Context context;
    private Repository repository;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * Constructor for the moduleLogin class.
     *
     * @param context The context of the calling activity.
     */
    public moduleLogin(Context context) {
        this.context = context;
        repository = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Checks if the user exists in Firebase authentication.
     *
     * @param user     The username to check.
     * @param password The password to check.
     * @param callback Callback interface for handling search completion.
     */
    public void doesUserExist(String user, String password, FirebaseHelper.SearchComplete callback) {
        repository.doesUserExist(user, password, callback);
    }

    /**
     * Adds a new user to the Firebase database.
     *
     * @param Username The username of the new user.
     * @param Password The password of the new user.
     * @param Email    The email address of the new user.
     */
    public void addUser(String Username, String Password, String Email) {
        repository.addUser(Username, Password, Email);
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
     * Sets the "Remember" flag in SharedPreferences.
     *
     * @param flag The boolean value to set for the "Remember" flag.
     */
    public void RememberMe(boolean flag) {
        editor.putBoolean("Remember", flag);
        editor.apply();
    }

    /**
     * Retrieves user data from the database by username.
     *
     * @param user The username of the user to retrieve.
     * @return A Cursor object containing the user data.
     */
    public Cursor getUserByName(String user) {
        return repository.getUserByName(user);
    }

    /**
     * Saves user data in SharedPreferences.
     *
     * @param etUser The EditText view containing the username.
     */
    public void SaveUser(EditText etUser) {
        if(!etUser.getText().toString().contains("@")) {
            editor.putString("username", etUser.getText().toString());
            editor.putString("email", getUserByName(etUser.getText().toString()).getString(3));
            editor.apply();
        }
        else {
            editor.putString("username", repository.getUserByEmail(etUser.getText().toString()).getString(1));
            editor.putString("email", etUser.getText().toString());
            editor.apply();
        }
    }

    /**
     * Checks if user credentials exist in SharedPreferences.
     *
     * @return True if user credentials exist, false otherwise.
     */
    public boolean CredentialsExist() {
        return sharedPreferences.contains("username");
    }

    /**
     * Retrieves user credentials from SharedPreferences.
     *
     * @return An array containing the username and email address.
     */
    public String[] getCredentials() {
        return new String[]{sharedPreferences.getString("username", ""), sharedPreferences.getString("email", "")};
    }

    /**
     * Retrieves the user ID from the database by username.
     *
     * @param user The username of the user.
     * @return The ID of the user.
     */
    public String getIdByName(String user) {
        return repository.getIdByName(user);
    }

    /**
     * Checks if the user exists in the local database.
     *
     * @param user The username to check.
     * @return True if the user exists in the local database, false otherwise.
     */
    public boolean isExist(String user) {
        return !repository.findUser(user);
    }

    public void deleteAllSQLData()
    {
        repository.deleteAllData();
    }

    public void updateAllSQLData(ProgressDialog pd) {
        repository.updateAllSQLData(pd);
    }









}
