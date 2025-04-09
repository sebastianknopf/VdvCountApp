package de.vdvcount.app.model;

import de.vdvcount.app.common.ApiObjectMapper;
import de.vdvcount.app.remote.LineObject;

public class Line implements ApiObjectMapper<LineObject> {

    private int lineId;
    private String name;
    private String internationalId;

    public int getLineId() {
        return this.lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternationalId() {
        return this.internationalId;
    }

    public void setInternationalId(String internationalId) {
        this.internationalId = internationalId;
    }

    @Override
    public LineObject mapApiObject() {
        LineObject apiObject = new LineObject();

        apiObject.setLineId(this.getLineId());
        apiObject.setName(this.getName());
        apiObject.setInternationalId(this.getInternationalId());

        return apiObject;
    }
}
