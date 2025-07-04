package de.vdvcount.app.common;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

   private BroadcastReceiver locationChangedReceiver;
   private boolean locationUpdatesActive;

   private MutableLiveData<Location> location;
   private MutableLiveData<Boolean> locationAvailable;

   public static LocationService getInstance() {
      if (singleInstance == null) {
         singleInstance = new LocationService();
      }

      return singleInstance;
   }

   public LocationService() {
      this.fusedClient = LocationServices.getFusedLocationProviderClient(App.getStaticContext());

      this.locationCallback = new LocationCallback() {
         @Override
         public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
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

   public void requireLocationEnabled(Context context) {
      this.locationChangedReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(LocationService.LOCATION_CHANGED_BROADCAST)) {
               updateLocationAvailability(context);
            }
         }
      };

      IntentFilter filter = new IntentFilter(LocationService.LOCATION_CHANGED_BROADCAST);
      ContextCompat.registerReceiver(
              context,
              this.locationChangedReceiver,
              filter,
              ContextCompat.RECEIVER_NOT_EXPORTED
      );

      // this first call is required to set the initial location availability
      // the broadcast receiver runs first time when the location state is changed
      // if the location state is OFF when the broadcast receiver is registered,
      // no update would be fired an vice-versa
      this.updateLocationAvailability(context);
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

   private void updateLocationAvailability(Context context) {
      LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
      boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
      boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

      this.locationAvailable.postValue(isGpsEnabled || isNetworkEnabled);
   }
}
