package de.vdvcount.app.model;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.remote.CountedStopTimeObject;
import de.vdvcount.app.remote.CountedTripObject;
import de.vdvcount.app.remote.PassengerCountingEventObject;

public class CountedTrip extends Trip implements ApiObjectMapper<CountedTripObject> {

    private String vehicleId;
    private List<CountedStopTime> countedStopTimes;
    private List<PassengerCountingEvent> unmatchedPassengerCountingEvents;

    public CountedTrip() {
        this.countedStopTimes = new ArrayList<>();
        this.unmatchedPassengerCountingEvents = new ArrayList<>();
    }

    public static CountedTrip from(Trip trip) {
        CountedTrip countedTrip = new CountedTrip();
        countedTrip.setTripId(trip.getTripId());
        countedTrip.setLine(trip.getLine());
        countedTrip.setDirection(trip.getDirection());
        countedTrip.setHeadsign(trip.getHeadsign());
        countedTrip.setInternationalId(trip.getInternationalId());
        countedTrip.setNextTripId(trip.getNextTripId());

        for (StopTime stopTime : trip.getStopTimes()) {
            countedTrip.getCountedStopTimes().add(CountedStopTime.from(stopTime));
        }

        return countedTrip;
    }

    @Override
    public List<StopTime> getStopTimes() {
        throw new RuntimeException("Method getStopTimes not available for CountedTrip object!");
    }

    @Override
    public void setStopTimes(List<StopTime> stopTimes) {
        throw new RuntimeException("Method setStopTimes not available for CountedTrip object!");
    }

    public String getVehicleId() {
        return this.vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public List<CountedStopTime> getCountedStopTimes() {
        return this.countedStopTimes;
    }

    public void setCountedStopTimes(List<CountedStopTime> countedStopTimes) {
        this.countedStopTimes = countedStopTimes;
    }

    public List<PassengerCountingEvent> getUnmatchedPassengerCountingEvents() {
        return this.unmatchedPassengerCountingEvents;
    }

    public void setUnmatchedPassengerCountingEvents(List<PassengerCountingEvent> unmatchedPassengerCountingEvents) {
        this.unmatchedPassengerCountingEvents = unmatchedPassengerCountingEvents;
    }

    @Override
    public CountedTripObject mapApiObject() {
        CountedTripObject apiObject = new CountedTripObject();
        apiObject.setTripId(this.getTripId());
        apiObject.setLine(this.getLine().mapApiObject());
        apiObject.setDirection(this.getDirection());
        apiObject.setHeadsign(this.getHeadsign());
        apiObject.setInternationalId(this.getInternationalId());
        apiObject.setNextTripId(this.getNextTripId());
        apiObject.setVehicleId(this.getVehicleId());

        List<CountedStopTimeObject> countedStopTimes = new ArrayList<>();
        for (CountedStopTime obj : this.getCountedStopTimes()) {
            countedStopTimes.add(obj.mapApiObject());
        }
        apiObject.setCountedStopTimes(countedStopTimes);

        List<PassengerCountingEventObject> unmatchedPassengerCountingEvents = new ArrayList<>();
        for (PassengerCountingEvent obj : this.getUnmatchedPassengerCountingEvents()) {
            unmatchedPassengerCountingEvents.add(obj.mapApiObject());
        }
        apiObject.setUnmatchedPassengerCountingEvents(unmatchedPassengerCountingEvents);

        return apiObject;
    }
}
