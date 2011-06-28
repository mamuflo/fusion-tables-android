package com.google.fusiontables;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Location listener - gets updates from GPS to set current location.
 */
public class MyLocationListener implements LocationListener {
  private Location location;
  private String address = "";
  private String formattedAddress = "";
  private FusionTablesDemoActivity act;
  private static final int TWO_MINUTES = 1000 * 60 * 2;

  /**
   * Constructor, sets the activity.
   *
   * @param act  the FusionTablesDemoActivity
   */
  public MyLocationListener(FusionTablesDemoActivity act) {
    this.act = act;
  }

  /**
   * Called when the location changes.
   *
   * @param location  the new location
   */
  public void onLocationChanged(Location location) {
    setLocation(location);
  }

  /**
   * Sets the new location values.
   *
   * @param location  the new location
   */
  public void setLocation(Location location) {
    // Only if the new location is better
    if (isBetterLocation(location, this.location)) {
      Geocoder gc = new Geocoder(this.act, Locale.getDefault());
      try {
        // Get a list of addresses
        List<Address> addresses = gc.getFromLocation(location.getLatitude(),
            location.getLongitude(), 1);

        // Initialize 2 strings, one simple and one formatted
        StringBuilder simpleAddress = new StringBuilder();
        StringBuilder formAddress = new StringBuilder();

        // Concatenate the address strings
        if (addresses.size() > 0) {
          Address address = addresses.get(0);
          for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            simpleAddress.append(address.getAddressLine(i) + " ");
            formAddress.append(address.getAddressLine(i) + "\n");
          }
        }

        // If there really is an address, set the new location
        if (simpleAddress.toString() != "") {
          this.location = location;
          address = simpleAddress.toString();
          formattedAddress = formAddress.toString();
          this.act.setLocationText(formattedAddress);
          this.act.enableButton();
        }
      } catch (IOException e) { }
    }
  }

  /**
   * Determines whether new Location reading is better than the current one
   *
   * @param location  The new Location that you want to evaluate
   * @param currentBestLocation  The current Location fix, to which you want
   *        to compare the new one
   *
   * @return true if better location, false if not
   */
  protected boolean isBetterLocation(Location newLocation,
      Location currentBestLocation) {
    if (currentBestLocation == null) {
      return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;

    // If it's been more than two minutes since the current location,
    // use the new location because the user has likely moved
    if (isSignificantlyNewer) {
      return true;
    // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
      return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (newLocation.getAccuracy() -
        currentBestLocation.getAccuracy());
    boolean isMoreAccurate = accuracyDelta < 0;

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
      return true;
    }
    return false;
  }

  /**
   * Return location.
   *
   * @return location
   */
  public Location getLocation() {
    return this.location;
  }

  /**
   * Return string-formatted location.
   *
   * @return string-formatted location
   */
  public String getStringLocation() {
    return this.location.getLatitude() + "," + this.location.getLongitude();
  }

  /**
   * Return address.
   *
   * @return address
   */
  public String getAddress() {
    return this.address;
  }

  /**
   * Return formatted address (with newline characters).
   *
   * @return formatted address
   */
  public String getFormattedAddress() {
    return this.formattedAddress;
  }

  /**
   * When the provider is disabled, do nothing.
   *
   * @param provider
   */
  public void onProviderDisabled(String provider) { }

  /**
   * When the provider is enabled, do nothing.
   *
   * @param provider
   */
  public void onProviderEnabled(String provider) { }

  /**
   * When the status has changed.
   *
   * @param provider
   * @param status
   * @param extras
   */
  public void onStatusChanged(String provider, int status, Bundle extras) { }
}