package com.example.qldrl.Mistake;

import java.io.Serializable;

public class Mistake implements Serializable {
    private String nameMistake, lvpid, vpID;
    private String vpDiemtru;


    public Mistake(String nameMistake, String lvpid, String vpID, String vpDiemtru) {
        this.nameMistake = nameMistake;
        this.lvpid = lvpid;
        this.vpID = vpID;
        this.vpDiemtru = vpDiemtru;
    }

    public String getNameMistake() {
        return nameMistake;
    }

    public void setNameMistake(String nameMistake) {
        this.nameMistake = nameMistake;
    }

    public String getLvpid() {
        return lvpid;
    }

    public void setLvpid(String lvpid) {
        this.lvpid = lvpid;
    }

    public String getVpID() {
        return vpID;
    }

    public void setVpID(String vpID) {
        this.vpID = vpID;
    }

    public String getVpDiemtru() {
        return vpDiemtru;
    }

    public void setVpDiemtru(String vpDiemtru) {
        this.vpDiemtru = vpDiemtru;
    }
}
