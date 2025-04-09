package de.vdvcount.app.model;

import com.google.gson.annotations.SerializedName;

public class ObjectClass {

   private String name;
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
}
