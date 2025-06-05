package com.sinhvien.quanlitruyen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CBZDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CBZ_FILES = "cbz_files";
    private static final String KEY_ID = "id";
    private static final String KEY_FILE_HASH = "file_hash";
    private static final String KEY_URI = "uri";
    private static final String KEY_IMAGE_PATH = "image_path";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CBZ_FILES_TABLE = "CREATE TABLE " + TABLE_CBZ_FILES + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FILE_HASH + " TEXT,"
                + KEY_URI + " TEXT,"
                + KEY_IMAGE_PATH + " TEXT)";
        db.execSQL(CREATE_CBZ_FILES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CBZ_FILES);
        onCreate(db);
    }

    public void saveCBZFile(String fileHash, String uri, List<String> imagePaths) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CBZ_FILES, KEY_FILE_HASH + "=?", new String[]{fileHash}); // Xóa bản ghi cũ nếu có

        for (String path : imagePaths) {
            ContentValues values = new ContentValues();
            values.put(KEY_FILE_HASH, fileHash);
            values.put(KEY_URI, uri);
            values.put(KEY_IMAGE_PATH, path);
            db.insert(TABLE_CBZ_FILES, null, values);
        }
        db.close();
    }

    public List<String> getImagePaths(String fileHash) {
        List<String> imagePaths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CBZ_FILES, new String[]{KEY_IMAGE_PATH},
                KEY_FILE_HASH + "=?", new String[]{fileHash}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                imagePaths.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_PATH)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return imagePaths;
    }

    public String getUri(String fileHash) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CBZ_FILES, new String[]{KEY_URI},
                KEY_FILE_HASH + "=?", new String[]{fileHash}, null, null, null);
        String uri = "";
        if (cursor.moveToFirst()) {
            uri = cursor.getString(cursor.getColumnIndexOrThrow(KEY_URI));
        }
        cursor.close();
        db.close();
        return uri;
    }
}