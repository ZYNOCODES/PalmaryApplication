package com.example.palmaryapplication.Models;

public class Product {
    private String ID;
    private String Name;
    private String Price;
    private String Ingredients;
    private String Category;
    private String Description;
    private String IMG;
    private String imgRef;
    private Boolean Annonce;

    public Product(String ID, String name, String price, String ingredients, String category, String description, String IMG, Boolean annonce) {
        this.ID = ID;
        Name = name;
        Price = price;
        Ingredients = ingredients;
        Category = category;
        Description = description;
        this.IMG = IMG;
        Annonce = annonce;
    }

    public Product(String ID, String name, String price, String ingredients, String category, String description, String IMG, String imgRef, Boolean annonce) {
        this.ID = ID;
        Name = name;
        Price = price;
        Ingredients = ingredients;
        Category = category;
        Description = description;
        this.IMG = IMG;
        this.imgRef = imgRef;
        Annonce = annonce;
    }

    public Product() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getIngredients() {
        return Ingredients;
    }

    public void setIngredients(String ingredients) {
        Ingredients = ingredients;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getIMG() {
        return IMG;
    }

    public void setIMG(String IMG) {
        this.IMG = IMG;
    }

    public Boolean getAnnonce() {
        return Annonce;
    }

    public void setAnnonce(Boolean annonce) {
        Annonce = annonce;
    }

    public String getImgRef() {
        return imgRef;
    }

    public void setImgRef(String imgRef) {
        this.imgRef = imgRef;
    }
}

