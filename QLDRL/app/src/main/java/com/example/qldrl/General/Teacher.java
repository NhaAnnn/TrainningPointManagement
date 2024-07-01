package com.example.qldrl.General;

import java.io.Serializable;

public class Teacher implements Serializable {
    private String gvHoTen, gvID, gvNgaySing, gvGioiTinh, lhID, tkID;

    public Teacher(String gvHoTen, String gvID, String gvNgaySing, String gvGioiTinh, String lhID, String tkID) {
        this.gvHoTen = gvHoTen;
        this.gvID = gvID;
        this.gvNgaySing = gvNgaySing;
        this.gvGioiTinh = gvGioiTinh;
        this.lhID = lhID;
        this.tkID = tkID;
    }

    public String getGvHoTen() {
        return gvHoTen;
    }

    public void setGvHoTen(String gvHoTen) {
        this.gvHoTen = gvHoTen;
    }

    public String getGvID() {
        return gvID;
    }

    public void setGvID(String gvID) {
        this.gvID = gvID;
    }

    public String getGvNgaySing() {
        return gvNgaySing;
    }

    public void setGvNgaySing(String gvNgaySing) {
        this.gvNgaySing = gvNgaySing;
    }

    public String getGvGioiTinh() {
        return gvGioiTinh;
    }

    public void setGvGioiTinh(String gvGioiTinh) {
        this.gvGioiTinh = gvGioiTinh;
    }

    public String getLhID() {
        return lhID;
    }

    public void setLhID(String lhID) {
        this.lhID = lhID;
    }

    public String getTkID() {
        return tkID;
    }

    public void setTkID(String tkID) {
        this.tkID = tkID;
    }
}
