package com.example.camguard.UI.GoogleMaps;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.example.camguard.Data.CustomMarkerAdapter.CustomInfoWindowAdapter;
import com.example.camguard.R;
import com.example.camguard.UI.Camera.CameraActivity;
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
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;

    static String credentials[];



    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);
        context = getBaseContext();

        FragmentManager fragmentManager = getSupportFragmentManager();

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        credentials = new String[2];




        module = new moduleMap(this);
        Intent intent = new Intent(FragmentMap.this, UserActivity.class);
        Intent intent2 = new Intent(FragmentMap.this, CameraActivity.class);
        if (!module.DoesRemember() && !module.getCredentials()[0].equals("")) {
            intent.putExtra("username", module.getCredentials()[0]);
            intent.putExtra("email", module.getCredentials()[1]);
            intent2.putExtra("username", module.getCredentials()[0]);
            intent2.putExtra("email", module.getCredentials()[1]);
            credentials[0] = module.getCredentials()[0];
            credentials[1] = module.getCredentials()[1];
            module.DoNotRemember();
        }
        else {
            credentials[0] = module.getCredentials()[0];
            credentials[1] = module.getCredentials()[1];
        }

        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.fragmentContainerView);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_account) {
                startActivity(intent);
            } else if (item.getItemId() == R.id.menu_camera) {
                startActivity(intent2);
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.menu_map);

        isNewReport = getIntent().getBooleanExtra("NewReport", false);





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
                                    .position(latLng));

                            marker.setTag(uri.toString());

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

//                        create document and add marker
                        db.collection("markers")
                                .add(marker)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Toast.makeText(context, "Report Added Successfully", Toast.LENGTH_SHORT).show();
                                        module.AddReport(module.getIdByName(credentials[0]));
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