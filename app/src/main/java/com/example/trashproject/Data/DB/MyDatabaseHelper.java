package com.example.trashproject.Data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_library";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USERNAME = "column_username";
    private static final String COLUMN_PASSWORD = "column_password";
    private static final String COLUMN_EMAIL = "column_email";





    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_EMAIL + " TEXT )";
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addUser(String Username, String Password, String Email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, Username);
        cv.put(COLUMN_PASSWORD, Password);
        cv.put(COLUMN_EMAIL, Email);

        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateData(String row_id, String Username, String Password, String Email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, Username);
        cv.put(COLUMN_PASSWORD, Password);
        cv.put(COLUMN_EMAIL, Email);


        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        Toast.makeText(context, "All data has been successfully deleted", Toast.LENGTH_SHORT).show();
    }

    public boolean FindUser(String user)
    {
        boolean isUnique;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_USERNAME +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";

              cursor = db.rawQuery(query, new String[]{user});
              isUnique = cursor.getCount() == 0;
              cursor.close();

        return isUnique;
    }

    public boolean FindEmail(String email)
    {
        boolean isUnique;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_EMAIL +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ?";

        cursor = db.rawQuery(query, new String[]{email});
        isUnique = cursor.getCount() == 0;
        cursor.close();

        return isUnique;
    }

    public Cursor getUserByName(String user)
    {
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";

        cursor = db.rawQuery(query, new String[]{user});
        if(cursor==null)
        {
            return null;
        }
        cursor.moveToFirst();
        return cursor;
    }

    public boolean LoginUser(String user, String password, int EmailLogin)
    {
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
