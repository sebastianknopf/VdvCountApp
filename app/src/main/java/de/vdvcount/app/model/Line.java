package de.vdvcount.app.model;

import com.google.gson.annotations.SerializedName;

public class Line {

    private int id;
    private String name;
    private String internationalId;

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

    public String getInternationalId() {
        return this.internationalId;
    }

    public void setInternationalId(String internationalId) {
        this.internationalId = internationalId;
    }
}
