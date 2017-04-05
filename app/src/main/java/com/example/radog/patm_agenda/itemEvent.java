package com.example.radog.patm_agenda;

/**
 * Created by radog on 03/04/2017.
 */

public class itemEvent {

    private int picture;
    private String nameE, descE, dateE;

    public itemEvent(int picture, String nameE, String descE, String dateE) {
        this.picture = picture;
        this.nameE = nameE;
        this.descE = descE;
        this.dateE = dateE;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public String getNameE() {
        return nameE;
    }

    public void setNameE(String nameE) {
        this.nameE = nameE;
    }

    public String getDescE() {
        return descE;
    }

    public void setDescE(String descE) {
        this.descE = descE;
    }

    public String getDateE() {
        return dateE;
    }

    public void setDateE(String dateE) {
        this.dateE = dateE;
    }
}
