package com.example.camguard.UI.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.example.camguard.Data.Repository.Repository;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The ModuleAdmin class provides methods for administrative operations in the application.
 * It interacts with the repository for data management and SharedPreferences for storing user preferences.
 */
public class ModuleAdmin {

    // Instance variables
    private Repository repository;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * Constructs a new ModuleAdmin object.
     *
     * @param context The context of the calling activity or application.
     */
    public ModuleAdmin(Context context) {
        this.context = context;
        repository = new Repository(context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Clears the stored user login credentials from SharedPreferences.
     * This method is called when the user chooses not to be remembered.
     */
    public void DoNotRemember() {
        editor.remove("username");
        editor.remove("password");
        editor.remove("Remember");
        editor.apply();
    }

    /**
     * Deletes all data related to a specific user.
     */
    public void deleteAllData() {
        repository.deleteAllData();
    }

    /**
     * Retrieves user data by name.
     *
     * @param user The username of the user to retrieve.
     * @return A Cursor object containing the user data.
     */
    public Cursor getUserByName(String user) {
        return repository.getUserByName(user);
    }

    /**
     * Deletes a specific row from the database.
     *
     * @param row_id The ID of the row to delete.
     */
    public void deleteOneRow(String row_id) {
        repository.deleteOneRow(row_id);
    }

    /**
     * Checks if a user exists in the database.
     *
     * @param user The username to check.
     * @return True if the user exists, otherwise false.
     */
    public boolean FindUser(String user) {
        return repository.findUser(user);
    }

    /**
     * Deletes a marker by its ID.
     *
     * @param marker The ID of the marker to delete.
     */
    public void deleteMarkerByID(String marker) {
        repository.deleteMarkerByID(marker);
    }

    /**
     * Deletes a marker by its description.
     *
     * @param marker The description of the marker to delete.
     */
    public void deleteMarkerByDesc(String marker) {
        repository.deleteMarkerByDesc(marker);
    }

    /**
     * Deletes all markers from the database.
     */
    public void deleteAllMarkers() {
        repository.deleteAllMarkers();
    }

    /**
     * Deletes all users from the Firestore database.
     */
    public void deleteAllFireStoreUsers() {
        repository.deleteAllFireStoreUsers();
    }

    /**
     * Deletes a specific user from the Firestore database.
     *
     * @param user The username of the user to delete.
     */
    public void deleteFireStoreUser(String user) {
        repository.deleteFireStoreUser(user);
    }
}
