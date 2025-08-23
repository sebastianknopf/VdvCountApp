package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.PassengerCountingEvent;
import de.vdvcount.app.model.WayPoint;

public class CountedTripObject extends TripObject {

    @SerializedName("vca_version")
    private String vcaVersion;
    @SerializedName("device_id")
    private String deviceId;
    @SerializedName("vehicle_id")
    private String vehicleId;
    @SerializedName("vehicle_num_doors")
    private int vehicleNumDoors;
    @SerializedName("counted_stop_times")
    private List<CountedStopTimeObject> countedStopTimes;
    @SerializedName("unmatched_passenger_counting_events")
    private List<PassengerCountingEventObject> unmatchedPassengerCountingEvents;
    @SerializedName("way_points")
    private List<WayPointObject> wayPoints;

    public CountedTripObject() {
        this.countedStopTimes = new ArrayList<>();
        this.unmatchedPassengerCountingEvents = new ArrayList<>();
        this.wayPoints = new ArrayList<>();
    }

    @Override
    public List<StopTimeObject> getStopTimes() {
        throw new RuntimeException("Method getStopTimes not available for CountedTripObject object!");
    }

    @Override
    public void setStopTimes(List<StopTimeObject> stopTimes) {
        throw new RuntimeException("Method setStopTimes not available for CountedTripObject object!");
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

    public List<WayPointObject> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(List<WayPointObject> wayPoints) {
        this.wayPoints = wayPoints;
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
        domainModel.setVcaVersion(this.getVcaVersion());
        domainModel.setDeviceId(this.getDeviceId());
        domainModel.setVehicleId(this.getVehicleId());
        domainModel.setVehicleNumDoors(this.getVehicleNumDoors());

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

        List<WayPoint> wayPoints = new ArrayList<>();
        for (WayPointObject obj : this.getWayPoints()) {
            wayPoints.add(obj.mapDomainModel());
        }
        domainModel.setWayPoints(wayPoints);

        return domainModel;
    }
}
