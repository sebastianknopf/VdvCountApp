package de.vdvcount.app.model;

import java.util.Date;

public class StopTime {

    private Date arrivalTimestamp;
    private Date departureTimestamp;
    private int sequence;
    private Stop stop;

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

    public Stop getStop() {
        return this.stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }
}
