package com.sinhvien.quanlitruyen.model;

public class Chuong {
    private int maChuong;
    private int maTruyen;
    private String tenChuong;
    private int soChuong;

    public Chuong(int maChuong, int maTruyen, String tenChuong, int soChuong) {
        this.maChuong = maChuong;
        this.maTruyen = maTruyen;
        this.tenChuong = tenChuong;
        this.soChuong = soChuong;
    }

    public int getMaChuong() { return maChuong; }
    public int getMaTruyen() { return maTruyen; }
    public String getTenChuong() { return tenChuong; }
    public int getSoChuong() { return soChuong; }
}
