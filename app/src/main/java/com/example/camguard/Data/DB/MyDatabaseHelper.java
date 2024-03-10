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

    private final Context context;
    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USERNAME = "column_username";
    private static final String COLUMN_PASSWORD = "column_password";
    private static final String COLUMN_EMAIL = "column_email";
    private static final String COLUMN_REPORTS = "column_reports";

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
                COLUMN_EMAIL + " TEXT," +
                COLUMN_REPORTS + " INTEGER )";
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
        cv.put(COLUMN_REPORTS, 0);

        db.insert(TABLE_NAME,null, cv);
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

    public String getIdByName(String user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_ID +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{user});
        if(cursor.getCount() == -1)
        {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public String getNameByEmail(String email)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_USERNAME +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email});
        if(cursor.getCount() == -1)
        {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(0);
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

    public boolean UserExistsNotLocal(String user, String email)
    {
        boolean isUnique;
        Cursor cursor;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_USERNAME +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_EMAIL + " = ?";

        cursor = db.rawQuery(query, new String[]{user, email});
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

    public int getReportsByID(String ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT "+  COLUMN_REPORTS +" FROM "+ TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{ID});
        if(cursor.getCount() == -1)
        {
            return -1;
        }
        cursor.moveToFirst();
        return cursor.getInt(0);
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

    public void AddReport(String id)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String query = "SELECT " + COLUMN_REPORTS + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        cursor.moveToFirst();
        cv.put(COLUMN_REPORTS, cursor.getInt(0) + 1);
        db.update(TABLE_NAME, cv, "_id=?", new String[]{id});
    }
    public void UpdateReports(String id, int reports)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String query = "SELECT " + COLUMN_REPORTS + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        cursor.moveToFirst();
        cv.put(COLUMN_REPORTS, reports);
        db.update(TABLE_NAME, cv, "_id=?", new String[]{id});
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
