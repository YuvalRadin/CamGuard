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
import android.view.LayoutInflater;
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
import com.example.camguard.Data.CurrentUser;
import com.example.camguard.Data.CustomMarkerAdapter.CustomInfoWindowAdapter;
import com.example.camguard.R;
import com.example.camguard.UI.Admin.AdminActivity;
import com.example.camguard.UI.Camera.CameraActivity;
import com.example.camguard.UI.Login.MainActivity;
import com.example.camguard.UI.User.UserActivity;
import com.google.android.gms.common.images.ImageManager;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class FragmentMap extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {


    BottomNavigationView bottomNavigationView;
    static GoogleMap mMap;
    Context context;
    LatLng latLng;
    Bitmap reportImage;
    moduleMap module;
    boolean isNewReport;
    static boolean reloadMap = true;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;



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

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();




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



    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        getLastLocation();
        if (isNewReport) {
            reportImage = (Bitmap) getIntent().getParcelableExtra("Picture");
            addReport();
        }
        getAllDocumentIds(new DocumentIdCallback() {
            @Override
            public void onDocumentIdListLoaded(List<String> documentIds) {
                // Process the list of document IDs here
                CreateCustomMarkers(documentIds);
            }
        });

    }

    public void CreateCustomMarkers(List<String> documentIds) {
        processMarkersRecursive(documentIds, 0);
    }

    public void processMarkersRecursive(List<String> documentIds, int index) {
        if (index < documentIds.size()) {
            String documentID = documentIds.get(index);

            db.collection("markers").document(documentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    LatLng latLng = new LatLng((double) document.getData().get("Latitude"), (double) document.getData().get("Longitude"));
                    String description = (String) document.getData().get("Description");
                    String reporter = (String) document.getData().get("Reporter");
                    String picPath = (String) document.getData().get("PictureKey");
                    StorageReference reportImageRef = storage.getReferenceFromUrl("gs://camguard-1d482.appspot.com/" + picPath);

                    reportImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(getBaseContext(), uri);
                            mMap.setInfoWindowAdapter(customInfoWindowAdapter);
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .title(description)
                                    .position(latLng)
                                    .snippet(uri.getLastPathSegment().toString().substring(7) + " Reporter: " + reporter));

                            marker.setTag(uri.toString());

                            Glide.with(context)
                                    .load(marker.getTag().toString())
                                    .preload();

                                // Set up the marker click listener
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker clickedMarker) {
                                    // Load the image when the marker is clicked
                                    Glide.with(context)
                                            .load(clickedMarker.getTag().toString())
                                            .placeholder(context.getDrawable(R.drawable.ic_camera))
                                            .centerCrop()
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    Log.e("ImageLoading", "Image load failed: " + e.getMessage());
                                                    // Handle the failure, if needed
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    Log.d("ImageLoading", "Image load successful " + clickedMarker.getTag().toString());
                                                    clickedMarker.showInfoWindow(); // Show the info window when the image is loaded
                                                    return false;
                                                }
                                            })
                                            .into(customInfoWindowAdapter.getImageView());

                                    // Return true to consume the marker click event
                                    return true;
                                }
                            });

                            // Process the next marker
                            processMarkersRecursive(documentIds, index + 1);
                        }
                    });
                }
            });
        }
    }

    public interface DocumentIdCallback {
        void onDocumentIdListLoaded(List<String> documentIds);
    }

    private void getAllDocumentIds(DocumentIdCallback callback) {
        db.collection("markers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> documentIds = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        documentIds.add(document.getId());
                    }
                    callback.onDocumentIdListLoaded(documentIds);
                }
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
        Map<String, Object> marker = new HashMap<>();

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
                        String picPath = uploadPicture();
                        marker.put("Latitude", latLng.latitude);
                        marker.put("Longitude",latLng.longitude);
                        marker.put("Description", getIntent().getStringExtra("Description"));
                        marker.put("PictureKey", picPath);
                        marker.put("Reporter", CurrentUser.getName());
//                        create document and add marker
                        db.collection("markers")
                                .add(marker)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Toast.makeText(context, "Report Added Successfully", Toast.LENGTH_SHORT).show();
                                        module.AddReport(module.getIdByName(CurrentUser.getName()));
                                        getAllDocumentIds(new DocumentIdCallback() {
                                            @Override
                                            public void onDocumentIdListLoaded(List<String> documentIds) {
                                                // Process the list of document IDs here
                                                CreateCustomMarkers(documentIds);
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        Toast.makeText(context, "Failed to Add Report", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

    }


    private String uploadPicture()
    {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        // Create a reference to "mountains.jpg"

        final String randomKey = UUID.randomUUID().toString();
        // Create a reference to 'images/mountains.jpg'
        StorageReference markersImagesRef = storageReference.child("images/" + randomKey);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        reportImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        markersImagesRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to Upload image", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " + (int) progressPercent + "%");
                    }
                });

        return markersImagesRef.getPath();
    }




    @Override
    public void onClick(View view) {




    }


}