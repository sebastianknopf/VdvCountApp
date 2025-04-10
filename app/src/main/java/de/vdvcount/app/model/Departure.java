package de.vdvcount.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Departure {

    private int stopId;
    private Line line;
    private int tripId;
    private int direction;
    private String headsign;
    private String internationalId;
    private Date arrivalTimestamp;
    private Date departureTimestamp;
    private int sequence;

    public int getStopId() {
        return this.stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public Line getLine() {
        return this.line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public int getTripId() {
        return this.tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getHeadsign() {
        return this.headsign;
    }

    public void setHeadsign(String headsign) {
        this.headsign = headsign;
    }

    public String getInternationalId() {
        return this.internationalId;
    }

    public void setInternationalId(String internationalId) {
        this.internationalId = internationalId;
    }

    public Date getArrivalTimestamp() {
        return this.arrivalTimestamp;
    }

    public void setArrivalTimestamp(Date arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    public Date getDepartureTimestamp() {
        return this.departureTimestamp;
    }

    public void setDepartureTimestamp(Date departureTimestamp) {
        this.departureTimestamp = departureTimestamp;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
