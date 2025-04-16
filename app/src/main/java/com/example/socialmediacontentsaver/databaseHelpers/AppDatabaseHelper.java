package com.example.socialmediacontentsaver.databaseHelpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AppDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "savedContent.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CONTENT_TABLE = "saved_content_table";
    public static final String FOLDERS_TABLE = "folders_table";
    public static final String FOLDER_CONTENT_TABLE = "folder_content_table";

    public AppDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + CONTENT_TABLE + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "thumbnail TEXT," +
                "title TEXT," +
                "description TEXT," +
                "platform TEXT," +
                "save_date TEXT," +
                "link TEXT)");

        db.execSQL("CREATE TABLE " + FOLDERS_TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "thumbnail TEXT," +
                "title TEXT," +
                "description TEXT," +
                "created_at TEXT)");

        db.execSQL("CREATE TABLE " + FOLDER_CONTENT_TABLE + " (" +
                "folder_id INTEGER," +
                "content_id INTEGER," +
                "PRIMARY KEY (folder_id, content_id)," +
                "FOREIGN KEY (folder_id) REFERENCES " + FOLDERS_TABLE + "(id) ON DELETE CASCADE," +
                "FOREIGN KEY (content_id) REFERENCES " + CONTENT_TABLE + "(ID) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FOLDER_CONTENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FOLDERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONTENT_TABLE);
        onCreate(db);
    }
}
