package de.vdvcount.app.common;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.vdvcount.app.App;

public class LocationService {

   private static FusedLocationProviderClient fusedClient;
   private static LocationCallback locationCallback;
   private static boolean locationUpdatesActive;

   private static MutableLiveData<Location> location;

   static {
      fusedClient = LocationServices.getFusedLocationProviderClient(App.getStaticContext());
      locationCallback = new LocationCallback() {
         @Override
         public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);

            if (locationAvailability.isLocationAvailable()) {
               Logging.i(LocationService.class.getName(), "Location is available");
            } else {
               Logging.i(LocationService.class.getName(), "Location is not available");
            }
         }

         @Override
         public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            Location lastLocation = locationResult.getLastLocation();
            if (lastLocation != null) {
               Logging.i(LocationService.class.getName(), String.format("Periodic location update (lat/lon) %f / %f", lastLocation.getLatitude(), lastLocation.getLongitude()));
            } else {
               Logging.w(LocationService.class.getName(), "Periodic location update returned null");
            }

            LocationService.location.postValue(locationResult.getLastLocation());
         }
      };

      location = new MutableLiveData<>(null);
   }

   public static LiveData<Location> getLocation() {
      return LocationService.location;
   }

   @SuppressLint("MissingPermission")
   public static Location requestCurrentLocation() {
      final CountDownLatch latch = new CountDownLatch(1);
      final Location[] result = new Location[1];

      try {
         LocationService.fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
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

   @SuppressLint("MissingPermission")
   public static void startLocationUpdates() {
      Logging.i(LocationService.class.getName(), "Requesting periodic location updates");

      if (LocationService.locationUpdatesActive) {
         Logging.i(LocationService.class.getName(), "Periodic location updates already requested and active");
         return;
      }

      try {
         LocationRequest locationRequest = new LocationRequest
                 .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                 .build();

         LocationService.fusedClient.requestLocationUpdates(
                 locationRequest,
                 locationCallback,
                 Looper.getMainLooper()
         );

         LocationService.locationUpdatesActive = true;
      } catch (Exception e) {
         Logging.e(LocationService.class.getName(), "Failed to register location updates", e);
      }
   }

   public static void stopLocationUpdates() {
      Logging.i(LocationService.class.getName(), "Stopping periodic location updates");

      if (!LocationService.locationUpdatesActive) {
         return;
      }

      LocationService.fusedClient.removeLocationUpdates(locationCallback);
      LocationService.locationUpdatesActive = false;
   }
}
