package com.example.camguard.Data.Repository;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.camguard.Data.DB.MyDatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    public boolean LoginUser(String user, String password, int EmailLogin) { return myDatabaseHelper.LoginUser(user, password, EmailLogin); }

    public void addUser(String Username, String Email, String Password) { myDatabaseHelper.addUser(Username, Email, Password);}

    public void deleteAllData() { myDatabaseHelper.deleteAllData(); }

    public Cursor getUserByName(String user){ return myDatabaseHelper.getUserByName(user);}

    public int getReportsByID(String ID) { return myDatabaseHelper.getReportsByID(ID);}

    public String getIdByName(String user) { return myDatabaseHelper.getIdByName(user);}

    public String getNameByEmail(String email) { return myDatabaseHelper.getNameByEmail(email);}

    public void AddReport(String id) { myDatabaseHelper.AddReport(id);}
    public void deleteOneRow(String row_id){ myDatabaseHelper.deleteOneRow(row_id);}

    public void DeleteMarker(String marker) {
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



}
