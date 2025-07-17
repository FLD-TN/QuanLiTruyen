package com.sinhvien.quanlitruyen.model;

public class Truyen {
    private int maTruyen;
    private String tenTruyen;
    private String moTa;
    private String coverImagePath;
    private String fileHash;
    private boolean isFavorite;
    private int soChuong;
    private int maTheLoai;
    private String note;

    // Constructor không tham số
    public Truyen() {
    }

    // Constructor đầy đủ
    public Truyen(int maTruyen, String tenTruyen, String moTa, String coverImagePath, String fileHash, boolean isFavorite, int soChuong, int maTheLoai, String note) {
        this.maTruyen = maTruyen;
        this.tenTruyen = tenTruyen;
        this.moTa = moTa;
        this.coverImagePath = coverImagePath;
        this.fileHash = fileHash;
        this.isFavorite = isFavorite;
        this.soChuong = soChuong;
        this.maTheLoai = maTheLoai;
        this.note = note;
    }

    // Getter và setter
    public int getMaTruyen() { return maTruyen; }
    public void setMaTruyen(int maTruyen) { this.maTruyen = maTruyen; }
    public String getTenTruyen() { return tenTruyen; }
    public void setTenTruyen(String tenTruyen) { this.tenTruyen = tenTruyen; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public String getCoverImagePath() { return coverImagePath; }
    public void setCoverImagePath(String coverImagePath) { this.coverImagePath = coverImagePath; }
    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public int getSoChuong() { return soChuong; }
    public void setSoChuong(int soChuong) { this.soChuong = soChuong; }
    public int getMaTheLoai() { return maTheLoai; }
    public void setMaTheLoai(int maTheLoai) { this.maTheLoai = maTheLoai; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}