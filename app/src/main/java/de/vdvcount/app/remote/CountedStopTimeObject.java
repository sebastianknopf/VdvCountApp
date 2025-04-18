package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.PassengerCountingEvent;

public class CountedStopTimeObject extends StopTimeObject {

    @SerializedName("passenger_counting_events")
    private List<PassengerCountingEventObject> passengerCountingEvents;

    public CountedStopTimeObject() {
        this.passengerCountingEvents = new ArrayList<>();
    }

    public List<PassengerCountingEventObject> getPassengerCountingEvents() {
        return this.passengerCountingEvents;
    }

    public void setPassengerCountingEvents(List<PassengerCountingEventObject> passengerCountingEvents) {
        this.passengerCountingEvents = passengerCountingEvents;
    }

    @Override
    public CountedStopTime mapDomainModel() {
        CountedStopTime domainModel = new CountedStopTime();
        domainModel.setArrivalTimestamp(new Date(this.getArrivalTimestamp() * 1000L));
        domainModel.setDepartureTimestamp(new Date(this.getDepartureTimestamp() * 1000L));
        domainModel.setSequence(this.getSequence());
        domainModel.setStop(this.getStop().mapDomainModel());

        List<PassengerCountingEvent> passengerCountingEvents = new ArrayList<>();
        for (PassengerCountingEventObject obj : this.passengerCountingEvents) {
            passengerCountingEvents.add(obj.mapDomainModel());
        }
        domainModel.setPassengerCountingEvents(passengerCountingEvents);

        return domainModel;
    }
}
