package com.google.maplocation;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class GetNearby extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    String url;
    GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {
        try {
            mMap = (GoogleMap) objects[0];
            url = (String) objects[1];

            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);

            Log.d("GetNearby","doInBackground Exit");

        }catch (Exception e){
            Log.d("GetNearby_googlePlaces",e.toString());
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {

        Log.d("GetNearby","onPostExecuting");
        List <HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(s);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GetNearby","onPostExecute end");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList){

        for (int i=0;i<nearbyPlacesList.size();i++) {
            Log.d("OnPostExecute", "Entered into show neaerby places");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            mMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }

    }

}
