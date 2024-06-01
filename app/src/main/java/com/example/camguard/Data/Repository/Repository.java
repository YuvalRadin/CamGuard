package com.example.camguard.Data.Repository;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.DB.MyDatabaseHelper;
import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Repository class that provides methods for interacting with both SQLite and Firebase databases.
 */
public class Repository {
    private Context context;
    private MyDatabaseHelper myDatabaseHelper;
    private FirebaseHelper myFirebaseHelper;

    /**
     * Constructs a new Repository.
     *
     * @param context The application context.
     */
    public Repository(Context context) {
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(this.context);
        myFirebaseHelper = new FirebaseHelper(this.context);
    }

    // SQLite database operations

    /**
     * Updates user data in the SQLite database.
     *
     * @param id    The user's ID.
     * @param name  The user's name.
     * @param pass  The user's password.
     * @param email The user's email.
     */
    public void updateUser(String id, String name, String pass, String email) {
        myDatabaseHelper.updateData(id, name, pass, email);
    }

    /**
     * Finds a user in the SQLite database by username.
     *
     * @param user The username to search for.
     * @return True if the user exists, false otherwise.
     */
    public boolean findUser(String user) {
        return myDatabaseHelper.findUser(user);
    }

    /**
     * Finds a user in the SQLite database by email.
     *
     * @param email The email to search for.
     * @return True if the email exists, false otherwise.
     */
    public boolean findEmail(String email) {
        return myDatabaseHelper.findEmail(email);
    }

    /**
     * Checks if a user exists in the SQLite database but not locally.
     *
     * @param user  The username to search for.
     * @param email The email to search for.
     * @return True if the user exists, false otherwise.
     */
    public boolean userExistsNotLocal(String user, String email) {
        return myDatabaseHelper.userExistsNotLocal(user, email);
    }

    /**
     * Attempts to log in a user with the given username and password.
     *
     * @param user       The username.
     * @param password   The password.
     * @param EmailLogin The email login flag.
     * @return True if the login is successful, false otherwise.
     */
    public boolean loginUser(String user, String password, int EmailLogin) {
        return myDatabaseHelper.loginUser(user, password, EmailLogin);
    }

    /**
     * Adds a new user to the SQLite database.
     *
     * @param Username The username.
     * @param Password The password.
     * @param Email    The email.
     */
    public void addUser(String Username, String Password, String Email) {
        myDatabaseHelper.addUser(Username, Password, Email);
    }

    /**
     * Deletes all data from the SQLite database.
     */
    public void deleteAllData() {
        myDatabaseHelper.deleteAllData();
    }

    /**
     * Retrieves a user from the SQLite database by username.
     *
     * @param user The username to search for.
     * @return A cursor pointing to the retrieved user.
     */
    public Cursor getUserByName(String user) {
        return myDatabaseHelper.getUserByName(user);
    }

    /**
     * Retrieves a user from the SQLite database by email.
     *
     * @param email The email to search for.
     * @return A cursor pointing to the retrieved user.
     */
    public Cursor getUserByEmail(String email) {
        return myDatabaseHelper.getUserByEmail(email);
    }

    /**
     * Retrieves the number of reports for a user by their ID.
     *
     * @param ID The user's ID.
     * @return The number of reports.
     */
    public int getReportsByID(String ID) {
        return myDatabaseHelper.getReportsByID(ID);
    }

    /**
     * Retrieves the user ID by username.
     *
     * @param user The username to search for.
     * @return The user ID.
     */
    public String getIdByName(String user) {
        return myDatabaseHelper.getIdByName(user);
    }

    /**
     * Updates the number of reports for a user.
     *
     * @param id      The user's ID.
     * @param reports The new number of reports.
     */
    public void updateReports(String id, int reports) {
        myDatabaseHelper.updateReports(id, reports);
    }

    /**
     * Deletes a single row from the SQLite database by row ID.
     *
     * @param row_id The ID of the row to delete.
     */
    public void deleteOneRow(String row_id) {
        myDatabaseHelper.deleteOneRow(row_id);
    }

    // Firebase database operations

