package de.vdvcount.app.remote;

import de.vdvcount.app.model.CountedTrip;

public class CountedTripObject extends TripObject {

    @Override
    public CountedTrip mapDomainModel() {
        CountedTrip domainModel = new CountedTrip();

        domainModel.setTripId(this.getTripId());
        domainModel.setLine(this.getLine().mapDomainModel());
        domainModel.setDirection(this.getDirection());
        domainModel.setHeadsign(this.getHeadsign());
        domainModel.setInternationalId(this.getInternationalId());
        domainModel.setNextTripId(this.getNextTripId());

        return domainModel;
    }
}
