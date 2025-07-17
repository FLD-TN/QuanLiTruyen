package com.sinhvien.quanlitruyen.model;

public class TheLoai {
    private int maTheLoai;
    private String tenTheLoai;

    // Constructor không tham số
    public TheLoai() {
    }

    // Constructor đầy đủ
    public TheLoai(int maTheLoai, String tenTheLoai) {
        this.maTheLoai = maTheLoai;
        this.tenTheLoai = tenTheLoai;
    }

    // Getter và setter
    public int getMaTheLoai() { return maTheLoai; }
    public void setMaTheLoai(int maTheLoai) { this.maTheLoai = maTheLoai; }
    public String getTenTheLoai() { return tenTheLoai; }
    public void setTenTheLoai(String tenTheLoai) { this.tenTheLoai = tenTheLoai; }
}