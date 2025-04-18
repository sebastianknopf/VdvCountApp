package de.vdvcount.app.model;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.remote.CountedStopTimeObject;
import de.vdvcount.app.remote.PassengerCountingEventObject;

public class CountedStopTime extends StopTime {

    private List<PassengerCountingEvent> passengerCountingEvents;

    public CountedStopTime() {
        this.passengerCountingEvents = new ArrayList<>();
    }

    public static CountedStopTime from(StopTime stopTime) {
        CountedStopTime countedStopTime = new CountedStopTime();
        countedStopTime.setArrivalTimestamp(stopTime.getArrivalTimestamp());
        countedStopTime.setDepartureTimestamp(stopTime.getDepartureTimestamp());
        countedStopTime.setSequence(stopTime.getSequence());
        countedStopTime.setStop(stopTime.getStop());

        return countedStopTime;
    }

    public List<PassengerCountingEvent> getPassengerCountingEvents() {
        return this.passengerCountingEvents;
    }

    public void setPassengerCountingEvents(List<PassengerCountingEvent> passengerCountingEvents) {
        this.passengerCountingEvents = passengerCountingEvents;
    }

    @Override
    public CountedStopTimeObject mapApiObject() {
        CountedStopTimeObject apiObject = new CountedStopTimeObject();
        apiObject.setArrivalTimestamp(this.getArrivalTimestamp().getTime() / 1000L);
        apiObject.setDepartureTimestamp(this.getDepartureTimestamp().getTime() / 1000L);
        apiObject.setSequence(this.getSequence());
        apiObject.setStop(this.getStop().mapApiObject());

        List<PassengerCountingEventObject> passengerCountingEvents = new ArrayList<>();
        for (PassengerCountingEvent obj : this.passengerCountingEvents) {
            passengerCountingEvents.add(obj.mapApiObject());
        }
        apiObject.setPassengerCountingEvents(passengerCountingEvents);

        return apiObject;
    }
}
