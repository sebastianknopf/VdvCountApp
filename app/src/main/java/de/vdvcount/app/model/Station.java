package de.vdvcount.app.model;

import com.google.gson.annotations.SerializedName;

public class Station {

    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private double similarity;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSimilarity() {
        return this.similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }
}