    /**
     * Retrieves documents from the Firebase database.
     *
     * @param which    The type of documents to retrieve.
     * @param callback The callback to handle the retrieved documents.
     */
    public void retrieveDocs(int which, FirebaseHelper.DocsRetrievedListener callback) {
        myFirebaseHelper.retrieveDocs(which, callback);
    }

    /**
     * Checks if a user exists in the Firebase database.
     *
     * @param user     The username.
     * @param password The password.
     * @param callback The callback to handle the result.
     */
    public void doesUserExist(String user, String password, FirebaseHelper.SearchComplete callback) {
        myFirebaseHelper.doesUserExist(user, password, callback);
    }

    /**
     * Checks if a user and email exist in the Firebase database.
     *
     * @param user     The username.
     * @param email    The email.
     * @param callback The callback to handle the result.
     */
    public void doesUserAndEmailExist(String user, String email, FirebaseHelper.CredentialsCheck callback) {
        myFirebaseHelper.doesUserAndEmailExist(user, email, callback);
    }

    /**
     * Checks if a user and email exist in the Firebase database, excluding the current user.
     *
     * @param user     The username.
     * @param email    The email.
     * @param callback The callback to handle the result.
     */
    public void checkUserAndEmailExistence(String user, String email, FirebaseHelper.CredentialsCheck callback) {
        myFirebaseHelper.checkUserAndEmailExistence(user, email, callback);
    }

    /**
     * Adds a report to the Firebase database and updates the local database.
     *
     * @param latLng      The location of the report.
     * @param Description The description of the report.
     * @param reportImage The image associated with the report.
     * @param mMap        The GoogleMap instance to add the marker to.
     */
    public void addReport(LatLng latLng, String Description, Bitmap reportImage, GoogleMap mMap) {
        myFirebaseHelper.addReport(latLng, Description, reportImage, mMap);
        myDatabaseHelper.addReport(CurrentUser.getId());
    }

    /**
     * Creates custom markers on the map from a list of document IDs.
     *
     * @param documentsIds The list of document IDs.
     * @param mMap         The GoogleMap instance to add the markers to.
     */
    public void createCustomMarkers(List<String> documentsIds, GoogleMap mMap) {
        myFirebaseHelper.createCustomMarkers(documentsIds, mMap);
    }

    /**
     * Deletes all users from the Firebase database.
     */
    public void deleteAllFireStoreUsers() {
        myFirebaseHelper.deleteAllFireStoreUsers();
    }

    /**
     * Deletes a specific user from the Firebase database.
     *
     * @param user The username of the user to delete.
     */
    public void deleteFireStoreUser(String user) {
        myFirebaseHelper.deleteFireStoreUser(user);
    }

    /**
     * Updates a user in the Firebase database.
     *
     * @param user    The current username.
     * @param upUser  The new username.
     * @param upEmail The new email.
     * @param upPass  The new password.
     */
    public void updateFireStoreUser(String user, String upUser, String upEmail, String upPass) {
        myFirebaseHelper.updateFireStoreUser(user, upUser, upEmail, upPass);
    }

    /**
     * Adds a user to the Firebase database.
     *
     * @param user     The username.
     * @param email    The email.
     * @param password The password.
     */
    public void addUserToFireBase(String user, String email, String password) {
        myFirebaseHelper.addUserToFireBase(user, email, password);
    }

    /**
     * Deletes a marker from the Firebase database by its ID.
     *
     * @param marker The ID of the marker to delete.
     */
    public void deleteMarkerByID(String marker) {
        myFirebaseHelper.deleteMarkerByID(marker);
    }

    /**
     * Deletes a marker from the Firebase database by its description.
     *
     * @param marker The description of the marker to delete.
     */
    public void deleteMarkerByDesc(String marker) {
        myFirebaseHelper.deleteMarkerByDesc(marker);
    }

    /**
     * Deletes all markers from the Firebase database.
     */
    public void deleteAllMarkers() {
        myFirebaseHelper.deleteAllMarkers();
    }

    /**
     * Retrieves the current user's markers from the Firebase database.
     *
     * @param callback The callback to handle the retrieved markers.
     */
    public void getMyMarkers(FirebaseHelper.markersGotten callback) {
        myFirebaseHelper.getMyMarkers(callback);
    }
}