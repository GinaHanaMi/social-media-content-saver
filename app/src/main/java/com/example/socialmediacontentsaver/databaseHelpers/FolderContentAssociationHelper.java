package com.example.socialmediacontentsaver.databaseHelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FolderContentAssociationHelper {
    private SQLiteDatabase db;

    public FolderContentAssociationHelper(SQLiteDatabase db) {
        this.db = db;
    }

    public long addContentToFolder(int folderId, int contentId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("folder_id", folderId);
        contentValues.put("content_id", contentId);
        return db.insert(AppDatabaseHelper.FOLDER_CONTENT_TABLE, null, contentValues);
    }

    public boolean removeContentFromFolder(int folderId, int contentId) {
        int result = db.delete(AppDatabaseHelper.FOLDER_CONTENT_TABLE,
                "folder_id = ? AND content_id = ?",
                new String[]{String.valueOf(folderId), String.valueOf(contentId)});
        return result > 0;
    }

    public Cursor getContentsInFolder(int folderId) {
        return db.rawQuery("SELECT c.* FROM " + AppDatabaseHelper.CONTENT_TABLE + " c " +
                "INNER JOIN " + AppDatabaseHelper.FOLDER_CONTENT_TABLE + " fc " +
                "ON c.ID = fc.content_id " +
                "WHERE fc.folder_id = ?", new String[]{String.valueOf(folderId)});
    }

    public Cursor getFoldersForContent(int contentId) {
        return db.rawQuery("SELECT f.* FROM " + AppDatabaseHelper.FOLDERS_TABLE + " f " +
                "INNER JOIN " + AppDatabaseHelper.FOLDER_CONTENT_TABLE + " fc " +
                "ON f.id = fc.folder_id " +
                "WHERE fc.content_id = ?", new String[]{String.valueOf(contentId)});
    }

    public boolean deleteContentsInFolderAssociation(int folderId) {
        int result = db.delete(AppDatabaseHelper.FOLDER_CONTENT_TABLE,
                "folder_id = ?",
                new String[]{String.valueOf(folderId)});
        return result > 0;
    }

    public boolean isContentAssociatedWithAnyFolder(int contentId) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + AppDatabaseHelper.FOLDER_CONTENT_TABLE +
                " WHERE content_id = ? LIMIT 1", new String[]{String.valueOf(contentId)});
        boolean isAssociated = cursor.moveToFirst();
        cursor.close();
        return isAssociated;
    }
}
