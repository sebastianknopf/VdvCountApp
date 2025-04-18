package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.CountingSequence;
import de.vdvcount.app.model.PassengerCountingEvent;

public class PassengerCountingEventObject implements DomainModelMapper<PassengerCountingEvent> {

    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("counting_sequences")
    private List<CountingSequenceObject> countingSequences;

    public PassengerCountingEventObject() {
        this.countingSequences = new ArrayList<>();
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<CountingSequenceObject> getCountingSequences() {
        return this.countingSequences;
    }

    public void setCountingSequences(List<CountingSequenceObject> countingSequences) {
        this.countingSequences = countingSequences;
    }

    @Override
    public PassengerCountingEvent mapDomainModel() {
        PassengerCountingEvent domainModel = new PassengerCountingEvent();
        domainModel.setLatitude(this.getLatitude());
        domainModel.setLongitude(this.getLongitude());

        List<CountingSequence> countingSequences = new ArrayList<>();
        for (CountingSequenceObject obj : this.getCountingSequences()) {
            countingSequences.add(obj.mapDomainModel());
        }
        domainModel.setCountingSequences(countingSequences);

        return domainModel;
    }
}
