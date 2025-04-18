package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.Stop;

public class StopObject implements DomainModelMapper<Stop> {

   @SerializedName("international_id")
   private String internationalId;
   @SerializedName("latitude")
   private double latitude;
   @SerializedName("longitude")
   private double longitude;
   @SerializedName("name")
   private String name;
   @SerializedName("parent_id")
   private int parentId;
   @SerializedName("stop_id")
   private int stopId;

   public String getInternationalId() {
      return internationalId;
   }

   public void setInternationalId(String internationalId) {
      this.internationalId = internationalId;
   }

   public double getLatitude() {
      return latitude;
   }

   public void setLatitude(double latitude) {
      this.latitude = latitude;
   }

   public double getLongitude() {
      return longitude;
   }

   public void setLongitude(double longitude) {
      this.longitude = longitude;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getParentId() {
      return parentId;
   }

   public void setParentId(int parentId) {
      this.parentId = parentId;
   }

   public int getStopId() {
      return stopId;
   }

   public void setStopId(int stopId) {
      this.stopId = stopId;
   }

   @Override
   public Stop mapDomainModel() {
      Stop domainModel = new Stop();
      domainModel.setInternationalId(this.getInternationalId());
      domainModel.setLatitude(this.getLatitude());
      domainModel.setLongitude(this.getLongitude());
      domainModel.setLatitude(this.getLatitude());
      domainModel.setName(this.getName());
      domainModel.setParentId(this.getParentId());
      domainModel.setStopId(this.getStopId());

      return domainModel;
   }
}
