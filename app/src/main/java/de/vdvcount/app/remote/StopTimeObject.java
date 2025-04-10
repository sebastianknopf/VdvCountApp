package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.StopTime;

class StopTimeObject implements DomainModelMapper<StopTime> {

    @SerializedName("arrival_timestamp")
    private long arrivalTimestamp;
    @SerializedName("departure_timestamp")
    private long departureTimestamp;
    @SerializedName("sequence")
    private int sequence;
    @SerializedName("stop")
    private StopObject stop;

    public long getArrivalTimestamp() {
        return arrivalTimestamp;
    }

    public void setArrivalTimestamp(long arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    public long getDepartureTimestamp() {
        return departureTimestamp;
    }

    public void setDepartureTimestamp(long departureTimestamp) {
        this.departureTimestamp = departureTimestamp;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public StopObject getStop() {
        return stop;
    }

    public void setStop(StopObject stop) {
        this.stop = stop;
    }

    @Override
    public StopTime mapDomainModel() {
        StopTime domainModel = new StopTime();

        domainModel.setArrivalTimestamp(new Date(this.getArrivalTimestamp() * 1000L));
        domainModel.setDepartureTimestamp(new Date(this.getDepartureTimestamp() * 1000L));
        domainModel.setSequence(this.getSequence());
        domainModel.setStop(this.getStop().mapDomainModel());

        return domainModel;
    }
}
