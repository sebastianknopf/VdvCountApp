package de.vdvcount.app.model;

import java.util.Date;

import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.remote.StopTimeObject;

public class StopTime implements ApiObjectMapper<StopTimeObject> {

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

    @Override
    public StopTimeObject mapApiObject() {
        StopTimeObject apiObject = new StopTimeObject();
        apiObject.setArrivalTimestamp(this.getArrivalTimestamp().getTime() / 1000L);
        apiObject.setDepartureTimestamp(this.getDepartureTimestamp().getTime() / 1000L);
        apiObject.setSequence(this.getSequence());
        apiObject.setStop(this.getStop().mapApiObject());

        return apiObject;
    }
}
