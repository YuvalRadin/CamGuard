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

public class Repository {
    Context context;

    MyDatabaseHelper myDatabaseHelper;
    FirebaseHelper myFirebaseHelper;

    public Repository(Context context)
    {
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(this.context);
        myFirebaseHelper = new FirebaseHelper(this.context);
    }

    //SQLite database
    public void updateUser(String id, String name, String pass, String email) { myDatabaseHelper.updateData(id, name, pass, email);}
    public boolean findUser(String user) { return myDatabaseHelper.findUser(user);}
    public boolean findEmail(String email) { return myDatabaseHelper.findEmail(email);}
    public boolean userExistsNotLocal(String user, String email) { return myDatabaseHelper.userExistsNotLocal(user, email);}
    public boolean loginUser(String user, String password, int EmailLogin) { return myDatabaseHelper.loginUser(user, password, EmailLogin); }
    public void addUser(String Username, String Password, String Email) { myDatabaseHelper.addUser(Username, Password, Email);}
    public void deleteAllData() { myDatabaseHelper.deleteAllData(); }
    public Cursor getUserByName(String user){ return myDatabaseHelper.getUserByName(user);}
    public int getReportsByID(String ID) { return myDatabaseHelper.getReportsByID(ID);}
    public String getIdByName(String user) { return myDatabaseHelper.getIdByName(user);}
    public void updateReports(String id, int reports) { myDatabaseHelper.updateReports(id,reports);}
    public void deleteOneRow(String row_id){ myDatabaseHelper.deleteOneRow(row_id);}


    //Firebase database
    public void retrieveDocs(int which, FirebaseHelper.DocsRetrievedListener callback) { myFirebaseHelper.retrieveDocs(which, callback);}
    public void doesUserExist(String user, String password, FirebaseHelper.SearchComplete callback) { myFirebaseHelper.doesUserExist(user, password, callback);}
    public void doesUserAndEmailExist(String user, String email, FirebaseHelper.CredentialsCheck callback) { myFirebaseHelper.doesUserAndEmailExist(user, email, callback);}
    public void checkUserAndEmailExistence(String user, String email , FirebaseHelper.CredentialsCheck callback) {myFirebaseHelper.checkUserAndEmailExistence(user,email,callback); }
    public void addReport(LatLng latLng, String Description, Bitmap reportImage, GoogleMap mMap) { myFirebaseHelper.addReport(latLng,Description,reportImage,mMap); myDatabaseHelper.addReport(CurrentUser.getId());}
    public void createCustomMarkers(List<String> documentsIds, GoogleMap mMap) { myFirebaseHelper.createCustomMarkers(documentsIds, mMap);}
    public void deleteAllFireStoreUsers() { myFirebaseHelper.deleteAllFireStoreUsers();}
    public void deleteFireStoreUser(String user) { myFirebaseHelper.deleteFireStoreUser(user);}
    public void updateFireStoreUser(String user, String upUser, String upEmail, String upPass) { myFirebaseHelper.updateFireStoreUser(user,upUser,upEmail,upPass);}
    public void addUserToFireBase(String user, String email, String password) { myFirebaseHelper.addUserToFireBase(user,email,password); }
    public void deleteMarkerByID(String marker) { myFirebaseHelper.deleteMarkerByID(marker); }
    public void deleteMarkerByDesc(String marker) {myFirebaseHelper.deleteMarkerByDesc(marker);}
    public void deleteAllMarkers() { myFirebaseHelper.deleteAllMarkers();}
    public void getMyMarkers(FirebaseHelper.markersGotten callback) { myFirebaseHelper.getMyMarkers(callback);}





}
