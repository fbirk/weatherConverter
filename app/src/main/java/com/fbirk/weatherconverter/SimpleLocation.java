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

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    //The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;

    private static SimpleLocation instance = null;

    public SimpleLocation() {

    }

    private LocationManager locationManager;
    public Location location;
    public double longitude;
    public double latitude;

    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean locationServiceAvailable;


    /**
     * Singleton implementation
     * @return
     */
    public static SimpleLocation getLocationManager(Context context)     {
        if (instance == null) {
            instance = new SimpleLocation(context);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    private SimpleLocation( Context context )     {
        initLocationService(context);
    }


    /**
     * Sets up location service after permissions is granted
     */
    private Location initLocationService(Context context) {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        try   {
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            System.out.println("IS GPS ENABLED? " + isGPSEnabled + "IS NETWORK ENABLED? " + isNetworkEnabled);

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
            } else {
                this.locationServiceAvailable = true;

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
        return null;
    }

    public Location getCoordinates() {
        return this.location;
    }

    @Override
    public void onLocationChanged(Location newLocation)     {
        this.location = newLocation;
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