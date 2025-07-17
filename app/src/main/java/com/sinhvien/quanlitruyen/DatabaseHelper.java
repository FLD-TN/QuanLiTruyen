package com.sinhvien.quanlitruyen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sinhvien.quanlitruyen.model.Chuong;
import com.sinhvien.quanlitruyen.model.ReadingHistory;
import com.sinhvien.quanlitruyen.model.TheLoai;
import com.sinhvien.quanlitruyen.model.Truyen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuanLiTruyen.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTruyenTable = "CREATE TABLE Truyen (" +
                "MaTruyen INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TenTruyen TEXT, " +
                "MoTa TEXT, " +
                "CoverImagePath TEXT, " +
                "FileHash TEXT, " +
                "IsFavorite INTEGER DEFAULT 0, " +
                "SoChuong INTEGER DEFAULT 0, " +
                "MaTheLoai INTEGER, " +
                "Note TEXT)";
        db.execSQL(createTruyenTable);

        String createChuongTable = "CREATE TABLE Chuong (" +
                "MaChuong INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaTruyen INTEGER, " +
                "TenChuong TEXT, " +
                "SoChuong INTEGER, " +
                "FOREIGN KEY(MaTruyen) REFERENCES Truyen(MaTruyen) ON DELETE CASCADE)";
        db.execSQL(createChuongTable);

        String createTheLoaiTable = "CREATE TABLE TheLoai (" +
                "MaTheLoai INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TenTheLoai TEXT UNIQUE)";
        db.execSQL(createTheLoaiTable);

        String createTruyenTheLoaiTable = "CREATE TABLE Truyen_TheLoai (" +
                "MaTruyen INTEGER, " +
                "MaTheLoai INTEGER, " +
                "PRIMARY KEY(MaTruyen, MaTheLoai), " +
                "FOREIGN KEY(MaTruyen) REFERENCES Truyen(MaTruyen) ON DELETE CASCADE, " +
                "FOREIGN KEY(MaTheLoai) REFERENCES TheLoai(MaTheLoai) ON DELETE CASCADE)";
        db.execSQL(createTruyenTheLoaiTable);

        String createChapterImagesTable = "CREATE TABLE chapter_images (" +
                "MaImage INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaTruyen INTEGER, " +
                "MaChuong INTEGER, " +
                "ImagePath TEXT, " +
                "FOREIGN KEY(MaTruyen) REFERENCES Truyen(MaTruyen) ON DELETE CASCADE, " +
                "FOREIGN KEY(MaChuong) REFERENCES Chuong(MaChuong) ON DELETE CASCADE)";
        db.execSQL(createChapterImagesTable);

        String createCbzFilesTable = "CREATE TABLE cbz_files (" +
                "MaFile INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaTruyen INTEGER, " +
                "FilePath TEXT, " +
                "FOREIGN KEY(MaTruyen) REFERENCES Truyen(MaTruyen) ON DELETE CASCADE)";
        db.execSQL(createCbzFilesTable);

        String createReadingHistoryTable = "CREATE TABLE ReadingHistory (" +
                "HistoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MaTruyen INTEGER, " +
                "ReadTimestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(MaTruyen) REFERENCES Truyen(MaTruyen) ON DELETE CASCADE)";
        db.execSQL(createReadingHistoryTable);

        String createLastReadPageTable = "CREATE TABLE LastReadPage (" +
                "MaTruyen INTEGER, " +
                "MaChuong INTEGER, " +
                "LastPageIndex INTEGER, " +
                "LastReadTimestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY(MaTruyen, MaChuong), " +
                "FOREIGN KEY(MaTruyen) REFERENCES Truyen(MaTruyen) ON DELETE CASCADE, " +
                "FOREIGN KEY(MaChuong) REFERENCES Chuong(MaChuong) ON DELETE CASCADE)";
        db.execSQL(createLastReadPageTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String createReadingHistoryTable = "CREATE TABLE ReadingHistory (" +
                    "HistoryId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "MaTruyen INTEGER, " +
                    "ReadTimestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(MaTruyen) REFERENCES Truyen(MaTruyen) ON DELETE CASCADE)";
            db.execSQL(createReadingHistoryTable);
        }
        if (oldVersion < 3) {
            String createLastReadPageTable = "CREATE TABLE LastReadPage (" +
                    "MaTruyen INTEGER, " +
                    "MaChuong INTEGER, " +
                    "LastPageIndex INTEGER, " +
                    "LastReadTimestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY(MaTruyen, MaChuong), " +
                    "FOREIGN KEY(MaTruyen) REFERENCES Truyen(MaTruyen) ON DELETE CASCADE, " +
                    "FOREIGN KEY(MaChuong) REFERENCES Chuong(MaChuong) ON DELETE CASCADE)";
            db.execSQL(createLastReadPageTable);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    public List<Truyen> searchTruyenByNameOrGenre(String query) {
        List<Truyen> truyenList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT DISTINCT t.* FROM Truyen t " +
                "LEFT JOIN Truyen_TheLoai tt ON t.MaTruyen = tt.MaTruyen " +
                "LEFT JOIN TheLoai tl ON tt.MaTheLoai = tl.MaTheLoai " +
                "WHERE t.TenTruyen LIKE ? OR tl.TenTheLoai LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        Cursor cursor = db.rawQuery(searchQuery, selectionArgs);
        while (cursor.moveToNext()) {
            Truyen truyen = new Truyen();
            truyen.setMaTruyen(cursor.getInt(cursor.getColumnIndexOrThrow("MaTruyen")));
            truyen.setTenTruyen(cursor.getString(cursor.getColumnIndexOrThrow("TenTruyen")));
            truyen.setMoTa(cursor.getString(cursor.getColumnIndexOrThrow("MoTa")));
            truyen.setCoverImagePath(cursor.getString(cursor.getColumnIndexOrThrow("CoverImagePath")));
            truyen.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow("IsFavorite")) == 1);
            truyen.setSoChuong(cursor.getInt(cursor.getColumnIndexOrThrow("SoChuong")));
            truyen.setMaTheLoai(cursor.getInt(cursor.getColumnIndexOrThrow("MaTheLoai")));
            truyen.setFileHash(cursor.getString(cursor.getColumnIndexOrThrow("FileHash")));
            truyen.setNote(cursor.getString(cursor.getColumnIndexOrThrow("Note")));
            truyenList.add(truyen);
        }
        cursor.close();
        return truyenList;
    }

    public long insertTruyen(String tenTruyen, String moTa, String coverImagePath, String fileHash) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenTruyen", tenTruyen);
        values.put("MoTa", moTa);
        values.put("CoverImagePath", coverImagePath);
        values.put("FileHash", fileHash);
        values.put("IsFavorite", 0);
        values.put("SoChuong", 0);
        values.put("MaTheLoai", -1);
        values.put("Note", "");
        return db.insert("Truyen", null, values);
    }

    public long insertTheLoai(String tenTheLoai) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("TheLoai", new String[]{"MaTheLoai"}, "TenTheLoai = ?",
                new String[]{tenTheLoai}, null, null, null);
        if (cursor.moveToFirst()) {
            long maTheLoai = cursor.getLong(cursor.getColumnIndexOrThrow("MaTheLoai"));
            cursor.close();
            return maTheLoai;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("TenTheLoai", tenTheLoai);
        return db.insert("TheLoai", null, values);
    }

    public long insertTruyenTheLoai(int maTruyen, int maTheLoai) {
        if (maTheLoai == -1) {
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("Truyen_TheLoai", new String[]{"MaTruyen"},
                "MaTruyen = ? AND MaTheLoai = ?",
                new String[]{String.valueOf(maTruyen), String.valueOf(maTheLoai)},
                null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return -1;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("MaTruyen", maTruyen);
        values.put("MaTheLoai", maTheLoai);
        return db.insert("Truyen_TheLoai", null, values);
    }

    public long insertReadingHistory(int maTruyen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("MaTruyen", maTruyen);
        return db.insert("ReadingHistory", null, values);
    }

    public void saveLastReadPage(int maTruyen, int maChuong, int lastPageIndex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("MaTruyen", maTruyen);
        values.put("MaChuong", maChuong);
        values.put("LastPageIndex", lastPageIndex);
        values.put("LastReadTimestamp", "CURRENT_TIMESTAMP");
        db.replace("LastReadPage", null, values);
    }

    public int getLastReadPage(int maTruyen, int maChuong) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("LastReadPage", new String[]{"LastPageIndex"},
                "MaTruyen = ? AND MaChuong = ?",
                new String[]{String.valueOf(maTruyen), String.valueOf(maChuong)},
                null, null, null);
        if (cursor.moveToFirst()) {
            int lastPageIndex = cursor.getInt(cursor.getColumnIndexOrThrow("LastPageIndex"));
            cursor.close();
            return lastPageIndex;
        }
        cursor.close();
        return -1;
    }

    public Chuong getChuong(int maChuong) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Chuong", null, "MaChuong = ?",
                new String[]{String.valueOf(maChuong)}, null, null, null);
        if (cursor.moveToFirst()) {
            Chuong chuong = new Chuong();
            chuong.setMaChuong(cursor.getInt(cursor.getColumnIndexOrThrow("MaChuong")));
            chuong.setMaTruyen(cursor.getInt(cursor.getColumnIndexOrThrow("MaTruyen")));
            chuong.setTenChuong(cursor.getString(cursor.getColumnIndexOrThrow("TenChuong")));
            chuong.setSoChuong(cursor.getInt(cursor.getColumnIndexOrThrow("SoChuong")));
            cursor.close();
            return chuong;
        }
        cursor.close();
        return null;
    }

    public List<ReadingHistory> getReadingHistory() {
        List<ReadingHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT rh.HistoryId, rh.MaTruyen, rh.ReadTimestamp, t.TenTruyen " +
                "FROM ReadingHistory rh JOIN Truyen t ON rh.MaTruyen = t.MaTruyen " +
                "ORDER BY rh.ReadTimestamp DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            ReadingHistory history = new ReadingHistory();
            history.setHistoryId(cursor.getInt(cursor.getColumnIndexOrThrow("HistoryId")));
            history.setMaTruyen(cursor.getInt(cursor.getColumnIndexOrThrow("MaTruyen")));
            history.setReadTimestamp(cursor.getString(cursor.getColumnIndexOrThrow("ReadTimestamp")));
            history.setTenTruyen(cursor.getString(cursor.getColumnIndexOrThrow("TenTruyen")));
            historyList.add(history);
        }
        cursor.close();
        return historyList;
    }

    public Map<String, Integer> getReadingStatsByPeriod(String period) {
        Map<String, Integer> stats = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query;
        if ("daily".equals(period)) {
            query = "SELECT strftime('%Y-%m-%d', ReadTimestamp) as period, COUNT(*) as count " +
                    "FROM ReadingHistory GROUP BY period ORDER BY period DESC LIMIT 30";
        } else if ("weekly".equals(period)) {
            query = "SELECT strftime('%Y-%W', ReadTimestamp) as period, COUNT(*) as count " +
                    "FROM ReadingHistory GROUP BY period ORDER BY period DESC LIMIT 12";
        } else { // monthly
            query = "SELECT strftime('%Y-%m', ReadTimestamp) as period, COUNT(*) as count " +
                    "FROM ReadingHistory GROUP BY period ORDER BY period DESC LIMIT 12";
        }
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            stats.put(cursor.getString(cursor.getColumnIndexOrThrow("period")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("count")));
        }
        cursor.close();
        return stats;
    }

    public Map<String, Integer> getMostReadStories() {
        Map<String, Integer> stats = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t.TenTruyen, COUNT(*) as count " +
                "FROM ReadingHistory rh JOIN Truyen t ON rh.MaTruyen = t.MaTruyen " +
                "GROUP BY t.MaTruyen, t.TenTruyen ORDER BY count DESC LIMIT 5";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            stats.put(cursor.getString(cursor.getColumnIndexOrThrow("TenTruyen")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("count")));
        }
        cursor.close();
        return stats;
    }

    public Map<String, Integer> getMostReadGenres() {
        Map<String, Integer> stats = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT tl.TenTheLoai, COUNT(*) as count " +
                "FROM ReadingHistory rh " +
                "JOIN Truyen_TheLoai tt ON rh.MaTruyen = tt.MaTruyen " +
                "JOIN TheLoai tl ON tt.MaTheLoai = tl.MaTheLoai " +
                "GROUP BY tl.MaTheLoai, tl.TenTheLoai ORDER BY count DESC LIMIT 5";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            stats.put(cursor.getString(cursor.getColumnIndexOrThrow("TenTheLoai")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("count")));
        }
        cursor.close();
        return stats;
    }

    public List<String> getTheLoaiByTruyen(int maTruyen) {
        List<String> theLoaiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t.TenTheLoai FROM TheLoai t " +
                "JOIN Truyen_TheLoai tt ON t.MaTheLoai = tt.MaTheLoai " +
                "WHERE tt.MaTruyen = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(maTruyen)});
        while (cursor.moveToNext()) {
            theLoaiList.add(cursor.getString(cursor.getColumnIndexOrThrow("TenTheLoai")));
        }
        cursor.close();
        return theLoaiList;
    }

    public List<String> getImagesByTruyen(int maTruyen) {
        List<String> imagePaths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("chapter_images", new String[]{"ImagePath"},
                "MaTruyen = ?", new String[]{String.valueOf(maTruyen)},
                null, null, null);
        while (cursor.moveToNext()) {
            imagePaths.add(cursor.getString(cursor.getColumnIndexOrThrow("ImagePath")));
        }
        cursor.close();
        return imagePaths;
    }

    public List<String> getImagePathsByChuong(int maChuong) {
        List<String> imagePaths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("chapter_images", new String[]{"ImagePath"},
                "MaChuong = ?", new String[]{String.valueOf(maChuong)},
                null, null, null);
        while (cursor.moveToNext()) {
            imagePaths.add(cursor.getString(cursor.getColumnIndexOrThrow("ImagePath")));
        }
        cursor.close();
        return imagePaths;
    }

    public List<Chuong> getAllChuongByTruyen(int maTruyen) {
        List<Chuong> chuongList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Chuong", null, "MaTruyen = ?",
                new String[]{String.valueOf(maTruyen)}, null, null, "SoChuong ASC");
        while (cursor.moveToNext()) {
            Chuong chuong = new Chuong();
            chuong.setMaChuong(cursor.getInt(cursor.getColumnIndexOrThrow("MaChuong")));
            chuong.setMaTruyen(cursor.getInt(cursor.getColumnIndexOrThrow("MaTruyen")));
            chuong.setTenChuong(cursor.getString(cursor.getColumnIndexOrThrow("TenChuong")));
            chuong.setSoChuong(cursor.getInt(cursor.getColumnIndexOrThrow("SoChuong")));
            chuongList.add(chuong);
        }
        cursor.close();
        return chuongList;
    }

    public List<Truyen> getAllTruyen() {
        List<Truyen> truyenList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Truyen", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Truyen truyen = new Truyen();
            truyen.setMaTruyen(cursor.getInt(cursor.getColumnIndexOrThrow("MaTruyen")));
            truyen.setTenTruyen(cursor.getString(cursor.getColumnIndexOrThrow("TenTruyen")));
            truyen.setMoTa(cursor.getString(cursor.getColumnIndexOrThrow("MoTa")));
            truyen.setCoverImagePath(cursor.getString(cursor.getColumnIndexOrThrow("CoverImagePath")));
            truyen.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow("IsFavorite")) == 1);
            truyen.setSoChuong(cursor.getInt(cursor.getColumnIndexOrThrow("SoChuong")));
            truyen.setMaTheLoai(cursor.getInt(cursor.getColumnIndexOrThrow("MaTheLoai")));
            truyen.setFileHash(cursor.getString(cursor.getColumnIndexOrThrow("FileHash")));
            truyen.setNote(cursor.getString(cursor.getColumnIndexOrThrow("Note")));
            truyenList.add(truyen);
        }
        cursor.close();
        return truyenList;
    }

    public List<Truyen> getRecentTruyen() {
        List<Truyen> truyenList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Truyen", null, null, null, null, null, "MaTruyen DESC", "10");
        while (cursor.moveToNext()) {
            Truyen truyen = new Truyen();
            truyen.setMaTruyen(cursor.getInt(cursor.getColumnIndexOrThrow("MaTruyen")));
            truyen.setTenTruyen(cursor.getString(cursor.getColumnIndexOrThrow("TenTruyen")));
            truyen.setMoTa(cursor.getString(cursor.getColumnIndexOrThrow("MoTa")));
            truyen.setCoverImagePath(cursor.getString(cursor.getColumnIndexOrThrow("CoverImagePath")));
            truyen.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow("IsFavorite")) == 1);
            truyen.setSoChuong(cursor.getInt(cursor.getColumnIndexOrThrow("SoChuong")));
            truyen.setMaTheLoai(cursor.getInt(cursor.getColumnIndexOrThrow("MaTheLoai")));
            truyen.setFileHash(cursor.getString(cursor.getColumnIndexOrThrow("FileHash")));
            truyen.setNote(cursor.getString(cursor.getColumnIndexOrThrow("Note")));
            truyenList.add(truyen);
        }
        cursor.close();
        return truyenList;
    }

    public Truyen getTruyen(int maTruyen) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Truyen", null, "MaTruyen = ?",
                new String[]{String.valueOf(maTruyen)}, null, null, null);
        if (cursor.moveToFirst()) {
            Truyen truyen = new Truyen();
            truyen.setMaTruyen(cursor.getInt(cursor.getColumnIndexOrThrow("MaTruyen")));
            truyen.setTenTruyen(cursor.getString(cursor.getColumnIndexOrThrow("TenTruyen")));
            truyen.setMoTa(cursor.getString(cursor.getColumnIndexOrThrow("MoTa")));
            truyen.setCoverImagePath(cursor.getString(cursor.getColumnIndexOrThrow("CoverImagePath")));
            truyen.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow("IsFavorite")) == 1);
            truyen.setSoChuong(cursor.getInt(cursor.getColumnIndexOrThrow("SoChuong")));
            truyen.setMaTheLoai(cursor.getInt(cursor.getColumnIndexOrThrow("MaTheLoai")));
            truyen.setFileHash(cursor.getString(cursor.getColumnIndexOrThrow("FileHash")));
            truyen.setNote(cursor.getString(cursor.getColumnIndexOrThrow("Note")));
            cursor.close();
            return truyen;
        }
        cursor.close();
        return null;
    }

    public boolean updateTruyen(int maTruyen, String tenTruyen, String moTa, String coverImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenTruyen", tenTruyen);
        values.put("MoTa", moTa);
        values.put("CoverImagePath", coverImagePath);
        return db.update("Truyen", values, "MaTruyen = ?",
                new String[]{String.valueOf(maTruyen)}) > 0;
    }

    public boolean updateTruyenNote(int maTruyen, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Note", note);
        return db.update("Truyen", values, "MaTruyen = ?",
                new String[]{String.valueOf(maTruyen)}) > 0;
    }

    public void deleteTruyen(int maTruyen) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("LastReadPage", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
        db.delete("Truyen", "MaTruyen = ?", new String[]{String.valueOf(maTruyen)});
    }

    public List<TheLoai> getAllTheLoai() {
        List<TheLoai> theLoaiList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("TheLoai", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            TheLoai theLoai = new TheLoai();
            theLoai.setMaTheLoai(cursor.getInt(cursor.getColumnIndexOrThrow("MaTheLoai")));
            theLoai.setTenTheLoai(cursor.getString(cursor.getColumnIndexOrThrow("TenTheLoai")));
            theLoaiList.add(theLoai);
        }
        cursor.close();
        return theLoaiList;
    }

    public int updateTheLoai(int maTheLoai, String tenTheLoai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenTheLoai", tenTheLoai);
        return db.update("TheLoai", values, "MaTheLoai = ?",
                new String[]{String.valueOf(maTheLoai)});
    }

    public void deleteTheLoai(int maTheLoai) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("TheLoai", "MaTheLoai = ?", new String[]{String.valueOf(maTheLoai)});
    }
}
