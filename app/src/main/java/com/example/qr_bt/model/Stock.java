package com.example.qr_bt.model;

public class Stock {
    private String name; // Nombre del elemento
    private String date; // Fecha de creación
    private String enlace; // Enlace codificado en el QR
    private String photo; // URL de la imagen del QR en Firebase Storage
    private String referencia; // Texto de referencia

    // Constructor vacío necesario para Firebase
    public Stock() {}

    // Constructor con todos los atributos
    public Stock(String name, String date, String enlace, String photo, String referencia) {
        this.name = name;
        this.date = date;
        this.enlace = enlace;
        this.photo = photo;
        this.referencia = referencia;
    }

    // Getters y setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getEnlace() { return enlace; }
    public void setEnlace(String enlace) { this.enlace = enlace; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}
