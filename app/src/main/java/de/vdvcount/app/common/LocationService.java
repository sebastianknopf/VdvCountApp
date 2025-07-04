package de.vdvcount.app.common;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Handler;
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

   public final static String LOCATION_CHANGED_BROADCAST = "android.location.PROVIDERS_CHANGED";

   private static LocationService singleInstance;

   private FusedLocationProviderClient fusedClient;
   private LocationCallback locationCallback;
   private boolean locationUpdatesActive;

   private MutableLiveData<Location> location;
   private MutableLiveData<Boolean> locationAvailable;

   private Runnable locationAvailabilityRunnable;
   private Handler locationAvailabilityHandler;

   public static LocationService getInstance() {
      if (singleInstance == null) {
         singleInstance = new LocationService();
      }

      return singleInstance;
   }

   public LocationService() {
      this.fusedClient = LocationServices.getFusedLocationProviderClient(App.getStaticContext());

      this.locationAvailabilityRunnable = () -> {
         this.locationAvailable.postValue(false);
      };

      this.locationAvailabilityHandler = new Handler();

      this.locationCallback = new LocationCallback() {
         @Override
         public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);

            boolean locationAvailable = locationAvailability.isLocationAvailable();
            handleLocationAvailability(locationAvailable);
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

            location.postValue(locationResult.getLastLocation());
         }
      };

      this.location = new MutableLiveData<>(null);
      this.locationAvailable = new MutableLiveData<>(null);
   }

   public LiveData<Location> getLocation() {
      return this.location;
   }

   public LiveData<Boolean> getLocationAvailable() {
      return this.locationAvailable;
   }

   @SuppressLint("MissingPermission")
   public Location requestCurrentLocation() {
      final CountDownLatch latch = new CountDownLatch(1);
      final Location[] result = new Location[1];

      try {
         this.fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
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
   public void startLocationUpdates() {
      Logging.i(LocationService.class.getName(), "Requesting periodic location updates");

      if (this.locationUpdatesActive) {
         Logging.i(LocationService.class.getName(), "Periodic location updates already requested and active");
         return;
      }

      try {
         LocationRequest locationRequest = new LocationRequest
                 .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                 .build();

         this.fusedClient.requestLocationUpdates(
                 locationRequest,
                 this.locationCallback,
                 Looper.getMainLooper()
         );

         this.handleLocationAvailability(false);

         this.locationUpdatesActive = true;
      } catch (Exception e) {
         Logging.e(LocationService.class.getName(), "Failed to register location updates", e);
      }
   }

   public void stopLocationUpdates() {
      Logging.i(LocationService.class.getName(), "Stopping periodic location updates");

      if (!this.locationUpdatesActive) {
         return;
      }

      this.fusedClient.removeLocationUpdates(locationCallback);
      this.locationUpdatesActive = false;
   }

   private void handleLocationAvailability(boolean locationAvailable) {
      if (locationAvailable) {
         Logging.i(LocationService.class.getName(), "Location is available");

         this.locationAvailable.postValue(true);
         this.locationAvailabilityHandler.removeCallbacks(this.locationAvailabilityRunnable);
      } else {
         Logging.i(LocationService.class.getName(), "Location is not available");

         this.locationAvailabilityHandler.postDelayed(this.locationAvailabilityRunnable, 15000);
      }
   }
}
