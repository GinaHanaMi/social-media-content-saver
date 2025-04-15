package com.example.socialmediacontentsaver.databaseHelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FolderDatabaseHelper {
    private SQLiteDatabase db;

    public static final String FOLDERS_TABLE = "folders_table";

    public FolderDatabaseHelper(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean insertFolder(String thumbnail, String title, String description, String createdAt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("thumbnail", thumbnail);
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("created_at", createdAt);
        long result = db.insert(FOLDERS_TABLE, null, contentValues);
        return result != -1;
    }

    public Cursor getAllFolders() {
        return db.rawQuery("SELECT * FROM " + FOLDERS_TABLE, null);
    }

    public boolean updateFolder(int id, String thumbnail, String title, String description, String createdAt) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("thumbnail", thumbnail);
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("created_at", createdAt);
        int result = db.update(FOLDERS_TABLE, contentValues, "id = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deleteFolder(int id) {
        int result = db.delete(FOLDERS_TABLE, "id = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }
}
