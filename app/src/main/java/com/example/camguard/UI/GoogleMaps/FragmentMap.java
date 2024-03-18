package com.example.camguard.UI.GoogleMaps;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.CustomMarkerAdapter.CustomInfoWindowAdapter;
import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.Data.Repository.Repository;
import com.example.camguard.R;
import com.example.camguard.UI.Admin.AdminActivity;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.Login.MainActivity;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class FragmentMap extends AppCompatActivity implements OnMapReadyCallback {


    BottomNavigationView bottomNavigationView;
    static GoogleMap mMap;
    Context context;
    LatLng latLng;
    Bitmap reportImage;
    moduleMap module;
    boolean isNewReport;
    static boolean reloadMap = true;



    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        context = getBaseContext();

        FragmentManager fragmentManager = getSupportFragmentManager();
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted && coarseLocationGranted != null && coarseLocationGranted) {
                                //location access granted.

                                if(reloadMap && module.CredentialsExist()) {
                                    Intent intent = new Intent(FragmentMap.this, MainActivity.class);
                                    startActivity(intent);
                                    reloadMap = false;
                                }
                            } else {
                                // No location access granted.
                                Intent intent = new Intent(FragmentMap.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }

                );
        locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});








        module = new moduleMap(this);

        if (!module.DoesRemember() && !module.getCredentials()[0].equals("")) {
            module.DoNotRemember();
        }
        else {

        }

        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        isNewReport = getIntent().getBooleanExtra("NewReport", false);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if(CurrentUser.getEmail().equals("s16131@nhs.co.il"))
        {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.admin_bottom_navigation_menu);
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_account) {
                Intent intent = new Intent(FragmentMap.this, UserActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.menu_camera) {
                Intent intent = new Intent(FragmentMap.this, CameraActivity.class);
                startActivity(intent);
            }
            else if(item.getItemId() == R.id.menu_admin)
            {
                Intent intent = new Intent(FragmentMap.this, AdminActivity.class);
                startActivity(intent);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_map);


        FirebaseFirestore FireStore = FirebaseFirestore.getInstance();
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if(document.getData().get("name").toString().equals(CurrentUser.getName()))
                    {
                        module.UpdateReports(CurrentUser.getId(), Integer.valueOf(document.getData().get("reports").toString()));
                    }
                }
            }
        });



    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        getLastLocation();
        if (isNewReport) {
            reportImage = (Bitmap) getIntent().getParcelableExtra("Picture");
            addReport();
        }
        module.retrieveDocs(2, new FirebaseHelper.DocsRetrievedListener() {
            @Override
            public void onDocsRetrieved(Task<QuerySnapshot> task) {
                LinkedList<String> documentsIds = new LinkedList<>();
                for(QueryDocumentSnapshot document : task.getResult())
                {
                    documentsIds.add(document.getId());
                }
                module.CreateCustomMarkers(documentsIds, mMap);
            }
        });


    }

    public void getLastLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    }
                });
    }

    public void addReport()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        module.AddReport(latLng, getIntent().getStringExtra("Description"), reportImage, mMap);

                    }
                });
    }

}