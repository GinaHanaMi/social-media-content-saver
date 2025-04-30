package com.example.socialmediacontentsaver.databaseHelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ContentDatabaseHelper {
    private SQLiteDatabase db;

    public ContentDatabaseHelper(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertContent(String thumbnail, String title, String description, String platform, String saveDate, String link) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("thumbnail", thumbnail);
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("platform", platform);
        contentValues.put("save_date", saveDate);
        contentValues.put("link", link);
        return db.insert(AppDatabaseHelper.CONTENT_TABLE, null, contentValues);
    }

    public Cursor getAllContent() {
        return db.rawQuery("SELECT * FROM " + AppDatabaseHelper.CONTENT_TABLE + " ORDER BY ID DESC", null);
    }

    public Cursor getContentById(int id) {
        return db.rawQuery("SELECT * FROM " + AppDatabaseHelper.CONTENT_TABLE + " WHERE ID = ?", new String[]{String.valueOf(id)});
    }

    public boolean updateContent(int id, String thumbnail, String title, String description, String platform, String saveDate, String link) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("thumbnail", thumbnail);
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("platform", platform);
        contentValues.put("save_date", saveDate);
        contentValues.put("link", link);
        int result = db.update(AppDatabaseHelper.CONTENT_TABLE, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public boolean deleteContent(int id) {
        int result = db.delete(AppDatabaseHelper.CONTENT_TABLE, "ID = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public Cursor getFoldersForContent(int contentId) {
        return db.rawQuery("SELECT f.id FROM " + AppDatabaseHelper.FOLDERS_TABLE + " f " +
                "INNER JOIN " + AppDatabaseHelper.FOLDER_CONTENT_TABLE + " fc " +
                "ON f.id = fc.folder_id " +
                "WHERE fc.content_id = ?", new String[]{String.valueOf(contentId)});
    }
}
