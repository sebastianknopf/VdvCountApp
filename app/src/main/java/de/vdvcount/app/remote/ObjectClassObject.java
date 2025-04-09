package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.ObjectClass;

class ObjectClassObject implements DomainModelMapper<ObjectClass> {
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public ObjectClass mapDomainModel() {
        ObjectClass domainModel = new ObjectClass();

        domainModel.setName(this.getName());
        domainModel.setDescription(this.getDescription());

        return domainModel;
    }
}
