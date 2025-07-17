package com.sinhvien.quanlitruyen.model;

public class ReadingHistory {
    private int historyId;
    private int maTruyen;
    private String tenTruyen;
    private String readTimestamp;

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getMaTruyen() {
        return maTruyen;
    }

    public void setMaTruyen(int maTruyen) {
        this.maTruyen = maTruyen;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }

    public void setTenTruyen(String tenTruyen) {
        this.tenTruyen = tenTruyen;
    }

    public String getReadTimestamp() {
        return readTimestamp;
    }

    public void setReadTimestamp(String readTimestamp) {
        this.readTimestamp = readTimestamp;
    }
}