package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.WayPoint;

public class WayPointObject implements DomainModelMapper<WayPoint> {

    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("timestamp")
    private long timestamp;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public WayPoint mapDomainModel() {
        WayPoint domainModel = new WayPoint();
        domainModel.setLatitude(this.getLatitude());
        domainModel.setLongitude(this.getLongitude());
        domainModel.setTimestamp(new Date(this.getTimestamp() * 1000L));

        return domainModel;
    }
}
