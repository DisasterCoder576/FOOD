/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.quaranfood.starter;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.R;

import java.util.List;


public class MainActivity extends AppCompatActivity {

  Boolean requestActive = false;

  LocationManager locationManager;

  LocationListener locationListener;

  Button callUberButton;
  Boolean signUpModeActive = true;
  TextView loginTextView;
  EditText usernameEditText;
  EditText passwordEditText;
    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), ViewRequestsActivity.class);
        startActivity(intent);
    }



  public void logout(View view) {

    ParseUser.logOut();

    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    startActivity(intent);


  }
  public void callUber(View view) {

    Log.i("Info", "SEND ALERT");

    if (requestActive) {

      ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");

      query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

      query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> objects, ParseException e) {

          if (e == null) {

            if (objects.size() > 0) {

              for (ParseObject object : objects) {

                object.deleteInBackground();

              }

              requestActive = false;
              callUberButton.setText("SEND ALERT");

            }

          }

        }
      });


    } else {

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastKnownLocation != null) {

          ParseObject request = new ParseObject("Request");

          request.put("username", ParseUser.getCurrentUser().getUsername());

          ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

          request.put("location", parseGeoPoint);

          request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

              if (e == null) {

                callUberButton.setText("Cancel ALERT");
                requestActive = true;

              }

            }
          });

        } else {

          Toast.makeText(this, "Could not find location. Please try again later.", Toast.LENGTH_SHORT).show();

        }

      }

    }


  }

  public void redirectActivity() {

    if (ParseUser.getCurrentUser().getString("riderOrDriver").equals("rider")) {

        Intent intent = new Intent(getApplicationContext(), RiderActivity.class);
        startActivity(intent);

    } else {

      Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);


    }
  }

  public void getStarted(View view) {

    Switch userTypeSwitch = (Switch) findViewById(R.id.userTypeSwitch);

    Log.i("Switch value", String.valueOf(userTypeSwitch.isChecked()));

    String userType = "rider";

    if (userTypeSwitch.isChecked()) {

      userType = "driver";

    }

    ParseUser.getCurrentUser().put("riderOrDriver", userType);

      ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
          @Override
          public void done(ParseException e) {

              redirectActivity();

          }
      });




  }

  public void onClick (View view)
  {
      EditText usernameEditText = (EditText) findViewById(R.id.editText);
      EditText passwordEditText = (EditText) findViewById(R.id.editText2);

      ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {
              if (user != null) {
                  Log.i("Login","ok!");
                  showUserList();
              } else {
                  Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
              }
          }
      });

  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    getSupportActionBar().hide();


    if (ParseUser.getCurrentUser() == null) {

      ParseAnonymousUtils.logIn(new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {

          if (e == null) {

            Log.i("Info", "Anonymous login successful");

          } else {

            Log.i("Info", "Anonymous login failed");

          }


        }
      });

    } else {

      if (ParseUser.getCurrentUser().get("riderOrDriver") != null) {

        Log.i("Info", "Redirecting as " + ParseUser.getCurrentUser().get("riderOrDriver"));

        redirectActivity();

      }


    }


    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }

}