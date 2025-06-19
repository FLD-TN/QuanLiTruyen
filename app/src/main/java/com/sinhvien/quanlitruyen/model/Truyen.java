package com.sinhvien.quanlitruyen.model;

public class Truyen {
    private int maTruyen;
    private String tenTruyen;
    private String moTa;
    private String fileHash;



    public Truyen(int maTruyen, String tenTruyen, String moTa, String fileHash) {
        this.maTruyen = maTruyen;
        this.tenTruyen = tenTruyen;
        this.moTa = moTa;
        this.fileHash = fileHash;
    }

    public int getMaTruyen() { return maTruyen; }
    public String getTenTruyen() { return tenTruyen; }
    public String getMoTa() { return moTa; }
    public String getFileHash() { return fileHash; }
}
