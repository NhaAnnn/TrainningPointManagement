package com.example.qldrl.Mistake;

public class ClassRom {
   private String lhID, lhTen, lhGVCN,nkNienKhoa;

    public ClassRom(String lhID, String lhTen, String lhGVCN, String nkNienKhoa) {
        this.lhID = lhID;
        this.lhTen = lhTen;
        this.lhGVCN = lhGVCN;
        this.nkNienKhoa = nkNienKhoa;
    }

    public String getLhID() {
        return lhID;
    }

    public void setLhID(String lhID) {
        this.lhID = lhID;
    }

    public String getLhTen() {
        return lhTen;
    }

    public void setLhTen(String lhTen) {
        this.lhTen = lhTen;
    }

    public String getLhGVCN() {
        return lhGVCN;
    }

    public void setLhGVCN(String lhGVCN) {
        this.lhGVCN = lhGVCN;
    }

    public String getNkNienKhoa() {
        return nkNienKhoa;
    }

    public void setNkNienKhoa(String nkNienKhoa) {
        this.nkNienKhoa = nkNienKhoa;
    }
}
