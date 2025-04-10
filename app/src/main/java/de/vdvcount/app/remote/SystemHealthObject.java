package de.vdvcount.app.remote;

import com.google.gson.annotations.SerializedName;

class SystemHealthObject {
   @SerializedName("timestamp")
   private int timestamp;

   public int getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(int timestamp) {
      this.timestamp = timestamp;
   }
}
