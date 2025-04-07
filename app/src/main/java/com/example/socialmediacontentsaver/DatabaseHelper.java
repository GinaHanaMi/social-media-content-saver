package com.example.socialmediacontentsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "savedContent.db";
    public static final String TABLE_NAME = "saved_content_table";

    public static final String COL_1 = "id";
    public static final String COL_2 = "thumbnail";
    public static final String COL_3 = "title";
    public static final String COL_4 = "platform";
    public static final String COL_5 = "save_date";
    public static final String COL_6 = "link";




    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,thumbnail TEXT,title TEXT,duration INTEGER,platform TEXT,creator TEXT, save_date TEXT, link TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public void insertData() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

    };

}
