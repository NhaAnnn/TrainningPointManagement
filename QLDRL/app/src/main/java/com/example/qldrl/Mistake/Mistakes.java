package com.example.qldrl.Mistake;

public class Mistakes {
    String hsID, vpID, ltvpID, tkID, ltvpThoiGian;

    public Mistakes(String hsID, String vpID, String ltvpID, String tkID, String ltvpThoiGian) {
        this.hsID = hsID;
        this.vpID = vpID;
        this.ltvpID = ltvpID;
        this.tkID = tkID;
        this.ltvpThoiGian = ltvpThoiGian;
    }

    public String getHsID() {
        return hsID;
    }

    public void setHsID(String hsID) {
        this.hsID = hsID;
    }

    public String getVpID() {
        return vpID;
    }

    public void setVpID(String vpID) {
        this.vpID = vpID;
    }

    public String getLtvpID() {
        return ltvpID;
    }

    public void setLtvpID(String ltvpID) {
        this.ltvpID = ltvpID;
    }

    public String getTkID() {
        return tkID;
    }

    public void setTkID(String tkID) {
        this.tkID = tkID;
    }

    public String getLtvpThoiGian() {
        return ltvpThoiGian;
    }

    public void setLtvpThoiGian(String ltvpThoiGian) {
        this.ltvpThoiGian = ltvpThoiGian;
    }
}
