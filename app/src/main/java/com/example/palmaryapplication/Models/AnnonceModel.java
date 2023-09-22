package com.example.palmaryapplication.Models;

public class AnnonceModel {
    private int IMG;
    private String Title;

    public AnnonceModel(int IMG, String title) {
        this.IMG = IMG;
        Title = title;
    }

    public AnnonceModel() {
    }

    public int getIMG() {
        return IMG;
    }

    public void setIMG(int IMG) {
        this.IMG = IMG;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
