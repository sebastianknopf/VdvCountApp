package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.common.DomainModelMapper;
import de.vdvcount.app.model.StopTime;
import de.vdvcount.app.model.Trip;

class TripObject implements DomainModelMapper<Trip> {

   @SerializedName("trip_id")
   private int tripId;
   @SerializedName("line")
   private LineObject line;
   @SerializedName("direction")
   private int direction;
   @SerializedName("headsign")
   private String headsign;
   @SerializedName("international_id")
   private String internationalId;
   @SerializedName("next_trip_id")
   private int nextTripId;
   @SerializedName("stop_times")
   private List<StopTimeObject> stopTimes;

   public int getTripId() {
      return this.tripId;
   }

   public void setTripId(int tripId) {
      this.tripId = tripId;
   }

   public LineObject getLine() {
      return this.line;
   }

   public void setLine(LineObject line) {
      this.line = line;
   }

   public int getDirection() {
      return this.direction;
   }

   public void setDirection(int direction) {
      this.direction = direction;
   }

   public String getHeadsign() {
      return this.headsign;
   }

   public void setHeadsign(String headsign) {
      this.headsign = headsign;
   }

   public String getInternationalId() {
      return this.internationalId;
   }

   public void setInternationalId(String internationalId) {
      this.internationalId = internationalId;
   }

   public int getNextTripId() {
      return this.nextTripId;
   }

   public void setNextTripId(int nextTripId) {
      this.nextTripId = nextTripId;
   }

   public List<StopTimeObject> getStopTimes() {
      return this.stopTimes;
   }

   public void setStopTimes(List<StopTimeObject> stopTimes) {
      this.stopTimes = stopTimes;
   }

   @Override
   public Trip mapDomainModel() {
      Trip domainModel = new Trip();

      domainModel.setTripId(this.getTripId());
      domainModel.setLine(this.getLine().mapDomainModel());
      domainModel.setDirection(this.getDirection());
      domainModel.setHeadsign(this.getHeadsign());
      domainModel.setInternationalId(this.getInternationalId());
      domainModel.setNextTripId(this.getNextTripId());

      List<StopTime> stopTimes = new ArrayList<>();
      for (StopTimeObject obj : this.getStopTimes()) {
         stopTimes.add(obj.mapDomainModel());
      }

      domainModel.setStopTimes(stopTimes);

      return domainModel;
   }
}
