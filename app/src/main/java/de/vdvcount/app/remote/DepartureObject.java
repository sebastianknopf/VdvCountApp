package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.Departure;

class DepartureObject implements DomainModelMapper<Departure> {

    @SerializedName("stop_id")
    private int stopId;
    @SerializedName("line")
    private LineObject line;
    @SerializedName("trip_id")
    private int tripId;
    @SerializedName("direction")
    private int direction;
    @SerializedName("headsign")
    private String headsign;
    @SerializedName("international_id")
    private String internationalId;
    @SerializedName("arrival_timestamp")
    private int arrivalTimestamp;
    @SerializedName("departure_timestamp")
    private int departureTimestamp;
    @SerializedName("sequence")
    private int sequence;

    public int getStopId() {
        return this.stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public LineObject getLine() {
        return this.line;
    }

    public void setLine(LineObject line) {
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

    public int getArrivalTimestamp() {
        return this.arrivalTimestamp;
    }

    public void setArrivalTimestamp(int arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    public int getDepartureTimestamp() {
        return this.departureTimestamp;
    }

    public void setDepartureTimestamp(int departureTimestamp) {
        this.departureTimestamp = departureTimestamp;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public Departure mapDomainModel() {
        Departure domainModel = new Departure();

        domainModel.setStopId(this.getStopId());
        domainModel.setLine(this.getLine().mapDomainModel());
        domainModel.setTripId(this.getTripId());
        domainModel.setDirection(this.getDirection());
        domainModel.setHeadsign(this.getHeadsign());
        domainModel.setInternationalId(this.getInternationalId());
        domainModel.setArrivalTimestamp(new Date(this.getArrivalTimestamp() * 1000L));
        domainModel.setDepartureTimestamp(new Date(this.getDepartureTimestamp() * 1000L));
        domainModel.setSequence(this.getSequence());

        return domainModel;
    }
}
