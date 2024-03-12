package com.example.camguard.Data.Repository;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
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

    FirebaseFirestore FireStore;
    FirebaseStorage FireStorage;

    public Repository(Context context)
    {
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(this.context);
        FireStore = FirebaseFirestore.getInstance();
        FireStorage = FirebaseStorage.getInstance();
    }

    public void UpdateUser(String id, String name, String pass, String email) { myDatabaseHelper.updateData(id, name, pass, email);}
    public boolean FindUser(String user) { return myDatabaseHelper.FindUser(user);}
    public boolean FindEmail(String email) { return myDatabaseHelper.FindEmail(email);}
    public boolean UserExistsNotLocal(String user, String email) { return myDatabaseHelper.UserExistsNotLocal(user, email);}

    public boolean LoginUser(String user, String password, int EmailLogin) { return myDatabaseHelper.LoginUser(user, password, EmailLogin); }

    public void addUser(String Username, String Password, String Email) { myDatabaseHelper.addUser(Username, Password, Email);}

    public void deleteAllData() { myDatabaseHelper.deleteAllData(); }

    public Cursor getUserByName(String user){ return myDatabaseHelper.getUserByName(user);}

    public int getReportsByID(String ID) { return myDatabaseHelper.getReportsByID(ID);}

    public String getIdByName(String user) { return myDatabaseHelper.getIdByName(user);}

    public void UpdateReports(String id, int reports) { myDatabaseHelper.UpdateReports(id,reports);}

    public void AddReportToUser(String id) {
        myDatabaseHelper.AddReport(id);
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getData().get("name").toString().equals(CurrentUser.getName())) {
                            int updatedReports = Integer.parseInt(document.getData().get("reports").toString()) + 1;
                            FireStore.collection("users").document(document.getId()).update("reports", updatedReports);
                        }
                    }
                }
            }
        });
    }
    public void deleteOneRow(String row_id){ myDatabaseHelper.deleteOneRow(row_id);}
    public void DeleteAllFireStoreData()
    {
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FireStore.collection("users").document(document.getId()).delete();
                    }
                }
            }
        });

    }
    public void DeleteFireStoreUser(String user)
    {
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getData().get("name").toString().equals(user)) {
                            FireStore.collection("users").document(document.getId()).delete();
                        }
                    }
                }
            }
        });

    }
    public void UpdateFireStoreUser(String user, String upUser, String upEmail, String upPass)
    {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", upUser);
        newUser.put("email", upEmail);
        newUser.put("password", upPass);
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getData().get("name").toString().equals(user)) {
                            FireStore.collection("users").document(document.getId()).update(newUser);
                        }
                    }
                }
            }
        });

    }


    public void AddUserToFireBase(String user, String email, String password) {
        Map<String, Object> User = new HashMap<String, Object>();
        User.put("name", user);
        User.put("email",email);
        User.put("password", password);
        User.put("reports", 0);
//                        create document and add marker
        FireStore.collection("users")
                .add(User)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void DeleteMarkerByID(String marker) {
        FireStore.collection("markers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean didFind = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().get("PictureKey").toString().equals("/images/" + marker)) {
                            
                            didFind = true;
                            final String documentID = document.getId();
                            FireStore.collection("markers").document(documentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String PicPath = documentSnapshot.getData().get("PictureKey").toString();
                                    StorageReference Ref = null;
                                    if (PicPath != null && !PicPath.equals("")) {
                                        Ref = FireStorage.getReference().child(PicPath);
                                    }

                                    // Delete the file
                                    if (Ref != null) {

                                        Ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                FireStore.collection("markers").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "failed to retrieve picture", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    if(!didFind)
                    {
                        Toast.makeText(context, "Report does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "report does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void DeleteMarkerByDesc(String marker) {
        FireStore.collection("markers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    boolean didFind = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().get("Description").toString().equals(marker)) {
                            didFind = true;
                            final String documentID = document.getId();
                            FireStore.collection("markers").document(documentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String PicPath = documentSnapshot.getData().get("PictureKey").toString();
                                    StorageReference Ref = null;
                                    if (PicPath != null && !PicPath.equals("")) {
                                        Ref = FireStorage.getReference().child(PicPath);
                                    }

                                    // Delete the file
                                    if (Ref != null) {

                                        Ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                FireStore.collection("markers").document(documentID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "failed to retrieve picture", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    if(!didFind)
                    {
                        Toast.makeText(context, "Report does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "report does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void DeleteAllMarkers() {
        FireStore.collection("markers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FireStorage.getReference().child(document.getData().get("PictureKey").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FireStore.collection("markers").document(document.getId()).delete();
                            }
                        });
                    }
                    Toast.makeText(context, "Deleted All Markers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void AddReport(LatLng latLng, String Description, Bitmap reportImage, GoogleMap mMap){
        Map<String, Object> marker = new HashMap<String, Object>();
        String picPath = uploadPicture(reportImage);
        marker.put("Latitude", latLng.latitude);
        marker.put("Longitude",latLng.longitude);
        marker.put("Description", Description);
        marker.put("PictureKey", picPath);
        marker.put("Reporter", CurrentUser.getName());
//                        create document and add marker
        FireStore.collection("markers")
                .add(marker)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(context, "Report Added Successfully", Toast.LENGTH_SHORT).show();
                        AddReportToUser(getIdByName(CurrentUser.getName()));
                        getAllDocumentIds(new DocumentIdCallback() {
                            @Override
                            public void onDocumentIdListLoaded(List<String> documentIds) {
                                // Process the list of document IDs here
                                CreateCustomMarkers(documentIds, mMap);
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

    public interface DocumentIdCallback {
        void onDocumentIdListLoaded(List<String> documentIds);
    }

    public void getAllDocumentIds(DocumentIdCallback callback) {
        FireStore.collection("markers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

    public String uploadPicture(Bitmap reportImage)
    {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        // Create a reference to "mountains.jpg"

        final String randomKey = UUID.randomUUID().toString();
        // Create a reference to 'images/mountains.jpg'

        StorageReference markersImagesRef = FireStorage.getReference().child("images/" + randomKey);

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

    public void CreateCustomMarkers(List<String> documentIds, GoogleMap mMap) {
        processMarkersRecursive(documentIds, 0, mMap);
    }

    public void processMarkersRecursive(List<String> documentIds, int index, GoogleMap mMap) {
        if (index < documentIds.size()) {
            String documentID = documentIds.get(index);

            FireStore.collection("markers").document(documentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    LatLng latLng = new LatLng((double) document.getData().get("Latitude"), (double) document.getData().get("Longitude"));
                    String description = (String) document.getData().get("Description");
                    String reporter = (String) document.getData().get("Reporter");
                    String picPath = (String) document.getData().get("PictureKey");
                    StorageReference reportImageRef = FireStorage.getReferenceFromUrl("gs://camguard-1d482.appspot.com/" + picPath);

                    reportImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(context, uri);
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
                            processMarkersRecursive(documentIds, index + 1, mMap);
                        }
                    });
                }
            });
        }
    }

}
