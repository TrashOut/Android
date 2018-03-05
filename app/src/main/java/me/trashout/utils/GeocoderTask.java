/*
 * TrashOut is an environmental project that teaches people how to recycle 
 * and showcases the worst way of handling waste - illegal dumping. All you need is a smart phone.
 *  
 *  
 * There are 10 types of programmers - those who are helping TrashOut and those who are not.
 * Clean up our code, so we can clean up our planet. 
 * Get in touch with us: help@trashout.ngo
 *  
 * Copyright 2017 TrashOut, n.f.
 *  
 * This file is part of the TrashOut project.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See the GNU General Public License for more details: <https://www.gnu.org/licenses/>.
 */

package me.trashout.utils;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class GeocoderTask extends AsyncTask<Void, Void, GeocoderTask.GeocoderResult> {

    private static final String TAG = GeocoderTask.class.getSimpleName();

    public interface Callback {
        public void onAddressComplete(GeocoderResult geocoderResult);
    }

    private Geocoder mGeocoder;
    private double mLat;
    private double mLng;
    private Callback mCallback;

    public GeocoderTask(Geocoder geocoder, double lat, double lng,
                        Callback callback) {
        mGeocoder = geocoder;
        mLat = lat;
        mLng = lng;
        mCallback = callback;
    }

    @Override
    protected GeocoderResult doInBackground(Void... params) {
        GeocoderResult value = new GeocoderResult();
        try {
            List<Address> address = mGeocoder.getFromLocation(mLat, mLng, 1);
            Log.d(TAG, "doInBackground: " + address);

            String addressString = "";
            String shortAddressString = "";
            for (Address addr : address) {
                if (address != null && addressString.isEmpty() && addr.getMaxAddressLineIndex() >= 0) {
                    addressString = getFormattedAddress(addr);
                    shortAddressString = getFormattedShortAddress(addr);
                }
            }

            value.address = !address.isEmpty() ? address.get(0) : null;
            value.formattedAddress = addressString;
            value.formattedShortAddress = shortAddressString;
        } catch (IOException ex) {
            Log.e(TAG, "Geocoder exception: ", ex);
        } catch (RuntimeException ex) {
            Log.e(TAG, "Geocoder exception: ", ex);
        }
        return value;
    }

    @Override
    protected void onPostExecute(GeocoderResult location) {
        mCallback.onAddressComplete(location);
    }

    private String getFormattedAddress(Address address) {
        String addressString = "";
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressString = addressString.isEmpty() ? addressString + address.getAddressLine(i) : addressString + ", " + address.getAddressLine(i);
        }
        return addressString;
    }

    private String getFormattedShortAddress(Address address) {
        String addressString = "";
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            if(address.getThoroughfare() == null){
                addressString = address.getLocality();
            } else {
                addressString = address.getThoroughfare() + ", " + address.getLocality();
            }
        }
        return addressString;
    }

    public class GeocoderResult {
        Address address;
        String formattedAddress;
        private CharSequence formattedShortAddress;

        public Address getAddress() {
            return address;
        }

        public String getFormattedAddress() {
            return formattedAddress;
        }

        public CharSequence getFormattedShortAddress() {
            return formattedShortAddress;
        }
    }
}

