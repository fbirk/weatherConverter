package com.fbirk.weatherconverter;

/*
 * Copyright (c) delight.im <info@delight.im>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;
import android.location.LocationListener;
import android.location.Location;
import android.content.Context;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.os.Build;


public class SimpleLocation implements LocationListener  {

    private LocationManager locationManager;
    private Location location;

    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

    private static Context context;


    public SimpleLocation(final Context context) {
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
    }

    public Location getLocation() {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.err.println("Error");
            return null;
        }

        try   {
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                System.err.println("GPS and Network Location not enabled");
            } else {

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            0,
                            0, this);
                    if (locationManager != null)   {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        return location;
                    }
                } else if(isGPSEnabled)  {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                    if (locationManager != null)  {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        return location;
                        }
                }
            }
        } catch (Exception ex)  {
            System.err.println(ex.getMessage());
        }
        System.err.println("An Error occurred");
        return null;
    }

    @Override
    public void onLocationChanged(Location newLocation)     {
        // do stuff here with location object
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle b) {
        // whatever
    }

    @Override
    public void onProviderEnabled(String s) {
        // more stuff to do
    }

    public void onProviderDisabled(String s) {
        // even more stuff to do - wow, what a messy place
    }
}