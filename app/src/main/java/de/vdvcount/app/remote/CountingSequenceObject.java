package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.CountingSequence;

public class CountingSequenceObject implements DomainModelMapper<CountingSequence> {

    @SerializedName("count_begin_timestamp")
    private long countBeginTimestamp;

    @SerializedName("count_begin_timestamp")
    private long countEndTimestamp;

    @SerializedName("door_id")
    private String doorId;

    @SerializedName("object_class")
    private String objectClass;

    @SerializedName("counting_area_id")
    private String countingAreaId;

    @SerializedName("in")
    private int in;

    @SerializedName("out")
    private int out;

    public CountingSequenceObject() {}

    public long getCountBeginTimestamp() {
        return countBeginTimestamp;
    }

    public void setCountBeginTimestamp(long countBeginTimestamp) {
        this.countBeginTimestamp = countBeginTimestamp;
    }

    public long getCountEndTimestamp() {
        return countEndTimestamp;
    }

    public void setCountEndTimestamp(long countEndTimestamp) {
        this.countEndTimestamp = countEndTimestamp;
    }

    public String getDoorId() {
        return doorId;
    }

    public void setDoorId(String doorId) {
        this.doorId = doorId;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public String getCountingAreaId() {
        return countingAreaId;
    }

    public void setCountingAreaId(String countingAreaId) {
        this.countingAreaId = countingAreaId;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public int getOut() {
        return out;
    }

    public void setOut(int out) {
        this.out = out;
    }

    @Override
    public CountingSequence mapDomainModel() {
        CountingSequence domainModel = new CountingSequence();
        domainModel.setCountBeginTimestamp(new Date(this.getCountBeginTimestamp() * 1000L));
        domainModel.setCountEndTimestamp(new Date(this.getCountEndTimestamp() * 1000L));
        domainModel.setDoorId(this.getDoorId());
        domainModel.setObjectClass(this.getObjectClass());
        domainModel.setCountingAreaId(this.getCountingAreaId());
        domainModel.setIn(this.getIn());
        domainModel.setOut(this.getOut());

        return domainModel;
    }
}
