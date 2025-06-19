package com.sinhvien.quanlitruyen.model;

public class Truyen {
    private int maTruyen;
    private String tenTruyen;
    private String moTa;
    private String fileHash;
    private int imageResId;

    private boolean yeuThich;
    private int tongThoiGianDoc;



    public Truyen(int maTruyen, String tenTruyen, String moTa, String fileHash, boolean yeuThich, int tongThoiGianDoc, int imageResId) {
        this.maTruyen = maTruyen;
        this.tenTruyen = tenTruyen;
        this.moTa = moTa;
        this.fileHash = fileHash;
        this.yeuThich = yeuThich;
        this.tongThoiGianDoc = tongThoiGianDoc;
        this.imageResId = imageResId;
    }
    public int getMaTruyen() { return maTruyen; }
    public String getTenTruyen() { return tenTruyen; }
    public String getMoTa() { return moTa; }
    public String getFileHash() { return fileHash; }

    public int getImageResId() {
        return imageResId;
    }
}
