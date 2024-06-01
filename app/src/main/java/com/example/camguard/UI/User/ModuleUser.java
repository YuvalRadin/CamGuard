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

    /**
     * Constructor for ModuleUser class.
     *
     * @param context The context of the calling activity or application.
     */
    public ModuleUser(Context context) {
        this.context = context;
        repository = new Repository(context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Retrieves markers associated with the current user.
     *
     * @param callback Callback to handle the retrieved markers.
     */
    public void getMyMarkers(FirebaseHelper.markersGotten callback) {
        repository.getMyMarkers(callback);
    }

    /**
     * Deletes a marker by its ID.
     *
     * @param marker The ID of the marker to be deleted.
     */
    public void deleteMarkerByID(String marker) {
        repository.deleteMarkerByID(marker);
    }

    /**
     * Checks the existence of a user and email in the database.
     *
     * @param user     The username to be checked.
     * @param email    The email to be checked.
     * @param callback Callback to handle the check results.
     */
    public void checkUserAndEmailExistence(String user, String email, FirebaseHelper.CredentialsCheck callback) {
        repository.checkUserAndEmailExistence(user, email, callback);
    }

    /**
     * Gets the number of reports associated with a user.
     *
     * @param user The username for which reports are counted.
     * @return The number of reports associated with the user.
     */
    public int getReports(String user) {
        return repository.getReportsByID(repository.getIdByName(user));
    }

    /**
     * Removes user credentials from shared preferences.
     */
    public void DoNotRemember() {
        editor.remove("username");
        editor.remove("email");
        editor.remove("Remember");
        editor.apply();
    }

    /**
     * Retrieves user data by username.
     *
     * @param user The username for which data is retrieved.
     * @return Cursor containing user data.
     */
    public Cursor getUserByName(String user) {
        return repository.getUserByName(user);
    }

    /**
     * Checks if "Remember" option is enabled in shared preferences.
     *
     * @return True if "Remember" option is enabled, false otherwise.
     */
    public boolean DoesRemember() {
        return sharedPreferences.getBoolean("Remember", false);
    }

    /**
     * Updates user data in the database.
     *
     * @param id    The ID of the user.
     * @param name  The updated username.
     * @param pass  The updated password.
     * @param email The updated email.
     */
    public void updateUser(String id, String name, String pass, String email) {
        repository.updateUser(id, name, pass, email);
    }

    /**
     * Updates user data in Firestore.
     *
     * @param user   The current username.
     * @param upUser The updated username.
     * @param upEmail The updated email.
     * @param upPass The updated password.
     */
    public void updateFireStoreUser(String user, String upUser, String upEmail, String upPass) {
        repository.updateFireStoreUser(user, upUser, upEmail, upPass);
    }

    /**
     * Updates shared preferences with username and email.
     *
     * @param name  The username to be saved.
     * @param email The email to be saved.
     */
    public void updateSharedPreference(String name, String email) {
        editor.putString("username", name);
        editor.putString("email", email);
        editor.apply();
    }


    /**
     * Performs validation checks for username, email, and password fields.
     * Checks include length, format, and availability.
     *
     * @param etUser     EditText field for username.
     * @param etEmail    EditText field for email.
     * @param etPassword EditText field for password.
     * @return True if all checks pass, false otherwise.
     */
    public Boolean CheckUps(EditText etUser, EditText etEmail, EditText etPassword) {
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


        return true;
    }
}
