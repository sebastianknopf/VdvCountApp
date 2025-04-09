package de.vdvcount.app.model;

import java.util.List;

public class Trip {

    private int tripId;
    private Line line;
    private int direction;
    private String headsign;
    private String internationalId;
    private int nextTripId;
    private List<StopTime> stopTimes;

    public int getTripId() {
        return this.tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public Line getLine() {
        return this.line;
    }

    public void setLine(Line line) {
        this.line = line;
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

    public int getNextTripId() {
        return this.nextTripId;
    }

    public void setNextTripId(int nextTripId) {
        this.nextTripId = nextTripId;
    }

    public List<StopTime> getStopTimes() {
        return this.stopTimes;
    }

    public void setStopTimes(List<StopTime> stopTimes) {
        this.stopTimes = stopTimes;
    }
}
