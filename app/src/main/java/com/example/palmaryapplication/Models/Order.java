package com.example.palmaryapplication.Models;

import java.util.ArrayList;

public class Order {
    private String ID;
    private String Type;
    private Localisation Location;
    private String LocationNotes;
    private String Price;
    private String ClientID;
    private Boolean Confirmation;
    private ArrayList<Cart> Products;

    public Order(String ID, String type, Localisation location, String locationNotes, String price, String clientID, Boolean confirmation, ArrayList<Cart> products) {
        this.ID = ID;
        Type = type;
        Location = location;
        LocationNotes = locationNotes;
        Price = price;
        ClientID = clientID;
        Confirmation = confirmation;
        Products = products;
    }

    public Order(String ID, String type, String price, String clientID, Boolean confirmation, ArrayList<Cart> products) {
        this.ID = ID;
        Type = type;
        Price = price;
        ClientID = clientID;
        Confirmation = confirmation;
        Products = products;
    }

    public Order(String ID, String type, String price, Boolean confirmation) {
        this.ID = ID;
        Type = type;
        Price = price;
        Confirmation = confirmation;
    }

    public Order() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Localisation getLocation() {
        return Location;
    }

    public void setLocation(Localisation location) {
        Location = location;
    }

    public String getLocationNotes() {
        return LocationNotes;
    }

    public void setLocationNotes(String locationNotes) {
        LocationNotes = locationNotes;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public Boolean getConfirmation() {
        return Confirmation;
    }

    public void setConfirmation(Boolean confirmation) {
        Confirmation = confirmation;
    }

    public ArrayList<Cart> getProducts() {
        return Products;
    }

    public void setProducts(ArrayList<Cart> products) {
        Products = products;
    }
}
