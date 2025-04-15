package com.example.socialmediacontentsaver.databaseHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ContentDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "savedContent.db";
    public static final String TABLE_NAME = "saved_content_table";

    public static final String COL_1 = "id";
    public static final String COL_2 = "thumbnail";
    public static final String COL_3 = "title";
    public static final String COL_4 = "description";
    public static final String COL_5 = "platform";
    public static final String COL_6 = "save_date";
    public static final String COL_7 = "link";


    public ContentDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,thumbnail TEXT,title TEXT, description TEXT, platform TEXT, save_date TEXT, link TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String thumbnail, String title, String description, String platform, String save_date, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, thumbnail);
        contentValues.put(COL_3, title);
        contentValues.put(COL_4, description);
        contentValues.put(COL_5, platform);
        contentValues.put(COL_6, save_date);
        contentValues.put(COL_7, link);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

}
