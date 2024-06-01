package com.example.camguard.UI.GoogleMaps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
    private static boolean reloadMap = true;

    // Location provider client
    private FusedLocationProviderClient fusedLocationClient;

    // Activity result launcher for location permission request
    private ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                    .RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                if (fineLocationGranted != null && fineLocationGranted && coarseLocationGranted != null && coarseLocationGranted) {
                    // Location access granted
                    if(reloadMap && module.CredentialsExist()) {
                        Intent intent = new Intent(FragmentMap.this, MainActivity.class);
                        startActivity(intent);
                        reloadMap = false;
                    }
                } else {
                    // No location access granted
                    Intent intent = new Intent(FragmentMap.this, MainActivity.class);
                    startActivity(intent);
                }
            });

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
        locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check if GPS is enabled
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            startActivity(new Intent(FragmentMap.this, EnableGPSActivity.class));
        }

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

}