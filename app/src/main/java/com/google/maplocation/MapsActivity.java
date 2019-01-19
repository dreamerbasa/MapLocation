package com.google.maplocation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    double latitude,longitude;
    LatLng my_loc;

    Button hosp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        //getting location access
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //requesting location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    my_loc = new LatLng(latitude,longitude);//found ue location, and created a variable.

                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {

                        List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
                        String location_name = addresses.get(0).getLocality();
                        location_name += addresses.get(0).getCountryName();

                        mMap.addMarker(new MarkerOptions().position(my_loc).title(location_name));//.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_loc, 15));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    my_loc = new LatLng(latitude,longitude);//found ue location, and created a variable.

                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {

                        List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
                        String location_name = addresses.get(0).getLocality();
                        location_name += addresses.get(0).getCountryName();

                        mMap.addMarker(new MarkerOptions().position(my_loc).title(location_name));//.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_loc, 15));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }

        //writing code on button click
        hosp = findViewById(R.id.hosp);

        hosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Hospital = "hospital";
                mMap.clear();

                String url = getUrl(latitude,longitude,Hospital);

                Object[] dataTransfer = new Object[2];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;

                GetNearby getNearby = new GetNearby();
                getNearby.execute(dataTransfer);

                Toast.makeText(MapsActivity.this,"Nearby hospitals", Toast.LENGTH_LONG).show();

            }
        });



    }

    public boolean CheckGooglePlayServices(){

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        //Helper class for verifying that the Google Play services APK is available and up-to-date on this device.
        int result = googleApiAvailability.isGooglePlayServicesAvailable(MapsActivity.this);//status code indicating whether there was an error.
        //Verifies that Google Play services is installed and enabled on this device, and that the version installed on this device is no older than the one required by this client.
        if (result!=ConnectionResult.SUCCESS){

            if (googleApiAvailability.isUserResolvableError(result))//Determines whether an error can be resolved via user action.
            {

                googleApiAvailability.getErrorDialog(MapsActivity.this,result,0).show();//Determines whether an error can be resolved via user action.

            }
            return false;

        }
        return true;

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace){

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        googlePlacesUrl.append("&radius="+1000);
        googlePlacesUrl.append("&type="+nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key="+"AIzaSyDhyh8sr7mm0rkuhNSoLGl0Pv9wjwEhg_U");
        Log.d("getUrl",googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());

    }

}
