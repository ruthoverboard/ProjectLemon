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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.amazonaws.mobileconnectors.kinesis.kinesisrecorder.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import java.security.Provider;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private GoogleMap mMap;
    private LatLng myLatLng;
    LatLng latLngCetys = new LatLng(32.50660123141241, -116.92439664155245);
    public static KinesisRecorder recorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle pass = getIntent().getExtras();

        setContentView(R.layout.activity_maps);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final Button button = (Button) findViewById(R.id.btnRoute);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getRoute();
            }
        });

        final Button buttonHme = (Button) findViewById(R.id.btnHome);
        buttonHme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                //Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                //intent.putExtra(AccessToken token, extras.get("token"));
                //intent.putExtra("token", pass.getString("token"));

                //startActivity(intent);
                startActivity(new Intent(MapsActivity.this, UserProfileActivity.class));
            }
        });

        recorder = setUpAWS();



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getPermissions();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
             myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 17));
        }catch (SecurityException ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onLocationChanged(Location location) {

            recorder.saveRecord(location.toString().getBytes(), "ProjectLemonStream");
            //Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();

        try{
            recorder.submitAllRecords();
        }catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    private void getRoute(){
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + myLatLng.latitude + "," + myLatLng.longitude +
                "&destination=" + latLngCetys.latitude + "," + latLngCetys.longitude +
                "&key=AIzaSyCq7XqwYUeGOVLqs4FzvjDrYYRGLEar3-A";
        RetrieveFeedTask r = new RetrieveFeedTask();
        r.myLatLng = myLatLng;
        r.latLngCetys = latLngCetys;
        r.execute();
        Toast.makeText(this, "It WORKS!", Toast.LENGTH_LONG).show();
    }


    public KinesisRecorder setUpAWS(){
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:a0c3cc8d-5bb5-4c28-b4b7-9282805a37d3", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );
        String path = "CetysCarpool";
        KinesisRecorder rec;
        try{
        rec = new KinesisRecorder(
                this.getDir(path, 0), // An empty directory KinesisRecorder can use for storing requests
                Regions.US_EAST_1,  // Region that this Recorder should save and send requests to
                credentialsProvider);
            return rec;

        }catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
            return null;
        }

    }

}
class RetrieveFeedTask extends AsyncTask<Void, Void, String> {
    private Exception exception;
    public LatLng myLatLng;
    public LatLng latLngCetys;

    protected void onPreExecute() {

    }

    protected String doInBackground(Void... urls) {
       //String email = emailText.getText().toString();
        // Do some validation here

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="
                    + myLatLng.latitude + "," + myLatLng.longitude +
                    "&destination=" + latLngCetys.latitude + "," + latLngCetys.longitude +
                    "&key=AIzaSyCq7XqwYUeGOVLqs4FzvjDrYYRGLEar3-A");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            return null;
        }
    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }else {

            String s = response;
        }
        //
        // TODO: check this.exception
        // TODO: do something with the feed
        /*

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                String s = (String) object.get("status");

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
    }



}


