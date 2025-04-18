package de.vdvcount.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

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
    }

    public int getOut() {
        return this.out;
    }

    public void setOut(int out) {
        this.out = out;
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
