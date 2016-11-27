package com.example.projectlemon.projectlemon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivityPedirRaite extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
        , GoogleMap.InfoWindowAdapter
        {

    private GoogleMap mMap;
    private Location myLocation;
    Location lastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_pedir_raite);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button buttonHme = (Button) findViewById(R.id.btnHome);
        buttonHme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(MapsActivityPedirRaite.this, UserProfileActivity.class));
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getPermissions();

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            lastKnownLocation = myLocation;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()), 17));
        }catch (SecurityException ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }

        mMap.addMarker(new MarkerOptions()
        .position(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()))
                .title("Marker Yay"));

        mMap.setInfoWindowAdapter(this);

        /*
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(40,-73))
                .title("Marker"));
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.activity_passenger_info, null);

                LatLng latLng = marker.getPosition();

                return v;
            }
        });
        */

    }


            public void getPermissions(){

                int MY_PERMISSIONS_REQUEST_LOCATION = 99;
                boolean per = false;
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                }else{
                    per = true;
                }
                // Permission was granted.
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    mMap.setMyLocationEnabled(true);
                }else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onLocationChanged(Location location) {

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

            @Override
            public void onConnected(@Nullable Bundle bundle) {

            }

            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }

            @Override
            public View getInfoWindow(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.passenger_info, null);

                TextView m = (TextView)view.findViewById(R.id.Woah);

                m.setText("My Awesome Text");
                ProfilePictureView p = (ProfilePictureView) view.findViewById(R.id.img);
                p.setProfileId("10206931439515838");
                //ProfilePictureView profilePictureView;
                //profilePictureView = (ProfilePictureView) view.findViewById(R.id.img);
                //profilePictureView.setProfileId("1123555264402222");

                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        }


