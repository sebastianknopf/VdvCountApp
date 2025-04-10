package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.Vehicle;

class VehicleObject implements DomainModelMapper<Vehicle> {
    @SerializedName("name")
    private String name;
    @SerializedName("num_doors")
    private int numDoors;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumDoors() {
        return this.numDoors;
    }

    public void setNumDoors(int numDoors) {
        this.numDoors = numDoors;
    }

    @Override
    public Vehicle mapDomainModel() {
        Vehicle domainModel = new Vehicle();

        domainModel.setName(this.getName());
        domainModel.setNumDoors(this.getNumDoors());

        return domainModel;
    }
}
