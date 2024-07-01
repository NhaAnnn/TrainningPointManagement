package com.example.qldrl.Mistake;

public class MistakeType {
    String lvpID, lvpTen;

    public MistakeType(String lvpID, String lvpTen) {
        this.lvpID = lvpID;
        this.lvpTen = lvpTen;
    }

    public String getLvpID() {
        return lvpID;
    }

    public void setLvpID(String lvpID) {
        this.lvpID = lvpID;
    }

    public String getLvpTen() {
        return lvpTen;
    }

    public void setLvpTen(String lvpTen) {
        this.lvpTen = lvpTen;
    }
}
