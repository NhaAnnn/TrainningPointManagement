package com.example.qldrl.General;

import java.io.Serializable;

public class Account implements Serializable {
  private  String tkID, tkTenTK, tkNgaySinh, tkMatKhau, tkHoTen, tkChucVu;

    public Account(String tkID, String tkTenTK, String tkNgaySinh, String tkMatKhau, String tkHoTen, String tkChucVu) {
        this.tkID = tkID;
        this.tkTenTK = tkTenTK;
        this.tkNgaySinh = tkNgaySinh;
        this.tkMatKhau = tkMatKhau;
        this.tkHoTen = tkHoTen;
        this.tkChucVu = tkChucVu;
    }

    public String getTkID() {
        return tkID;
    }

    public void setTkID(String tkID) {
        this.tkID = tkID;
    }

    public String getTkTenTK() {
        return tkTenTK;
    }

    public void setTkTenTK(String tkTenTK) {
        this.tkTenTK = tkTenTK;
    }

    public String getTkNgaySinh() {
        return tkNgaySinh;
    }

    public void setTkNgaySinh(String tkNgaySinh) {
        this.tkNgaySinh = tkNgaySinh;
    }

    public String getTkMatKhau() {
        return tkMatKhau;
    }

    public void setTkMatKhau(String tkMatKhau) {
        this.tkMatKhau = tkMatKhau;
    }

    public String getTkHoTen() {
        return tkHoTen;
    }

    public void setTkHoTen(String tkHoTen) {
        this.tkHoTen = tkHoTen;
    }

    public String getTkChucVu() {
        return tkChucVu;
    }

    public void setTkChucVu(String tkChucVu) {
        this.tkChucVu = tkChucVu;
    }
}
