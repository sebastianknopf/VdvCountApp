package de.vdvcount.app.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.remote.CountingSequenceObject;
import de.vdvcount.app.remote.PassengerCountingEventObject;

public class PassengerCountingEvent implements ApiObjectMapper<PassengerCountingEventObject> {

    private double latitude;
    private double longitude;
    private int afterStopSequence;
    private List<CountingSequence> countingSequences;

    public PassengerCountingEvent() {
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

    public int getAfterStopSequence() {
        return this.afterStopSequence;
    }

    public void setAfterStopSequence(int afterStopSequence) {
        this.afterStopSequence = afterStopSequence;
    }

    public List<CountingSequence> getCountingSequences() {
        return this.countingSequences;
    }

    public void setCountingSequences(List<CountingSequence> countingSequences) {
        this.countingSequences = countingSequences;
    }

    public int getIn() {
        int sum = 0;
        for (CountingSequence countingSequence : this.getCountingSequences()) {
            sum += countingSequence.getIn();
        }

        return sum;
    }

    public int getOut() {
        int sum = 0;
        for (CountingSequence countingSequence : this.getCountingSequences()) {
            sum += countingSequence.getOut();
        }

        return sum;
    }

    @Override
    public PassengerCountingEventObject mapApiObject() {
        PassengerCountingEventObject apiObject = new PassengerCountingEventObject();
        apiObject.setLatitude(this.getLatitude());
        apiObject.setLongitude(this.getLongitude());
        apiObject.setAfterStopSequence(this.getAfterStopSequence());

        List<CountingSequenceObject> countingSequences = new ArrayList<>();
        for (CountingSequence obj : this.getCountingSequences()) {
            countingSequences.add(obj.mapApiObject());
        }
        apiObject.setCountingSequences(countingSequences);

        return apiObject;
    }
}
