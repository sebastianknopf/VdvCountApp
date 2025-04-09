package de.vdvcount.app.model;

import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.remote.CountedTripObject;

public class CountedTrip extends Trip implements ApiObjectMapper<CountedTripObject> {
    @Override
    public CountedTripObject mapApiObject() {
        CountedTripObject apiObject = new CountedTripObject();

        // add all elements of static trip object
        apiObject.setTripId(this.getTripId());
        apiObject.setLine(this.getLine().mapApiObject());
        apiObject.setDirection(this.getDirection());
        apiObject.setHeadsign(this.getHeadsign());
        apiObject.setInternationalId(this.getInternationalId());
        apiObject.setNextTripId(this.getNextTripId());

        // nominal stop times are NOT mapped backwards
        // instead counting sequences are mapped in future

        // TODO: add mapping for counting sequences

        return apiObject;
    }
}
