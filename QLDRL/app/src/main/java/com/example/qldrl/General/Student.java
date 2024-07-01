package com.example.qldrl.General;

import java.io.Serializable;

public class Student implements Serializable {
  private String hsChucvu, hsHoTen, hsNgaySinh, hsGioiTinh, hsID, lhID, tkID;

    public Student(String hsChucvu, String hsHoTen, String hsNgaySinh, String hsGioiTinh, String hsID, String lhID, String tkID) {
        this.hsChucvu = hsChucvu;
        this.hsHoTen = hsHoTen;
        this.hsNgaySinh = hsNgaySinh;
        this.hsGioiTinh = hsGioiTinh;
        this.hsID = hsID;
        this.lhID = lhID;
        this.tkID = tkID;
    }

    public String getHsChucvu() {
        return hsChucvu;
    }

    public void setHsChucvu(String hsChucvu) {
        this.hsChucvu = hsChucvu;
    }

    public String getHsHoTen() {
        return hsHoTen;
    }

    public void setHsHoTen(String hsHoTen) {
        this.hsHoTen = hsHoTen;
    }

    public String getHsNgaySinh() {
        return hsNgaySinh;
    }

    public void setHsNgaySinh(String hsNgaySinh) {
        this.hsNgaySinh = hsNgaySinh;
    }

    public String getHsGioiTinh() {
        return hsGioiTinh;
    }

    public void setHsGioiTinh(String hsGioiTinh) {
        this.hsGioiTinh = hsGioiTinh;
    }

    public String getHsID() {
        return hsID;
    }

    public void setHsID(String hsID) {
        this.hsID = hsID;
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