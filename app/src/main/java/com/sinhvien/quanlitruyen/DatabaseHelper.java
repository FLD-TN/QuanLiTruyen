package com.sinhvien.quanlitruyen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinhvien.quanlitruyen.model.Chuong;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CBZDatabase";
    private static final int DATABASE_VERSION = 5;

    //  TRUYEN
    private static final String TABLE_TRUYEN = "Truyen";
    private static final String COL_MA_TRUYEN = "MaTruyen";
    private static final String COL_TEN_TRUYEN = "TenTruyen";
    private static final String COL_MO_TA = "MoTa";
    private static final String COL_FILE_HASH = "FileHash";
    private static final String COL_COVER_IMAGE = "CoverImage";

    //  CHUONG
    private static final String TABLE_CHUONG = "Chuong";
    private static final String COL_MA_CHUONG = "MaChuong";
    private static final String COL_MA_TRUYEN_FK = "MaTruyen";
    private static final String COL_TEN_CHUONG = "TenChuong";
    private static final String COL_SO_CHUONG = "SoChuong";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng truyện
        String CREATE_TRUYEN = "CREATE TABLE IF NOT EXISTS " + TABLE_TRUYEN + "("
                + COL_MA_TRUYEN + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TEN_TRUYEN + " TEXT NOT NULL,"
                + COL_MO_TA + " TEXT,"
                + COL_FILE_HASH + " TEXT,"
                + COL_COVER_IMAGE + " TEXT)";
        db.execSQL(CREATE_TRUYEN);

        // Bảng chương
        String CREATE_CHUONG = "CREATE TABLE IF NOT EXISTS " + TABLE_CHUONG + "("
                + COL_MA_CHUONG + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MA_TRUYEN_FK + " INTEGER,"
                + COL_TEN_CHUONG + " TEXT,"
                + COL_SO_CHUONG + " INTEGER,"
                + "FOREIGN KEY (" + COL_MA_TRUYEN_FK + ") REFERENCES " + TABLE_TRUYEN + "(" + COL_MA_TRUYEN + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_CHUONG);

        String CREATE_CBZ = "CREATE TABLE IF NOT EXISTS cbz_files (" +
                "file_hash TEXT, " +
                "uri TEXT, " +
                "image_path TEXT)";
        db.execSQL(CREATE_CBZ);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TABLE_TRUYEN + " ADD COLUMN " + COL_COVER_IMAGE + " TEXT");
        }
    }


    // --- CRUD TRUYEN ---
    public long insertTruyen(String tenTruyen, String moTa, String fileHash, String coverImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TEN_TRUYEN, tenTruyen);
        values.put(COL_MO_TA, moTa);
        values.put(COL_FILE_HASH, fileHash);
        values.put(COL_COVER_IMAGE, coverImagePath);
        return db.insert(TABLE_TRUYEN, null, values);
    }

    public List<Truyen> getAllTruyen() {
        List<Truyen> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRUYEN, null);
        if (cursor.moveToFirst()) {
            do {
                int maTruyen = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MA_TRUYEN));
                String ten = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEN_TRUYEN));
                String moTa = cursor.getString(cursor.getColumnIndexOrThrow(COL_MO_TA));
                String hash = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILE_HASH));
                String cover = cursor.getString(cursor.getColumnIndexOrThrow(COL_COVER_IMAGE));
                list.add(new Truyen(maTruyen, ten, moTa, hash, false, 0, 0, cover,null));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Truyen getTruyen(int maTruyen) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TRUYEN, null, COL_MA_TRUYEN + "=?",
                new String[]{String.valueOf(maTruyen)}, null, null, null);
        Truyen truyen = null;
        if (cursor.moveToFirst()) {
            String ten = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEN_TRUYEN));
            String moTa = cursor.getString(cursor.getColumnIndexOrThrow(COL_MO_TA));
            String hash = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILE_HASH));
            String cover = cursor.getString(cursor.getColumnIndexOrThrow(COL_COVER_IMAGE));
            truyen = new Truyen(maTruyen, ten, moTa, hash, false, 0, 0, cover,null);
        }
        cursor.close();
        return truyen;
    }

    // --- CRUD CHUONG ---
    public long insertChuong(int maTruyen, String tenChuong, int soChuong) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MA_TRUYEN_FK, maTruyen);
        values.put(COL_TEN_CHUONG, tenChuong);
        values.put(COL_SO_CHUONG, soChuong);
        return db.insert(TABLE_CHUONG, null, values);
    }

    public List<Chuong> getAllChuongByTruyen(int maTruyen) {
        List<Chuong> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHUONG + " WHERE " + COL_MA_TRUYEN_FK + "= ?", new String[]{String.valueOf(maTruyen)});
        if (cursor.moveToFirst()) {
            do {
                int maChuong = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MA_CHUONG));
                String tenChuong = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEN_CHUONG));
                int soChuong = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SO_CHUONG));
                list.add(new Chuong(maChuong, maTruyen, tenChuong, soChuong));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getImagePaths(String fileHash) {
        List<String> imagePaths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cbz_files", new String[]{"image_path"},
                "file_hash=?", new String[]{fileHash}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                imagePaths.add(cursor.getString(cursor.getColumnIndexOrThrow("image_path")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return imagePaths;
    }

    public void saveCBZFile(String fileHash, String uri, List<String> imagePaths) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cbz_files", "file_hash=?", new String[]{fileHash});

        for (String path : imagePaths) {
            ContentValues values = new ContentValues();
            values.put("file_hash", fileHash);
            values.put("uri", uri);
            values.put("image_path", path);
            db.insert("cbz_files", null, values);
        }
        db.close();
    }

    public List<Truyen> getRecentTruyen() {
        List<Truyen> all = getAllTruyen();
        return all.size() > 10 ? all.subList(0, 10) : all;
    }

    public boolean deleteTruyen(int maTruyen) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("Truyen", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
        return result > 0;
    }

    public boolean updateTruyen(int maTruyen, String tenTruyen, String moTa, String coverImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenTruyen", tenTruyen);
        values.put("MoTa", moTa);
        values.put("CoverImage", coverImagePath);

        int result = db.update("Truyen", values, "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
        return result > 0;
    }

    public String getNoteForTruyen(int maTruyen) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT GhiChu FROM Truyen WHERE MaTruyen=?", new String[]{String.valueOf(maTruyen)});
        if (cursor.moveToFirst()) {
            String note = cursor.getString(0);
            cursor.close();
            return note != null ? note : "";
        }
        cursor.close();
        return "";
    }

    public void updateNoteForTruyen(int maTruyen, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("GhiChu", note);
        db.update("Truyen", values, "MaTruyen=?", new String[]{String.valueOf(maTruyen)});
    }

}
