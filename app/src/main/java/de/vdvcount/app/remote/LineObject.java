package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.Line;

class LineObject implements DomainModelMapper<Line> {

    @SerializedName("line_id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("international_id")
    private String internationalId;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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

        domainModel.setId(this.getId());
        domainModel.setName(this.getName());
        domainModel.setInternationalId(this.getInternationalId());

        return domainModel;
    }
}
