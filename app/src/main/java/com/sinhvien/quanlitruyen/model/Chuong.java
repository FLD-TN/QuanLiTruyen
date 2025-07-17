package com.sinhvien.quanlitruyen.model;

public class Chuong {
    private int maChuong;
    private int maTruyen;
    private String tenChuong;
    private int soChuong;

    // Constructor không tham số
    public Chuong() {
    }

    // Constructor đầy đủ
    public Chuong(int maChuong, int maTruyen, String tenChuong, int soChuong) {
        this.maChuong = maChuong;
        this.maTruyen = maTruyen;
        this.tenChuong = tenChuong;
        this.soChuong = soChuong;
    }

    // Getter
    public int getMaChuong() { return maChuong; }
    public int getMaTruyen() { return maTruyen; }
    public String getTenChuong() { return tenChuong; }
    public int getSoChuong() { return soChuong; }

    // Setter
    public void setMaChuong(int maChuong) { this.maChuong = maChuong; }
    public void setMaTruyen(int maTruyen) { this.maTruyen = maTruyen; }
    public void setTenChuong(String tenChuong) { this.tenChuong = tenChuong; }
    public void setSoChuong(int soChuong) { this.soChuong = soChuong; }
}