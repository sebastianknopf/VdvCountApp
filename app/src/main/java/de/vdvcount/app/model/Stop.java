package de.vdvcount.app.model;

import com.google.gson.annotations.SerializedName;

import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.remote.StopObject;
import de.vdvcount.app.remote.StopTimeObject;

public class Stop implements ApiObjectMapper<StopObject> {

    private String internationalId;
    private double latitude;
    private double longitude;
    private String name;
    private int parentId;
    private int stopId;

    public String getInternationalId() {
        return this.internationalId;
    }

    public void setInternationalId(String internationalId) {
        this.internationalId = internationalId;
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentId() {
        return this.parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getStopId() {
        return this.stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    @Override
    public StopObject mapApiObject() {
        StopObject apiObject = new StopObject();
        apiObject.setStopId(this.getStopId());
        apiObject.setLatitude(this.getLatitude());
        apiObject.setLongitude(this.getLongitude());
        apiObject.setInternationalId(this.getInternationalId());
        apiObject.setName(this.getName());
        apiObject.setParentId(this.getParentId());

        return apiObject;
    }
}
