package de.vdvcount.app.model;

import java.util.ArrayList;
import java.util.List;

public class AdditionalStop implements ICountable {

    private PassengerCountingEvent passengerCountingEvent;

    public AdditionalStop() {
    }

    public void setPassengerCountingEvent(PassengerCountingEvent passengerCountingEvent) {
        this.passengerCountingEvent = passengerCountingEvent;
    }

    @Override
    public List<PassengerCountingEvent> getPassengerCountingEvents() {
        List<PassengerCountingEvent> result = new ArrayList<>();
        result.add(this.passengerCountingEvent);

        return result;
    }
}
