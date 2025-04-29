package de.vdvcount.app.common;

import android.annotation.SuppressLint;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.vdvcount.app.App;

public class LocationService {

   private static FusedLocationProviderClient fusedClient;

   static {
      fusedClient = LocationServices.getFusedLocationProviderClient(App.getStaticContext());
   }

   @SuppressLint("MissingPermission")
   public static Location requestCurrentLocation() {
      final CountDownLatch latch = new CountDownLatch(1);
      final Location[] result = new Location[1];

      try {
         fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                 .addOnSuccessListener(location -> {
                    result[0] = location;
                    latch.countDown();
                 })
                 .addOnFailureListener(e -> {
                    Logging.e(LocationService.class.getName(), "FusedLocationClient::onFailureListener", e);
                    latch.countDown();
                 });

         int timeout = 2000;
         boolean completed = latch.await(timeout, TimeUnit.MILLISECONDS);
         if (!completed) {
            Logging.w(LocationService.class.getName(), String.format("CountDownLatch cancelled after %d ms, using last known location", timeout));
         }
      } catch (Exception e) {
         Logging.e(LocationService.class.getName(), "Failed to update current location", e);
      }

      return result[0];
   }
}
