package com.example.palmaryapplication.Models;

public class Cart {
    private String ID;
    private Product product;
    private String Quantity;
    private String Modifications;

    public Cart(String ID, Product product, String quantity, String modifications) {
        this.ID = ID;
        this.product = product;
        Quantity = quantity;
        Modifications = modifications;
    }

    public Cart() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getModifications() {
        return Modifications;
    }

    public void setModifications(String modifications) {
        Modifications = modifications;
    }
}
