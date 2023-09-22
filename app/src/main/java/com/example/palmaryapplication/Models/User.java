package com.example.palmaryapplication.Models;

public class User {
    private String Email;
    private String FullName;
    private String Phone;
    private String IMG;

    private String imgRef;

    public User(String email, String fullName, String phone, String IMG, String imgRef) {
        Email = email;
        FullName = fullName;
        Phone = phone;
        this.IMG = IMG;
        this.imgRef = imgRef;
    }

    public User(String email, String fullName, String phone) {
        Email = email;
        FullName = fullName;
        Phone = phone;
    }

    public User() {
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIMG() {
        return IMG;
    }

    public void setIMG(String IMG) {
        this.IMG = IMG;
    }

    public String getImgRef() {
        return imgRef;
    }

    public void setImgRef(String imgRef) {
        this.imgRef = imgRef;
    }
}
