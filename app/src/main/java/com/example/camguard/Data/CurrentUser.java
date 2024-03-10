package com.example.camguard.Data;

import android.content.SharedPreferences;

public class CurrentUser {

    static String Name;
    static String Email;
    static String Id;

    public CurrentUser()
    {

    }

    public static void InitializeUser(String name, String email, String id)
    {
        Name = name;
        Email = email;
        Id = id;
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
}
