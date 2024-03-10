package com.example.camguard.UI.GoogleMaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

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


    public moduleMap(Context context)
    {
        this.context = context;
        repository = new Repository(this.context);
        sharedPreferences = context.getSharedPreferences("Main", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void DoNotRemember()
    {
        editor.remove("username");
        editor.remove("password");
        editor.remove("Remember");
        editor.apply();
    }

    public void UpdateReports(String id, int reports) { repository.UpdateReports(id,reports);}
    public boolean DoesRemember()
    {
        return sharedPreferences.getBoolean("Remember", false);
    }
    public boolean CredentialsExist()
    {
        return sharedPreferences.contains("username");
    }
    public String[] getCredentials() { return new String[]{sharedPreferences.getString("username", ""), sharedPreferences.getString("email", "")}; }
    public void AddReport(LatLng latLng, String Description, Bitmap reportImage, GoogleMap mMap) { repository.AddReport(latLng,Description,reportImage,mMap); }
    public void CreateCustomMarkers(List<String> documentIds, GoogleMap mMap) { repository.CreateCustomMarkers(documentIds, mMap);}
    public void getAllDocumentIds(Repository.DocumentIdCallback callback) { repository.getAllDocumentIds(callback);}

}
