package de.vdvcount.app.model;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.BuildConfig;
import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.common.Secret;
import de.vdvcount.app.remote.CountedStopTimeObject;
import de.vdvcount.app.remote.CountedTripObject;
import de.vdvcount.app.remote.PassengerCountingEventObject;
import de.vdvcount.app.remote.WayPointObject;

public class CountedTrip extends Trip implements ApiObjectMapper<CountedTripObject> {

    private String vcaVersion;
    private String deviceId;
    private String vehicleId;
    private int vehicleNumDoors;
    private List<CountedStopTime> countedStopTimes;
    private List<PassengerCountingEvent> unmatchedPassengerCountingEvents;
    private List<WayPoint> wayPoints;

    public CountedTrip() {
        this.countedStopTimes = new ArrayList<>();
        this.unmatchedPassengerCountingEvents = new ArrayList<>();
        this.wayPoints = new ArrayList<>();
    }

    public static CountedTrip from(Trip trip) {
        String vcaVersion = BuildConfig.VERSION_NAME;
        String deviceId = Secret.getSecretString(Secret.DEVICE_ID, null);

        CountedTrip countedTrip = new CountedTrip();
        countedTrip.setTripId(trip.getTripId());
        countedTrip.setLine(trip.getLine());
        countedTrip.setDirection(trip.getDirection());
        countedTrip.setHeadsign(trip.getHeadsign());
        countedTrip.setInternationalId(trip.getInternationalId());
        countedTrip.setOperationDay(trip.getOperationDay());
        countedTrip.setNextTripId(trip.getNextTripId());
        countedTrip.setVcaVersion(vcaVersion);
        countedTrip.setDeviceId(deviceId);
        countedTrip.setVehicleId(null);

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

    public String getVcaVersion() {
        return this.vcaVersion;
    }

    public void setVcaVersion(String vcaVersion) {
        this.vcaVersion = vcaVersion;
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

    public int getVehicleNumDoors() {
        return this.vehicleNumDoors;
    }

    public void setVehicleNumDoors(int vehicleNumDoors) {
        this.vehicleNumDoors = vehicleNumDoors;
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

    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

    @Override
    public CountedTripObject mapApiObject() {

        CountedTripObject apiObject = new CountedTripObject();
        apiObject.setTripId(this.getTripId());
        apiObject.setLine(this.getLine().mapApiObject());
        apiObject.setDirection(this.getDirection());
        apiObject.setHeadsign(this.getHeadsign());
        apiObject.setInternationalId(this.getInternationalId());
        apiObject.setOperationDay(this.getOperationDay());
        apiObject.setNextTripId(this.getNextTripId());
        apiObject.setVcaVersion(this.getVcaVersion());
        apiObject.setDeviceId(this.getDeviceId());
        apiObject.setVehicleId(this.getVehicleId());
        apiObject.setVehicleNumDoors(this.getVehicleNumDoors());

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

        List<WayPointObject> wayPoints = new ArrayList<>();
        for (WayPoint obj : this.getWayPoints()) {
            wayPoints.add(obj.mapApiObject());
        }
        apiObject.setWayPoints(wayPoints);

        return apiObject;
    }
}
