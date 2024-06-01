package com.example.camguard.Data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TableLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Context reference for accessing resources and system services
    private final Context context;

    // Database constants
    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 2;

    // Table and column names
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USERNAME = "column_username";
    private static final String COLUMN_PASSWORD = "column_password";
    private static final String COLUMN_EMAIL = "column_email";
    private static final String COLUMN_REPORTS = "column_reports";

    /**
     * Constructor for MyDatabaseHelper.
     * @param context The context of the calling activity or fragment.
     */
    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Called when the database is created for the first time.
     * @param db The database instance.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL query to create the users table
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_REPORTS + " INTEGER )";
        // Execute the SQL query
        db.execSQL(query);
    }

    /**
     * Called when the database needs to be upgraded.
     * @param db The database instance.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Recreate the table
        onCreate(db);
    }

    /**
     * Adds a new user to the database.
     * @param Username The username of the user.
     * @param Password The password of the user.
     * @param Email The email of the user.
     */

    public void addUser(String Username, String Password, String Email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, Username);
        cv.put(COLUMN_PASSWORD, Password);
        cv.put(COLUMN_EMAIL, Email);
        cv.put(COLUMN_REPORTS, 0);

        db.insert(TABLE_NAME,null, cv);
    }

    /**
     * Updates user data in the database based on the specified row ID.
     * @param row_id The ID of the row to be updated.
     * @param Username The new username.
     * @param Password The new password.
     * @param Email The new email.
     */
    public void updateData(String row_id, String Username, String Password, String Email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, Username);
        cv.put(COLUMN_PASSWORD, Password);
        cv.put(COLUMN_EMAIL, Email);

        // Update the row in the database
        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            // Show a toast message if the update fails
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            // Show a toast message if the update is successful
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes a single row from the database based on the specified row ID.
     * @param row_id The ID of the row to be deleted.
     */
    public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete the row from the database
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            // Show a toast message if the deletion fails
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        } else {
            // Show a toast message if the deletion is successful
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes all data from the database.
     */
    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete all data from the table
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    /**
     * Retrieves the ID of a user by their username.
     * @param user The username of the user.
     * @return The ID of the user if found, otherwise null.
     */
    public String getIdByName(String user){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_ID +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{user});
        if(cursor.getCount() == -1){
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    /**
     * Checks if a user with the specified username exists in the database.
     * @param user The username to check.
     * @return True if the user does not exist, otherwise false.
     */
    public boolean findUser(String user){
        boolean isUnique;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_USERNAME +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";

        cursor = db.rawQuery(query, new String[]{user});
        isUnique = cursor.getCount() == 0;
        cursor.close();

        return isUnique;
    }

    /**
     * Checks if a user with the specified username and email does not exist locally in the database.
     * @param user The username to check.
     * @param email The email to check.
     * @return True if the user does not exist, otherwise false.
     */
    public boolean userExistsNotLocal(String user, String email) {
        boolean isUnique;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_USERNAME +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_EMAIL + " = ?";

        cursor = db.rawQuery(query, new String[]{user, email});
        isUnique = cursor.getCount() == 0;
        cursor.close();
        return isUnique;
    }

    /**
     * Checks if an email is unique in the database.
     * @param email The email to check.
     * @return True if the email is unique, otherwise false.
     */
    public boolean findEmail(String email) {
        boolean isUnique;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_EMAIL +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ?";

        cursor = db.rawQuery(query, new String[]{email});
        isUnique = cursor.getCount() == 0;
        cursor.close();
        return isUnique;
    }

    /**
     * Retrieves the number of reports associated with a user by their ID.
     * @param ID The ID of the user.
     * @return The number of reports if found, otherwise -1.
     */
    public int getReportsByID(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_REPORTS +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{ID});
        if(cursor.getCount() == -1) {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    /**
     * Retrieves a cursor containing user data by their username.
     * @param user The username of the user.
     * @return A cursor containing user data if found, otherwise null.
     */
    public Cursor getUserByName(String user) {
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";

        cursor = db.rawQuery(query, new String[]{user});
        if(cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getUserByEmail(String email) {
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ?";

        cursor = db.rawQuery(query, new String[]{email});
        if(cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * Increases the number of reports associated with a user identified by their ID.
     * @param id The ID of the user.
     */
    public void addReport(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String query = "SELECT " + COLUMN_REPORTS + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        cursor.moveToFirst();
        cv.put(COLUMN_REPORTS, cursor.getInt(0) + 1);
        // Update the number of reports in the database for the specified user
        db.update(TABLE_NAME, cv, "_id=?", new String[]{id});
    }

    /**
     * Updates the number of reports associated with a user identified by their ID.
     * @param id The ID of the user.
     * @param reports The new number of reports.
     */
    public void updateReports(String id, int reports) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String query = "SELECT " + COLUMN_REPORTS + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        cursor.moveToFirst();
        cv.put(COLUMN_REPORTS, reports);
        // Update the number of reports in the database for the specified user
        db.update(TABLE_NAME, cv, "_id=?", new String[]{id});
    }

    /**
     * Logs in a user based on the provided username, password, and login method.
     * @param user The username or email of the user.
     * @param password The password of the user.
     * @param EmailLogin The login method (1 for username, 2 for email).
     * @return True if the login is successful, otherwise false.
     */
    public boolean loginUser(String user, String password, int EmailLogin) {
        boolean isExist = false;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query1 = "SELECT "+  COLUMN_USERNAME +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String query2 = "SELECT "+  COLUMN_EMAIL +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";

        switch (EmailLogin) {
            case 1: {
                cursor = db.rawQuery(query1, new String[]{user, password});
                isExist = cursor.getCount() == 1;
                cursor.close();
                return isExist;
            }
            case 2:
            {
                cursor = db.rawQuery(query2, new String[]{user, password});
                isExist = cursor.getCount() == 1;
                cursor.close();
                return isExist;
            }
        }
        return isExist;
    }


}
