package de.vdvcount.app.model;

import java.util.Date;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.remote.CountingSequenceObject;

public class CountingSequence implements ApiObjectMapper<CountingSequenceObject> {

    private Date countBeginTimestamp;
    private Date countEndTimestamp;
    private String doorId;
    private String objectClass;
    private String countingAreaId;
    private int in;
    private int out;

    private MutableLiveData<Integer> observableIn;
    private MutableLiveData<Integer> observableOut;

    public CountingSequence() {
        this.observableIn = new MutableLiveData<>();
        this.observableOut = new MutableLiveData<>();
    }

    public Date getCountBeginTimestamp() {
        return this.countBeginTimestamp;
    }

    public void setCountBeginTimestamp(Date countBeginTimestamp) {
        this.countBeginTimestamp = countBeginTimestamp;
    }

    public Date getCountEndTimestamp() {
        return this.countEndTimestamp;
    }

    public void setCountEndTimestamp(Date countEndTimestamp) {
        this.countEndTimestamp = countEndTimestamp;
    }

    public String getDoorId() {
        return this.doorId;
    }

    public void setDoorId(String doorId) {
        this.doorId = doorId;
    }

    public String getObjectClass() {
        return this.objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public String getCountingAreaId() {
        return this.countingAreaId;
    }

    public void setCountingAreaId(String countingAreaId) {
        this.countingAreaId = countingAreaId;
    }

    public int getIn() {
        return this.in;
    }

    public void setIn(int in) {
        this.in = in;
        this.observableIn.postValue(in);
    }

    public int getOut() {
        return this.out;
    }

    public void setOut(int out) {
        this.out = out;
        this.observableOut.postValue(out);
    }

    public LiveData<Integer> getObservableIn() {
        return this.observableIn;
    }

    public LiveData<Integer> getObservableOut() {
        return this.observableOut;
    }

    public void incrementIn() {
        int in = this.getIn();
        this.setIn(in++);
    }

    public void decrementIn() {
        int in = this.getIn();
        if (in > 0) {
            this.setIn(in--);
        }
    }

    public void incrementOut() {
        int out = this.getOut();
        this.setOut(out++);
    }

    public void decrementOut() {
        int out = this.getOut();
        if (out> 0) {
            this.setOut(out--);
        }
    }

    @Override
    public CountingSequenceObject mapApiObject() {
        CountingSequenceObject apiObject = new CountingSequenceObject();
        apiObject.setCountBeginTimestamp(this.getCountBeginTimestamp().getTime() / 1000L);
        apiObject.setCountEndTimestamp(this.getCountEndTimestamp().getTime() / 1000L);
        apiObject.setDoorId(this.getDoorId());
        apiObject.setObjectClass(this.getObjectClass());
        apiObject.setCountingAreaId(this.getCountingAreaId());
        apiObject.setIn(this.getIn());
        apiObject.setOut(this.getOut());

        return apiObject;
    }
}
