package com.example.camguard.Data.CurrentUser;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CurrentUser {

    // Static variables to hold user data
    static String Name;
    static String Email;
    static String Id;
    static String FireId;

    /**
     * Initializes the user with provided data.
     * Retrieves Firebase Firestore document ID associated with the user based on the provided name.
     */
    public static void initializeUser(String name, String email, String id) {
        Name = name;
        Email = email;
        Id = id;
        // Calls method to find Firebase ID asynchronously
        findUserFireId(new userFound() {
            @Override
            public void onUserFound(String FireId) {
                // Sets the Firebase ID when found
                CurrentUser.FireId = FireId;
            }
        });
    }

    /**
     * Interface to handle asynchronous callback for user Firebase ID retrieval.
     */
    public interface userFound {
        void onUserFound(String FireId);
    }

    /**
     * Finds the Firebase Firestore document ID associated with the user.
     * Executes the callback when found.
     */
    public static void findUserFireId(userFound callback) {
        // Retrieves Firestore instance and queries 'users' collection
        FirebaseFirestore.getInstance().collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // Iterates through the documents in the result
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Checks if document's 'name' field matches current user's name
                    if (document.getData().get("name").toString().equals(CurrentUser.getName())) {
                        // Calls callback with the document ID
                        callback.onUserFound(document.getId());
                    }
                }
            }
        });
    }

    /**
     * Retrieves the name of the current user.
     * */
    public static String getName() {
        return Name;
    }

    /**
     * Sets the name of the current user.
     */
    public static void setName(String name) {
        Name = name;
    }

    /**
     * Retrieves the email of the current user.
     */
    public static String getEmail() {
        return Email;
    }

    /**
     * Sets the email of the current user.
     */
    public static void setEmail(String email) {
        Email = email;
    }

    /**
     * Retrieves the ID of the current user.
     */
    public static String getId() {
        return Id;
    }

    /**
     * Sets the ID of the current user.
     */
    public static void setId(String id) {
        Id = id;
    }

    /**
     * Retrieves the Firebase ID associated with the current user.
     */
    public static String getFireId() {
        return FireId;
    }
}
