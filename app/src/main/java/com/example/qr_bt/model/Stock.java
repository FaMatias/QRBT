package com.example.qr_bt.model;

public class Stock  {
    String name, date, enlace, photo;
    Double referencia;
    public Stock(){}

    public Stock(String name, String date, String enlace, Double referencia, String photo) {
        this.name = name;
        this.date = date;
        this.enlace = enlace;
        this.referencia = referencia;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public Double getReferencia() {
        return referencia;
    }

    public void setReferencia(Double referencia) {
        this.referencia = referencia;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}