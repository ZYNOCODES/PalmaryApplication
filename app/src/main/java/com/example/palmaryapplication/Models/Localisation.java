package com.example.palmaryapplication.Models;

public class Localisation {
    private String Address;
    private String city;
    private String Lantitude;
    private String Longitude;

    public Localisation(String address, String city, String lantitude, String longitude) {
        Address = address;
        this.city = city;
        Lantitude = lantitude;
        Longitude = longitude;
    }

    public Localisation() {
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLantitude() {
        return Lantitude;
    }

    public void setLantitude(String lantitude) {
        Lantitude = lantitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
