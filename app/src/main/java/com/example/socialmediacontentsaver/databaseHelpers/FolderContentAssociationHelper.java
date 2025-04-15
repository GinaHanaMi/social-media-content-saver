package com.example.socialmediacontentsaver.databaseHelpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FolderContentAssociationHelper {
    private SQLiteDatabase db;

    public static final String FOLDER_CONTENT_TABLE = "folder_content_table";

    public FolderContentAssociationHelper(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean addContentToFolder(int folderId, int contentId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("folder_id", folderId);
        contentValues.put("content_id", contentId);
        long result = db.insert(FOLDER_CONTENT_TABLE, null, contentValues);
        return result != -1;
    }

    public boolean removeContentFromFolder(int folderId, int contentId) {
        int result = db.delete(FOLDER_CONTENT_TABLE,
                "folder_id = ? AND content_id = ?",
                new String[]{String.valueOf(folderId), String.valueOf(contentId)});
        return result > 0;
    }

    public Cursor getContentsInFolder(int folderId) {
        return db.rawQuery("SELECT sc.* FROM saved_content_table sc " +
                "INNER JOIN " + FOLDER_CONTENT_TABLE + " fc ON sc.id = fc.content_id " +
                "WHERE fc.folder_id = ?", new String[]{String.valueOf(folderId)});
    }
}
