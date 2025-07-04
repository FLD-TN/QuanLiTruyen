package com.sinhvien.quanlitruyen.model;

public class Truyen {
    private int maTruyen;
    private String tenTruyen;
    private String moTa;
    private String fileHash;
    private int imageResId;
    private boolean yeuThich;
    private int tongThoiGianDoc;
    private String coverImagePath;
    private String note;


    public Truyen(int maTruyen, String tenTruyen, String moTa, String fileHash, boolean yeuThich, int tongThoiGianDoc, int imageResId) {
        this.maTruyen = maTruyen;
        this.tenTruyen = tenTruyen;
        this.moTa = moTa;
        this.fileHash = fileHash;
        this.yeuThich = yeuThich;
        this.tongThoiGianDoc = tongThoiGianDoc;
        this.imageResId = imageResId;
        this.coverImagePath = null;
    }

    public Truyen(int maTruyen, String tenTruyen, String moTa, String fileHash, boolean yeuThich, int tongThoiGianDoc, int imageResId, String coverImagePath, String note) {
        this.maTruyen = maTruyen;
        this.tenTruyen = tenTruyen;
        this.moTa = moTa;
        this.fileHash = fileHash;
        this.yeuThich = yeuThich;
        this.tongThoiGianDoc = tongThoiGianDoc;
        this.imageResId = imageResId;
        this.coverImagePath = coverImagePath;
        this.note = note;
    }
    public int getMaTruyen() { return maTruyen; }
    public String getTenTruyen() { return tenTruyen; }
    public String getMoTa() { return moTa; }
    public String getFileHash() { return fileHash; }
    public int getImageResId() { return imageResId; }
    public String getCoverImagePath() { return coverImagePath; }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
