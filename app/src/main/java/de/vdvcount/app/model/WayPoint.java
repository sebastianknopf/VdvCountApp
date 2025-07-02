package de.vdvcount.app.model;

import java.util.Date;

import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.remote.WayPointObject;

public class WayPoint implements ApiObjectMapper<WayPointObject> {
    private double latitude;
    private double longitude;
    private Date timestamp;

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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public WayPointObject mapApiObject() {
        WayPointObject apiObject = new WayPointObject();
        apiObject.setLatitude(this.getLatitude());
        apiObject.setLongitude(this.getLongitude());
        apiObject.setTimestamp(this.getTimestamp().getTime() / 1000L);

        return apiObject;
    }
}
