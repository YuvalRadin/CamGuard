package com.example.camguard.Data.FireBase;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.example.camguard.Data.Repository.Repository;
import com.example.camguard.R;
import com.example.camguard.UI.User.UserActivity;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.Help;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.grpc.stub.annotations.RpcMethod;

public class FirebaseHelper {
    FirebaseFirestore FireStore = FirebaseFirestore.getInstance();
    FirebaseStorage FireStorage = FirebaseStorage.getInstance();
    Context context;
    MyDatabaseHelper myDatabaseHelper;

    public FirebaseHelper(Context context)
    {
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(context);
    }


    public interface DocsRetrievedListener
    {
        void onDocsRetrieved(Task<QuerySnapshot> task);
    }

    public void retrieveDocs(int which, DocsRetrievedListener callback)
    {
        if(which == 1)
        {
                FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            callback.onDocsRetrieved(task);
                        }
                        else callback.onDocsRetrieved(null);
                    }
                });
            }
        else if(which == 2)
        {
                FireStore.collection("markers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            callback.onDocsRetrieved(task);
                        }
                        else callback.onDocsRetrieved(null);
                    }
                });
            }
        else callback.onDocsRetrieved(null);


        }


    public void addReport(LatLng latLng, String Description, Bitmap reportImage, GoogleMap mMap){
        Map<String, Object> marker = new HashMap<String, Object>();
        String picPath = uploadPicture(reportImage);
        if(picPath == null || picPath.isEmpty())
        {
            Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
            return;
        }
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

                        FireStore.collection("users").document(CurrentUser.getFireId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                int updatedReports = Integer.parseInt(document.getData().get("reports").toString()) + 1;
                                FireStore.collection("users").document(document.getId()).update("reports", updatedReports);
                            }
                        });
                        retrieveDocs(2, new DocsRetrievedListener() {
                            @Override
                            public void onDocsRetrieved(Task<QuerySnapshot> task) {
                                LinkedList<String> documentsIds = new LinkedList<>();
                                for(QueryDocumentSnapshot document : task.getResult())
                                {
                                    documentsIds.add(document.getId());
                                }
                                createCustomMarkers(documentsIds, mMap);
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

    public String uploadPicture(Bitmap reportImage)
    {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        final String randomKey = UUID.randomUUID().toString();

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


    public void createCustomMarkers(List<String> documentsIds, GoogleMap mMap) {
        processMarkersRecursive(documentsIds, 0, mMap);
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


    public interface SearchComplete
    {
        void onSearchComplete(String user, String email, String password, boolean doesExist);
    }

    public void doesUserExist(String user, String password, SearchComplete callback) {
        if (!user.equals(null) && !password.equals(null)) {
            retrieveDocs(1, new DocsRetrievedListener() {
                @Override
                public void onDocsRetrieved(Task<QuerySnapshot> task) {
                    if(task !=null) {
                        String ExistingPassword = null, ExistingEmail = null, ExistingName = null;
                        boolean isFound = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                                if (user.contains("@")) {
                                    if (document.getData().get("email").toString().equals(user) && document.getData().get("password").toString().equals(password)) {
                                        ExistingPassword = document.getData().get("password").toString();
                                        ExistingName = document.getData().get("name").toString();
                                        ExistingEmail = document.getData().get("email").toString();
                                        isFound = true;
                                    }

                                } else if (document.getData().get("name").toString().equals(user) && document.getData().get("password").toString().equals(password)) {
                                    ExistingPassword = document.getData().get("password").toString();
                                    ExistingName = document.getData().get("name").toString();
                                    ExistingEmail = document.getData().get("email").toString();
                                    isFound = true;
                                }
                        }
                        callback.onSearchComplete(ExistingName, ExistingEmail, ExistingPassword, isFound);
                    }
                }
            });
        }
    }

    public interface CredentialsCheck
    {
        void onCredentialsCheckComplete(boolean doesUserExist, boolean doesEmailExist);
    }
    public void doesUserAndEmailExist(String user, String email, CredentialsCheck callback) {
        if (!user.equals(null) && !email.equals(null)) {
            retrieveDocs(1, new DocsRetrievedListener() {
                @Override
                public void onDocsRetrieved(Task<QuerySnapshot> task) {
                    if(task !=null) {
                        boolean isUser = false, isEmail = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getData().get("email").toString().equals(email)) {
                                    isEmail = true;
                            }
                             if (document.getData().get("name").toString().equals(user)) {
                                    isUser = true;
                            }
                        }
                        callback.onCredentialsCheckComplete(isUser,isEmail);
                    }
                }
            });
        }
    }
    public void checkUserAndEmailExistence(String user, String email , CredentialsCheck callback) {
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean usernameExists = false;
                boolean emailExists = false;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getData().get("name").toString().equals(user) && !document.getData().get("name").toString().equals(CurrentUser.getName())) {

                        usernameExists = true;
                    }
                    if (document.getData().get("email").toString().equals(email) && !document.getData().get("email").toString().equals(CurrentUser.getEmail())) {
                        emailExists = true;
                    }
                }
                callback.onCredentialsCheckComplete(usernameExists, emailExists);
            }
        });
    }


    public void deleteAllFireStoreUsers()
    {
        retrieveDocs(1, new DocsRetrievedListener() {
            @Override
            public void onDocsRetrieved(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FireStore.collection("users").document(document.getId()).delete();}
                }
            }
        });
    }
    public void deleteFireStoreUser(String user)
    {
        retrieveDocs(1, new DocsRetrievedListener() {
            @Override
            public void onDocsRetrieved(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().get("name").toString().equals(user)) {
                            FireStore.collection("users").document(document.getId()).delete();
                        }
                    }
                }
            }
        });
    }
    public void updateFireStoreUser(String user, String upUser, String upEmail, String upPass)
    {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", upUser);
        newUser.put("email", upEmail);
        newUser.put("password", upPass);
        retrieveDocs(1, new DocsRetrievedListener() {
            @Override
            public void onDocsRetrieved(Task<QuerySnapshot> task) {
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
    public void addUserToFireBase(String user, String email, String password) {
        Map<String, Object> User = new HashMap<String, Object>();
        User.put("name", user);
        User.put("email",email);
        User.put("password", password);
        User.put("reports", 0);

//      create document and add marker
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
    public void deleteMarkerByID(String marker) {
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
    public void deleteMarkerByDesc(String marker) {
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
    public void deleteAllMarkers() {
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

    public interface markersGotten
    {
        void onMarkersGotten(Task<QuerySnapshot> task, LinkedList<Uri> photos);
    }

    public void getMyMarkers(markersGotten callback)
    {
        getMyPhotos(new MyPhotos() {
            @Override
            public void onMyPhotosHere(LinkedList<Uri> photos) {
                retrieveDocs(2, new DocsRetrievedListener() {
                    @Override
                    public void onDocsRetrieved(Task<QuerySnapshot> task) {
                        callback.onMarkersGotten(task, photos);
                    }
                });
            }
        });

    }

    interface MyPhotos
    {
      void onMyPhotosHere(LinkedList<Uri> photos);
    }

    public void getMyPhotos(MyPhotos callback)
    {
        LinkedList<Uri> photos = new LinkedList<>();
            retrieveDocs(2, new DocsRetrievedListener() {
                @Override
                public void onDocsRetrieved(Task<QuerySnapshot> task) {
                    getMyPhotosRecursive(photos, 0, task, callback);
                }
            });

    }

    public void getMyPhotosRecursive(LinkedList<Uri> photos, int index, Task<QuerySnapshot> task, MyPhotos callback) {
        if(index < task.getResult().size())
        {
        StorageReference reportImageRef = FireStorage.getReferenceFromUrl("gs://camguard-1d482.appspot.com/" + task.getResult().getDocuments().get(index).getData().get("PictureKey"));
        reportImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                photos.add(uri);
                if(photos.size() == task.getResult().size()){
                    callback.onMyPhotosHere(photos);
                }
                else getMyPhotosRecursive(photos, index + 1, task, callback);
            }
        });
        }
    }



}
