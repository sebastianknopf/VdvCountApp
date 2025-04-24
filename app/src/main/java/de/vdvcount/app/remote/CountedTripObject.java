package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.PassengerCountingEvent;

public class CountedTripObject extends TripObject {

    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("vehicle_id")
    private String vehicleId;
    @SerializedName("counted_stop_times")
    private List<CountedStopTimeObject> countedStopTimes;
    @SerializedName("unmatched_passenger_counting_events")
    private List<PassengerCountingEventObject> unmatchedPassengerCountingEvents;

    public CountedTripObject() {
        this.countedStopTimes = new ArrayList<>();
        this.unmatchedPassengerCountingEvents = new ArrayList<>();
    }

    @Override
    public List<StopTimeObject> getStopTimes() {
        throw new RuntimeException("Method getStopTimes not available for CountedTripObject object!");
    }

    @Override
    public void setStopTimes(List<StopTimeObject> stopTimes) {
        throw new RuntimeException("Method setStopTimes not available for CountedTripObject object!");
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public List<CountedStopTimeObject> getCountedStopTimes() {
        return this.countedStopTimes;
    }

    public void setCountedStopTimes(List<CountedStopTimeObject> countedStopTimes) {
        this.countedStopTimes = countedStopTimes;
    }

    public List<PassengerCountingEventObject> getUnmatchedPassengerCountingEvents() {
        return this.unmatchedPassengerCountingEvents;
    }

    public void setUnmatchedPassengerCountingEvents(List<PassengerCountingEventObject> unmatchedPassengerCountingEvents) {
        this.unmatchedPassengerCountingEvents = unmatchedPassengerCountingEvents;
    }

    @Override
    public CountedTrip mapDomainModel() {
        CountedTrip domainModel = new CountedTrip();

        domainModel.setTripId(this.getTripId());
        domainModel.setLine(this.getLine().mapDomainModel());
        domainModel.setDirection(this.getDirection());
        domainModel.setHeadsign(this.getHeadsign());
        domainModel.setInternationalId(this.getInternationalId());
        domainModel.setOperationDay(this.getOperationDay());
        domainModel.setNextTripId(this.getNextTripId());
        domainModel.setDeviceId(this.getDeviceId());
        domainModel.setVehicleId(this.getVehicleId());

        List<CountedStopTime> countedStopTimes = new ArrayList<>();
        for (CountedStopTimeObject obj : this.getCountedStopTimes()) {
            countedStopTimes.add(obj.mapDomainModel());
        }
        domainModel.setCountedStopTimes(countedStopTimes);

        List<PassengerCountingEvent> unmatchedPassengerCountingEvents = new ArrayList<>();
        for (PassengerCountingEventObject obj : this.getUnmatchedPassengerCountingEvents()) {
            unmatchedPassengerCountingEvents.add(obj.mapDomainModel());
        }
        domainModel.setUnmatchedPassengerCountingEvents(unmatchedPassengerCountingEvents);

        return domainModel;
    }
}
