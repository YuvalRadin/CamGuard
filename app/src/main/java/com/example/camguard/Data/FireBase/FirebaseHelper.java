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
    // Firebase Firestore instance
    FirebaseFirestore FireStore = FirebaseFirestore.getInstance();
    // Firebase Storage instance
    FirebaseStorage FireStorage = FirebaseStorage.getInstance();
    // Context reference
    Context context;
    // Database helper instance
    MyDatabaseHelper myDatabaseHelper;

    /**
     * Constructor for FirebaseHelper.
     * @param context The context.
     */
    public FirebaseHelper(Context context) {
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(context);
    }

    /**
     * Listener interface for document retrieval.
     */
    public interface DocsRetrievedListener {
        void onDocsRetrieved(Task<QuerySnapshot> task);
    }

    /**
     * Retrieves documents from Firestore based on the specified collection.
     * @param which Indicates which collection to retrieve documents from (1 for 'users', 2 for 'markers').
     * @param callback The callback to be invoked upon retrieval.
     */
    public void retrieveDocs(int which, DocsRetrievedListener callback) {
        if(which == 1) {
            FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        callback.onDocsRetrieved(task);
                    } else {
                        callback.onDocsRetrieved(null);
                    }
                }
            });
        } else if(which == 2) {
            FireStore.collection("markers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        callback.onDocsRetrieved(task);
                    } else {
                        callback.onDocsRetrieved(null);
                    }
                }
            });
        } else {
            callback.onDocsRetrieved(null);
        }
    }

    /**
     * Adds a report to Firestore with the provided details.
     * @param latLng The latitude and longitude of the report.
     * @param Description The description of the report.
     * @param reportImage The image associated with the report.
     * @param mMap The GoogleMap instance.
     */
    public void addReport(LatLng latLng, String Description, Bitmap reportImage, GoogleMap mMap) {
        Map<String, Object> marker = new HashMap<String, Object>();
        String picPath = uploadPicture(reportImage);
        if(picPath == null || picPath.isEmpty()) {
            Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
            return;
        }
        marker.put("Latitude", latLng.latitude);
        marker.put("Longitude", latLng.longitude);
        marker.put("Description", Description);
        marker.put("PictureKey", picPath);
        marker.put("Reporter", CurrentUser.getName());

        // Add the marker document to Firestore
        FireStore.collection("markers").add(marker).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // Update user's report count
                FireStore.collection("users").document(CurrentUser.getFireId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        int updatedReports = Integer.parseInt(document.getData().get("reports").toString()) + 1;
                        FireStore.collection("users").document(document.getId()).update("reports", updatedReports);
                    }
                });

                // Retrieve marker documents
                retrieveDocs(2, new DocsRetrievedListener() {
                    @Override
                    public void onDocsRetrieved(Task<QuerySnapshot> task) {
                        LinkedList<String> documentsIds = new LinkedList<>();
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            documentsIds.add(document.getId());
                        }
                        createCustomMarkers(documentsIds, mMap);
                    }
                });

                // Toast success message
                Toast.makeText(context, "Report Added Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast failure message
                Log.w(TAG, "Error adding document", e);
                Toast.makeText(context, "Failed to Add Report", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Uploads a picture to Firebase Storage.
     *
     * This method takes a Bitmap image, compresses it into JPEG format, and uploads it to Firebase Storage.
     * A unique identifier is generated for each image to ensure that each upload has a unique name.
     * The method shows a ProgressDialog during the upload process and updates the user on the progress.
     * Upon successful upload, a toast message is displayed to the user. If the upload fails, an error toast message is shown.
     * The method returns the storage path of the uploaded image.
     *
     * @param reportImage The Bitmap image to be uploaded.
     * @return The storage path of the uploaded image.
     */
    public String uploadPicture(Bitmap reportImage) {
        // Initialize the progress dialog
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setTitle("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        // Generate a unique key for the image
        final String randomKey = UUID.randomUUID().toString();

        // Get a reference to the Firebase Storage location
        StorageReference markersImagesRef = FireStorage.getReference().child("images/" + randomKey);

        // Convert the Bitmap image to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        reportImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload the image data to Firebase Storage
        markersImagesRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Display a success message and dismiss the progress dialog
                        Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Display an error message and dismiss the progress dialog
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        // Update the progress dialog with the upload percentage
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " + (int) progressPercent + "%");
                    }
                });

        // Return the storage path of the uploaded image
        return markersImagesRef.getPath();
    }


    /**
     * Initiates the creation of custom markers on the provided GoogleMap instance.
     *
     * This method takes a list of document IDs from Firebase Firestore and starts the process of creating
     * custom markers on a GoogleMap instance by calling the processMarkersRecursive method.
     *
     * @param documentsIds A list of document IDs to fetch marker data from Firestore.
     * @param mMap The GoogleMap instance where the markers will be added.
     */
    public void createCustomMarkers(List<String> documentsIds, GoogleMap mMap) {
        processMarkersRecursive(documentsIds, 0, mMap);
    }

    /**
     * Recursively processes and adds custom markers to the GoogleMap instance.
     *
     * This method fetches marker data from Firestore using the provided document ID list.
     * For each document, it retrieves the latitude, longitude, description, reporter, and picture path.
     * It then sets up custom markers on the map with the fetched data and handles image loading for the marker info window.
     *
     * @param documentIds A list of document IDs to fetch marker data from Firestore.
     * @param index The current index in the document ID list being processed.
     * @param mMap The GoogleMap instance where the markers will be added.
     */
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
                            // Set up a custom info window adapter
                            CustomInfoWindowAdapter customInfoWindowAdapter = new CustomInfoWindowAdapter(context, uri);
                            mMap.setInfoWindowAdapter(customInfoWindowAdapter);

                            // Add a marker to the map
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                    .title(description)
                                    .position(latLng)
                                    .snippet(uri.getLastPathSegment().toString().substring(7) + " Reporter: " + reporter));
                            marker.setTag(uri.toString());

                            // Preload the marker's image using Glide
                            Glide.with(context)
                                    .load(marker.getTag().toString())
                                    .preload();

                            // Set up a click listener for the marker
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

                            // Process the next marker recursively
                            processMarkersRecursive(documentIds, index + 1, mMap);
                        }
                    });
                }
            });
        }
    }



    /**
     * Interface for handling search completion events.
     *
     * This interface defines a callback method to be invoked when a user search is complete.
     */
    public interface SearchComplete {
        /**
         * Called when the user search is complete.
         *
         * @param user The user's name.
         * @param email The user's email.
         * @param password The user's password.
         * @param doesExist True if the user exists, false otherwise.
         */
        void onSearchComplete(String user, String email, String password, boolean doesExist);
    }

    /**
     * Checks if a user exists in the database with the provided username and password.
     *
     * This method retrieves documents from the database and checks if a user exists
     * with the given username and password. If a matching user is found, the callback
     * is invoked with the user's details.
     *
     * @param user The username to search for.
     * @param password The password to search for.
     * @param callback The callback to be invoked upon search completion.
     */
    public void doesUserExist(String user, String password, SearchComplete callback) {
        if (user != null && password != null) {
            retrieveDocs(1, new DocsRetrievedListener() {
                @Override
                public void onDocsRetrieved(Task<QuerySnapshot> task) {
                    if (task != null) {
                        String existingPassword = null, existingEmail = null, existingName = null;
                        boolean isFound = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (user.contains("@")) {
                                if (document.getData().get("email").toString().equals(user) &&
                                        document.getData().get("password").toString().equals(password)) {
                                    existingPassword = document.getData().get("password").toString();
                                    existingName = document.getData().get("name").toString();
                                    existingEmail = document.getData().get("email").toString();
                                    isFound = true;
                                }
                            } else if (document.getData().get("name").toString().equals(user) &&
                                    document.getData().get("password").toString().equals(password)) {
                                existingPassword = document.getData().get("password").toString();
                                existingName = document.getData().get("name").toString();
                                existingEmail = document.getData().get("email").toString();
                                isFound = true;
                            }
                        }
                        callback.onSearchComplete(existingName, existingEmail, existingPassword, isFound);
                    }
                }
            });
        }
    }

    /**
     * Interface for handling credentials check completion events.
     *
     * This interface defines a callback method to be invoked when a credentials check is complete.
     */
    public interface CredentialsCheck {
        /**
         * Called when the credentials check is complete.
         *
         * @param doesUserExist True if the user exists, false otherwise.
         * @param doesEmailExist True if the email exists, false otherwise.
         */
        void onCredentialsCheckComplete(boolean doesUserExist, boolean doesEmailExist);
    }

    /**
     * Checks if a username and email exist in the database.
     *
     * This method retrieves documents from the database and checks if the given username and email exist.
     * If matches are found, the callback is invoked with the results.
     *
     * @param user The username to check for.
     * @param email The email to check for.
     * @param callback The callback to be invoked upon credentials check completion.
     */
    public void doesUserAndEmailExist(String user, String email, CredentialsCheck callback) {
        if (user != null && email != null) {
            retrieveDocs(1, new DocsRetrievedListener() {
                @Override
                public void onDocsRetrieved(Task<QuerySnapshot> task) {
                    if (task != null) {
                        boolean isUser = false, isEmail = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getData().get("email").toString().equals(email)) {
                                isEmail = true;
                            }
                            if (document.getData().get("name").toString().equals(user)) {
                                isUser = true;
                            }
                        }
                        callback.onCredentialsCheckComplete(isUser, isEmail);
                    }
                }
            });
        }
    }


    /**
     * Checks if a username and email exist in the Firestore database, excluding the current user.
     *
     * This method retrieves all documents from the "users" collection and checks if the given username
     * and email exist, excluding the current user's username and email from the check.
     * The callback is invoked with the results of the check.
     *
     * @param user The username to check for existence.
     * @param email The email to check for existence.
     * @param callback The callback to invoke upon completion of the check.
     */
    public void checkUserAndEmailExistence(String user, String email, CredentialsCheck callback) {
        FireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean usernameExists = false;
                boolean emailExists = false;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getData().get("name").toString().equals(user) &&
                            !document.getData().get("name").toString().equals(CurrentUser.getName())) {
                        usernameExists = true;
                    }
                    if (document.getData().get("email").toString().equals(email) &&
                            !document.getData().get("email").toString().equals(CurrentUser.getEmail())) {
                        emailExists = true;
                    }
                }
                callback.onCredentialsCheckComplete(usernameExists, emailExists);
            }
        });
    }

    /**
     * Deletes all users from the Firestore database.
     *
     * This method retrieves all documents from the "users" collection and deletes each document.
     */
    public void deleteAllFireStoreUsers() {
        retrieveDocs(1, new DocsRetrievedListener() {
            @Override
            public void onDocsRetrieved(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        FireStore.collection("users").document(document.getId()).delete();
                    }
                }
            }
        });
    }

    /**
     * Deletes a specific user from the Firestore database.
     *
     * This method retrieves all documents from the "users" collection and deletes the document
     * with the specified username.
     *
     * @param user The username of the user to be deleted.
     */
    public void deleteFireStoreUser(String user) {
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

    /**
     * Updates a specific user's details in the Firestore database.
     *
     * This method retrieves all documents from the "users" collection and updates the document
     * with the specified username with the new details provided.
     *
     * @param user The username of the user to be updated.
     * @param upUser The new username.
     * @param upEmail The new email.
     * @param upPass The new password.
     */
    public void updateFireStoreUser(String user, String upUser, String upEmail, String upPass) {
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("name", upUser);
        newUser.put("email", upEmail);
        newUser.put("password", upPass);

        retrieveDocs(1, new DocsRetrievedListener() {
            @Override
            public void onDocsRetrieved(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getData().get("name").toString().equals(user)) {
                            FireStore.collection("users").document(document.getId()).update(newUser);
                        }
                    }
                }
            }
        });
    }


    /**
     * Adds a new user to the Firestore database.
     *
     * This method creates a new user document with the specified username, email, and password,
     * and initializes the report count to 0. The document is added to the "users" collection.
     *
     * @param user The username of the new user.
     * @param email The email of the new user.
     * @param password The password of the new user.
     */
    public void addUserToFireBase(String user, String email, String password) {
        Map<String, Object> User = new HashMap<String, Object>();
        User.put("name", user);
        User.put("email", email);
        User.put("password", password);
        User.put("reports", 0);

        // Create document and add marker
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

    /**
     * Deletes a marker from the Firestore database by its ID.
     *
     * This method searches for a marker document in the "markers" collection with a PictureKey
     * matching the specified marker ID. If found, the associated image file is deleted from
     * Firebase Storage, and then the marker document is deleted from Firestore.
     *
     * @param marker The ID of the marker to be deleted.
     */
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
                                    String picPath = documentSnapshot.getData().get("PictureKey").toString();
                                    StorageReference ref = null;
                                    if (picPath != null && !picPath.equals("")) {
                                        ref = FireStorage.getReference().child(picPath);
                                    }

                                    // Delete the file
                                    if (ref != null) {
                                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                    Toast.makeText(context, "Failed to retrieve picture", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    if (!didFind) {
                        Toast.makeText(context, "Report does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Report does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Deletes a marker from the Firestore database by its description.
     *
     * This method searches for a marker document in the "markers" collection with a Description
     * matching the specified marker description. If found, the associated image file is deleted from
     * Firebase Storage, and then the marker document is deleted from Firestore.
     *
     * @param marker The description of the marker to be deleted.
     */
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
                                    String picPath = documentSnapshot.getData().get("PictureKey").toString();
                                    StorageReference ref = null;
                                    if (picPath != null && !picPath.equals("")) {
                                        ref = FireStorage.getReference().child(picPath);
                                    }

                                    // Delete the file
                                    if (ref != null) {
                                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                    Toast.makeText(context, "Failed to retrieve picture", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    if (!didFind) {
                        Toast.makeText(context, "Report does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Report does not exist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Deletes all markers from the Firestore database.
     *
     * This method retrieves all documents in the "markers" collection and deletes each one along with
     * its associated image file in Firebase Storage. A toast message is shown indicating that all markers
     * have been deleted.
     */
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

    /**
     * Interface for handling the result of retrieving markers.
     */
    public interface markersGotten {
        /**
         * Called when markers are retrieved.
         *
         * @param task   The task containing the query snapshot of the markers.
         * @param photos The list of URIs of the photos associated with the markers.
         */
        void onMarkersGotten(Task<QuerySnapshot> task, LinkedList<Uri> photos);
    }

    /**
     * Retrieves the current user's markers and associated photos.
     *
     * This method first retrieves the user's photos using the getMyPhotos method,
     * then retrieves the marker documents and invokes the callback with the results.
     *
     * @param callback The callback to handle the retrieved markers and photos.
     */
    public void getMyMarkers(markersGotten callback) {
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

    /**
     * Interface for handling the result of retrieving photos.
     */
    interface MyPhotos {
        /**
         * Called when photos are retrieved.
         *
         * @param photos The list of URIs of the retrieved photos.
         */
        void onMyPhotosHere(LinkedList<Uri> photos);
    }

    /**
     * Retrieves the current user's photos.
     *
     * This method retrieves the user's photo documents and invokes the callback with the results.
     *
     * @param callback The callback to handle the retrieved photos.
     */
    public void getMyPhotos(MyPhotos callback) {
        LinkedList<Uri> photos = new LinkedList<>();
        retrieveDocs(2, new DocsRetrievedListener() {
            @Override
            public void onDocsRetrieved(Task<QuerySnapshot> task) {
                getMyPhotosRecursive(photos, 0, task, callback);
            }
        });
    }

    /**
     * Recursively retrieves the URLs of the photos associated with the markers.
     *
     * This method retrieves the download URLs of the photos from Firebase Storage
     * and adds them to the provided list. It invokes the callback when all photos are retrieved.
     *
     * @param photos   The list to store the photo URIs.
     * @param index    The current index in the task's result documents.
     * @param task     The task containing the query snapshot of the markers.
     * @param callback The callback to handle the retrieved photos.
     */
    public void getMyPhotosRecursive(LinkedList<Uri> photos, int index, Task<QuerySnapshot> task, MyPhotos callback) {
        if (index < task.getResult().size()) {
            StorageReference reportImageRef = FireStorage.getReferenceFromUrl("gs://camguard-1d482.appspot.com/" + task.getResult().getDocuments().get(index).getData().get("PictureKey"));
            reportImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    photos.add(uri);
                    if (photos.size() == task.getResult().size()) {
                        callback.onMyPhotosHere(photos);
                    } else {
                        getMyPhotosRecursive(photos, index + 1, task, callback);
                    }
                }
            });
        }
    }



}
