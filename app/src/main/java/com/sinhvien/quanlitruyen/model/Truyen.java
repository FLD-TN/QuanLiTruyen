package com.sinhvien.quanlitruyen.model;

public class Truyen {
    private int maTruyen;
    private String tenTruyen;
    private String moTa;

    public Truyen(int maTruyen, String tenTruyen, String moTa) {
        this.maTruyen = maTruyen;
        this.tenTruyen = tenTruyen;
        this.moTa = moTa;
    }

    public int getMaTruyen() { return maTruyen; }
    public String getTenTruyen() { return tenTruyen; }
    public String getMoTa() { return moTa; }
}
