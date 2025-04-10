package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.Line;

public class LineObject implements DomainModelMapper<Line> {

    @SerializedName("line_id")
    private int lineId;
    @SerializedName("name")
    private String name;
    @SerializedName("international_id")
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
    public Line mapDomainModel() {
        Line domainModel = new Line();

        domainModel.setLineId(this.getLineId());
        domainModel.setName(this.getName());
        domainModel.setInternationalId(this.getInternationalId());

        return domainModel;
    }
}
