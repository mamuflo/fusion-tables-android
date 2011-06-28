// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.fusiontables;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.fusiontables.ftclient.ClientLogin;
import com.google.fusiontables.ftclient.FTClient;

/**
 * Main activity class.
 * 
 * @author kbrisbin@google.com (Kathryn Hurley)
 */
public class FusionTablesDemoActivity extends Activity {

  private Button button;

  private MyLocationListener locationListener;
  private LocationManager locationManager;
  private String locationProvider = LocationManager.GPS_PROVIDER;

  private FTClient ftclient;
  private long tableid = 123456;
  private String username = "<username>";
  private String password = "<password>";
  private String embedLink = "http://gmaps-samples.googlecode.com/svn/trunk/" +
      "fusiontables/potholes.html";

  /**
   * Method called during start up
   *
   * @param savedInstanceState the saved instance state
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // Initialize FTClient
    String token = ClientLogin.authorize(username, password);
    ftclient = new FTClient(token);

    initSpinner();
    initButton();
    initLocation();
  }

  /**
   * Method called during app pause. Stops GPS readings.
   */
  @Override
  protected void onPause() {
    super.onPause();
    locationManager.removeUpdates(locationListener);
  }

  /**
   * Method called during app resume. Starts GPS readings.
   */
  @Override
  protected void onResume() {
    super.onResume();
    requestUpdates();
    enableButton();
  }

  /**
   * Initializes the spinner menu with an array adapter.
   */
  private void initSpinner() {
    Spinner spinner = (Spinner) findViewById(R.id.status);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.statuses, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(
        android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
  }

  /**
   * Initializes the button and the click event.
   */
  private void initButton() {
    button = (Button) findViewById(R.id.submitform);

    // Disable until GPS reading is obtained.
    button.setEnabled(false);

    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        // Tell the user thanks
        Toast.makeText(FusionTablesDemoActivity.this,
            "Thanks for reporting!", 10).show();

        // Open the map in a browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
            Uri.parse(embedLink));
        startActivity(browserIntent);

        // Get the current spinner selection
        Spinner spinner = (Spinner) findViewById(R.id.status);
        long state = spinner.getSelectedItemId();

        // Save the data to Fusion Tables
        ftclient.query("INSERT INTO " + tableid +
            " (Severity, Location, Address, Timestamp) VALUES " +
            "(" + state + "," +
              " '" + locationListener.getStringLocation() + "' ," +
              " '" + locationListener.getAddress() + "'," +
              new Date().getTime() + ")");
      }
    });
  }

  /**
   * Initializes the location listener.
   */
  private void initLocation() {
    locationManager = (LocationManager)getSystemService(
        Context.LOCATION_SERVICE);
    Location lastKnownLocation = locationManager.getLastKnownLocation(
        locationProvider);

    locationListener = new MyLocationListener(this);
    if (lastKnownLocation != null) {
      locationListener.setLocation(lastKnownLocation);
    }
    setLocationText(locationListener.getFormattedAddress());

    requestUpdates();
  }

  /**
   * Request location updates.
   */
  private void requestUpdates() {
    Toast.makeText(this, "Getting location...", 15).show();
    locationManager.requestLocationUpdates(locationProvider, 5000, 1,
        locationListener);
  }

  /**
   * Update location text view.
   */
  public void setLocationText(String address) {
    TextView tv = (TextView) findViewById(R.id.location);
    tv.setText(address);
  }

  /**
   * Enable the button if disabled.
   */
  public void enableButton() {
    if (!button.isEnabled()) {
      button.setEnabled(true);
    }
  }

  /**
   * Disable the button if enabled.
   */
  public void disableButton() {
    if (button.isEnabled()) {
      button.setEnabled(false);
    }
  }
}
