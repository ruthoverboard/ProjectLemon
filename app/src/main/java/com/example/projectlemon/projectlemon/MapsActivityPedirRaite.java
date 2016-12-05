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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MapsActivityPedirRaite extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        LocationListener
        , GoogleMap.InfoWindowAdapter
        {

    public AWSHelper awsHelper = AWSHelper.getInstance();
    private GoogleMap mMap;
    Location lastKnownLocation;
    String idUser;
            boolean first = true;
            GroundOverlay driverIcon;

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
        Button pedirRte = (Button) findViewById(R.id.btnRaite);
        pedirRte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AccessToken accessToken = AccessToken.getCurrentAccessToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                try {
                                    idUser = object.getString("id");

                                    String params = "{ "
                                            + "\"id\": " + idUser
                                            + ", \"lat\": " + lastKnownLocation.getLatitude()
                                            + ", \"lon\": " + lastKnownLocation.getLongitude()
                                            + ", \"idFirebase\": " + "\"" + FirebaseInstanceId.getInstance().getToken() + "\""
                                            +  " }";

                                    Log.d("json:", params);
                                    new GetHttpRequest().execute(params);

                                } catch (JSONException e) {

                                }
                                Log.d("json:", object.toString());

                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id");
                request.setParameters(parameters);
                request.executeAsync();

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
            lastKnownLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude()), 17));
        }catch (SecurityException ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }



        mMap.setInfoWindowAdapter(this);


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
                    if(awsHelper.driver != null){
                        if(first) {
                            GroundOverlayOptions driveOptions = new GroundOverlayOptions()
                                    .image(BitmapDescriptorFactory.fromResource(R.mipmap.car_driver))
                                    .position(new LatLng(awsHelper.driver.getLatitude(), awsHelper.driver.getLongitude()),30f,20f);
                            driverIcon = mMap.addGroundOverlay(driveOptions);
                            first = false;
                        }
                        if(Math.abs(Math.abs(location.getLatitude()) - Math.abs(awsHelper.driver.getLatitude())) >= .001 ||
                                Math.abs(Math.abs(location.getLongitude()) - Math.abs(awsHelper.driver.getLongitude())) >= .001)
                        {
                            driverIcon.setPosition(new LatLng(awsHelper.driver.getLatitude(),awsHelper.driver.getLongitude()));
                        }
                    }

                lastKnownLocation = location;
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
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            @Override
            public void onInfoWindowClick(Marker marker) {

            }


            private class GetHttpRequest extends AsyncTask<String, Object, Boolean> {
                @Override
                protected Boolean doInBackground(String... params) {
                    Boolean bool = null;
                    JSONObject obj = null;
                    String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/queueUp";
                    try {
                        obj = new JSONObject(params[0]);

                        StringBuilder sb = new StringBuilder();
                        HttpURLConnection urlConnection = null;
                        URL url2 = new URL(url);
                        urlConnection = (HttpURLConnection) url2.openConnection();
                        urlConnection.setDoOutput(true);
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setUseCaches(false);
                        urlConnection.setConnectTimeout(10000);
                        urlConnection.setReadTimeout(10000);
                        urlConnection.setRequestProperty("Content-Type","application/json");
                        urlConnection.connect();

                        OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                        out.write(obj.toString());
                        out.close();

                        int HttpResult =urlConnection.getResponseCode();
                        if(HttpResult ==HttpURLConnection.HTTP_OK){
                            BufferedReader br = new BufferedReader(new InputStreamReader(
                                    urlConnection.getInputStream(),"utf-8"));
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();

                        }else{

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bool = true;
                    return bool;

                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if (result == true){
                    }
                    else{
                        //startActivity(new Intent(askPhoneNumber.this, firstLogin.class));
                    }
                }
            }
        }


