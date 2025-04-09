package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.Station;

class StationObject implements DomainModelMapper<Station> {

   @SerializedName("parent_id")
   private int id;

   @SerializedName("name")
   private String name;

   @SerializedName("latitude")
   private double latitude;

   @SerializedName("longitude")
   private double longitude;

   @SerializedName("similarity")
   private double similarity;

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

   public double getSimilarity() {
      return this.similarity;
   }

   public void setSimilarity(double similarity) {
      this.similarity = similarity;
   }

   @Override
   public Station mapDomainModel() {
      Station domainModel = new Station();

      domainModel.setId(this.getId());
      domainModel.setName(this.getName());
      domainModel.setLatitude(this.getLatitude());
      domainModel.setLongitude(this.getLongitude());
      domainModel.setSimilarity(this.getSimilarity());

      return domainModel;
   }
}
