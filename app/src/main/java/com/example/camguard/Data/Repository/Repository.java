package com.example.camguard.Data.Repository;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.CustomMarkerAdapter.CustomInfoWindowAdapter;
import com.example.camguard.Data.DB.MyDatabaseHelper;
import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.R;
import com.example.camguard.UI.GoogleMaps.FragmentMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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





}
