package com.example.camguard.UI.GoogleMaps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.camguard.Data.CurrentUser.CurrentUser;
import com.example.camguard.Data.FireBase.FirebaseHelper;
import com.example.camguard.R;
import com.example.camguard.UI.Admin.AdminActivity;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.EnableGPS.EnableGPSActivity;
import com.example.camguard.UI.Login.MainActivity;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;


public class FragmentMap extends AppCompatActivity implements OnMapReadyCallback {


    // UI elements
    private BottomNavigationView bottomNavigationView;
    private static GoogleMap mMap;
    private Context context;

    // Location and report data
    private LatLng latLng;
    private Bitmap reportImage;
    private moduleMap module;
    private boolean isNewReport;

    // Location provider client
    private FusedLocationProviderClient fusedLocationClient;


    /**
     * Initializes the activity when created.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        context = getBaseContext();

        // Initialize UI elements
        FragmentManager fragmentManager = getSupportFragmentManager();

        checkLocationPermission();

        module = new moduleMap(this);

        // Check if user credentials exist
        if (!module.DoesRemember() && !module.getCredentials()[0].equals("")) {
            module.DoNotRemember();
        }

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        isNewReport = getIntent().getBooleanExtra("NewReport", false);

        // Configure bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if(CurrentUser.getEmail().equals("s16131@nhs.co.il")) {
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
            } else if(item.getItemId() == R.id.menu_admin) {
                Intent intent = new Intent(FragmentMap.this, AdminActivity.class);
                startActivity(intent);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_map);

        // Update user reports from Firestore
        FirebaseFirestore FireStore = FirebaseFirestore.getInstance();
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.getData().get("name").toString().equals(CurrentUser.getName())) {
                        module.UpdateReports(CurrentUser.getId(), Integer.valueOf(document.getData().get("reports").toString()));
                    }
                }
            }
        });
    }


    /**
     * Called when the map is ready to be used.
     *
     * @param googleMap The GoogleMap object representing the map.
     */
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
                for(QueryDocumentSnapshot document : task.getResult()) {
                    documentsIds.add(document.getId());
                }
                module.CreateCustomMarkers(documentsIds, mMap);
            }
        });
    }

    /**
     * Gets the last known location and animates the camera to that location.
     */
    public void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    }
                });
    }

    /**
     * Adds a report to the map at the user's current location.
     */
    public void addReport() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        module.AddReport(latLng, getIntent().getStringExtra("Description"), reportImage, mMap);
                    }
                });
    }

    AlertDialog.Builder locationAlert;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Show an explanation to the user asynchronously -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            String title = "Location Settings";
            String text = "Please enable us to use your location in order for the app to function";
            locationAlert = new AlertDialog.Builder(this);
            locationAlert
                    .setTitle(title)
                    .setMessage(text)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Prompt the user once explanation has been shown

                            startActivity(
                                    new Intent(
                                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getPackageName(), null)
                                    )
                            );
                        }
                    })
                    .create()
                    .show();

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        if(checkLocationPermission())
        {
            startActivity(new Intent(FragmentMap.this, FragmentMap.class));
        }
    }

}