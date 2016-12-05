package com.example.projectlemon.projectlemon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.android.gms.maps.model.PolylineOptions;

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

//import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener,
        LocationListener {


    private GoogleMap mMap;
    private Location myLocation;
    LatLng latLngCetys = new LatLng(32.50660123141241, -116.92439664155245);
    static AWSHelper awsHelper = AWSHelper.getInstance();
    Double latSend, longSend;
    Location lastKnownLocation;
    int idTrip =1;
    int count = 1;
    String query;
    boolean tripActive = false;
    GroundOverlay driverIcon;
    String Username = "";

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

        getPermissions();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final Button button = (Button) findViewById(R.id.btnRoute);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getRoute();
                new GetHttpRequest().execute();
                new GetRouteRequest().execute();
            }
        });


        final Button buttonHme = (Button) findViewById(R.id.btnHome);
        buttonHme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(MapsActivity.this, UserProfileActivity.class));
            }
        });


        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            Username = object.getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name");
        request.setParameters(parameters);
        request.executeAsync();


    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }else {

            // Permission denied, Disable the functionality that depends on this permission.
            Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
        }
        try {
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            myLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            lastKnownLocation = myLocation;
            awsHelper.driver = myLocation;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),myLocation.getLongitude()), 17));
        }catch (SecurityException ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }catch (Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }


    }



    @Override
    public void onLocationChanged(Location location) {
        //latSend = location.getLatitude();
        //longSend = location.getLongitude();

        if(driverIcon != null) {
            driverIcon.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        if(tripActive){
            try{
                if(Math.abs(Math.abs(location.getLatitude()) - Math.abs(lastKnownLocation.getLatitude())) >= .001 ||
                        Math.abs(Math.abs(location.getLongitude()) - Math.abs(lastKnownLocation.getLongitude())) >= .001)
                {
                    query = idTrip+","+count+","+lastKnownLocation.getLongitude()+","+lastKnownLocation.getLatitude();
                    awsHelper.rec.saveRecord(query, "ProjectLemonStream");
                    awsHelper.rec.submitAllRecords();
                    awsHelper.rec.deleteAllRecords();
                    count++;
                    lastKnownLocation = location;
                }
            }catch (Exception ex){
                Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();

            }
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

    }

    private void getRoute(){/*
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + myLatLng.latitude + "," + myLatLng.longitude +
                "&destination=" + latLngCetys.latitude + "," + latLngCetys.longitude +
                "&key=AIzaSyCq7XqwYUeGOVLqs4FzvjDrYYRGLEar3-A";
        RetrieveFeedTask r = new RetrieveFeedTask();
        r.myLatLng = myLatLng;
        r.latLngCetys = latLngCetys;
        //r.execute();

        query = idTrip+","+count+","+lastKnownLocation.getLongitude()+","+lastKnownLocation.getLatitude()+","+ awsHelper.key;
        awsHelper.rec.saveRecord(query, "ProjectLemonStream");
        awsHelper.rec.submitAllRecords();
        awsHelper.rec.deleteAllRecords();
        count++;
        */Toast.makeText(this, "It WORKS!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        tripActive = true;
        Toast.makeText(this, marker.getTitle(),
                Toast.LENGTH_SHORT).show();

        String params = "{ "
                + "\"nameUser\": " + "\"" + Username + "\""
                + ", \"idFirebase\": " + "\"" + marker.getSnippet() + "\""
                +  " }";

        new PostRequest().execute(params);


    }


    private class GetHttpRequest extends AsyncTask<String, Object, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray response = null;
            Boolean bool = null;

            try {
                String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/searchRider/" + 1;

                response = new JSONArray(HttpRequest.get(url).body());

                if(response != null && response.length() >  0 ){

                    //String idUserDB = response.getJSONObject(0).get("idUser").toString();
                    Log.d("HttpSNAP", response.toString());

                    //if (params[0].equals(idUserDB)) {
                        //Log.d("wtf", params[0]);
                        //startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                        //bool = true;
                    //} else {
                        //startActivity(new Intent(MainActivity.this, firstLogin.class));
                       // bool = false;
                   // }
                }
                else{
                    bool = false;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Log.d("HttpReq", response.toString());
            return response;
            //return response;

        }

        @Override
        protected void onPostExecute(JSONArray result) {
            //Log.d("HttpReq", result.toString());

            for(int i = 0; i < result.length(); i++){

                try {
                    String a = result.getJSONObject(0).get("orderNumber").toString();
                    double lat = Double.parseDouble(result.getJSONObject(i).get("latitudeQueue").toString());
                    double lng = Double.parseDouble(result.getJSONObject(i).get("longitudeQueue").toString());
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(result.getJSONObject(i).get("name").toString())
                            .snippet(result.getJSONObject(i).get("idFirebase").toString()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }




        }
    }

    private class GetRouteRequest extends AsyncTask<String, Object, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray response = null;
            Boolean bool = null;

            try {
                String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/getRoute/" + 1;

                response = new JSONArray(HttpRequest.get(url).body());

                if(response != null && response.length() > 0 ){

                    //String idUserDB = response.getJSONObject(0).get("idUser").toString();
                    Log.d("RouteTrip", response.toString());

                    //if (params[0].equals(idUserDB)) {
                    //Log.d("wtf", params[0]);
                    //startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                    //bool = true;
                    //} else {
                    //startActivity(new Intent(MainActivity.this, firstLogin.class));
                    // bool = false;
                    // }
                }
                else{
                    bool = false;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Log.d("HttpReq", response.toString());
            return response;
            //return response;

        }

        @Override
        protected void onPostExecute(JSONArray result) {
            //Log.d("HttpReq", result.toString());
            LatLng a;
            for(int i = 0; i < result.length(); i++){

                try {
                    double lat = Double.parseDouble(result.getJSONObject(i).get("latitude").toString());
                    double lng = Double.parseDouble(result.getJSONObject(i).get("longitude").toString());
                    if(i > 0) {
                        a = new LatLng(Double.parseDouble(result.getJSONObject(i-1).get("latitude").toString()),
                                Double.parseDouble(result.getJSONObject(i-1).get("longitude").toString()));
                        mMap.addPolyline((new PolylineOptions()
                                .add(a, new LatLng(lat,lng))
                                .width(5)
                                .color(Color.RED)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private class PostRequest extends AsyncTask<String, Object, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean bool = null;
            JSONObject obj = null;
            String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/sendNotification";
            try {
                obj = new JSONObject(params[0]);

/*
                //for testing
                JSONObject jo = new JSONObject();
                jo.put("id", 120678);
                jo.put("name", "Doe");
                jo.put("phoneNumber", "John");
                jo.put("career", "Doesss");
                jo.put("email", "John");
*/
                Log.d("My App", obj.toString());

                StringBuilder sb = new StringBuilder();
                HttpURLConnection urlConnection=null;
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

                    Log.d("woah", ""+sb.toString());
                }else{
                    Log.d("woah", urlConnection.getResponseMessage());

                }


                /*
                Webb webb = Webb.create();
                webb.setDefaultHeader("Connection", "close");
                JSONObject result = webb.post(url)
                        .body(jo)
                        .ensureSuccess()
                        .asJsonObject()
                        .getBody();

                Log.d("WORKS", result.toString());
                */
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("wtf", params[0].toString());
            bool = true;
            return bool;

            //HttpRequest.post(url).send(params[0].toString()).code();
            //response = new JSONArray(HttpRequest.get(url).body());
            //String idUserDB = response.getJSONObject(0).get("idUser").toString();
            //Log.d("HttpSNAP", idUserDB);
            //if (params[0].equals(idUserDB)) {

            //startActivity(new Intent(MainActivity.this, UserProfileActivity.class));

            //} else {
            //startActivity(new Intent(MainActivity.this, firstLogin.class));
            //    bool = false;
            //}


            //Log.d("HttpReq", response.toString());

            //return response;

        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }


}/*
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
            }
    }
*/





