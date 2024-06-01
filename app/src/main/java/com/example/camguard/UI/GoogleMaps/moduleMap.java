package com.example.camguard.UI.GoogleMaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.Data.Repository.Repository;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class moduleMap {

    Context context;
    Repository repository;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    /**
     * Constructor for the moduleMap class.
     *
     * @param context The context of the calling activity or application.
     */
    public moduleMap(Context context) {
        this.context = context;
        repository = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Removes stored user credentials from shared preferences.
     */
    public void DoNotRemember() {
        editor.remove("username");
        editor.remove("password");
        editor.remove("Remember");
        editor.apply();
    }

    /**
     * Updates the number of reports for a user in the repository.
     *
     * @param id      The ID of the user.
     * @param reports The new number of reports for the user.
     */
    public void UpdateReports(String id, int reports) {
        repository.updateReports(id, reports);
    }

    /**
     * Checks if the 'Remember' flag is set in shared preferences.
     *
     * @return True if the 'Remember' flag is set, false otherwise.
     */
    public boolean DoesRemember() {
        return sharedPreferences.getBoolean("Remember", false);
    }

    /**
     * Checks if user credentials exist in shared preferences.
     *
     * @return True if user credentials exist, false otherwise.
     */
    public boolean CredentialsExist() {
        return sharedPreferences.contains("username");
    }

    /**
     * Retrieves user credentials from shared preferences.
     *
     * @return An array containing user credentials: [username, email].
     */
    public String[] getCredentials() {
        return new String[]{sharedPreferences.getString("username", ""), sharedPreferences.getString("email", "")};
    }

    /**
     * Adds a report to the repository.
     *
     * @param latLng      The latitude and longitude of the report.
     * @param Description The description of the report.
     * @param reportImage The image associated with the report.
     * @param mMap        The GoogleMap object.
     */
    public void AddReport(LatLng latLng, String Description, Bitmap reportImage, GoogleMap mMap) {
        repository.addReport(latLng, Description, reportImage, mMap);
    }

    /**
     * Creates custom markers on the map using document IDs.
     *
     * @param documentIds The list of document IDs.
     * @param mMap        The GoogleMap object.
     */
    public void CreateCustomMarkers(List<String> documentIds, GoogleMap mMap) {
        repository.createCustomMarkers(documentIds, mMap);
    }

    /**
     * Retrieves documents from the Firebase database.
     *
     * @param which    Identifier for the type of document to retrieve.
     * @param callback Callback interface for handling the retrieved documents.
     */
    public void retrieveDocs(int which, FirebaseHelper.DocsRetrievedListener callback) {
        repository.retrieveDocs(which, callback);
    }

}
