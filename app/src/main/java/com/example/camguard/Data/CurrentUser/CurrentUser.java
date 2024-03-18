package com.example.camguard.Data.CurrentUser;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CurrentUser {

    static String Name;
    static String Email;
    static String Id;
    static String FireId;

    public CurrentUser()
    {

    }

    public static void InitializeUser(String name, String email, String id)
    {
        Name = name;
        Email = email;
        Id = id;
        findUserFireId(new userFound() {
            @Override
            public void onUserFound(String FireId) {
                CurrentUser.FireId = FireId;
            }
        });
    }

    public interface userFound
    {
        void onUserFound(String FireId);
    }

    public static void findUserFireId(userFound callback)
    {
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult())
                {
                    if(document.getData().get("name").toString().equals(CurrentUser.getName()))
                    {
                        callback.onUserFound(document.getId());
                    }
                }
            }
        });
    }

    public static String getName() {
        return Name;
    }

    public static void setName(String name) {
        Name = name;
    }

    public static String getEmail() {
        return Email;
    }

    public static void setEmail(String email) {
        Email = email;
    }

    public static String getId() {
        return Id;
    }

    public static void setId(String id) {
        CurrentUser.Id = id;
    }

    public static String getFireId() {
        return FireId;
    }

    public static void setFireId(String FireId) {
        CurrentUser.FireId = FireId;
    }
}
