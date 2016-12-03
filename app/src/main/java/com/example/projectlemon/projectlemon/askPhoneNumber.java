package com.example.projectlemon.projectlemon;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.goebl.david.Webb;
import com.loopj.android.http.*;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class askPhoneNumber extends AppCompatActivity {
    String phoneNumber;
    String career;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_phone_number);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            career = extras.getString("career");
        }

        Button btnProfile = (Button) findViewById(R.id.btnContinuar);
        btnProfile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(askPhoneNumber.this, UserProfileActivity.class);
                //Bundle bndl = new Bundle();

                EditText phone = (EditText)findViewById(R.id.txtPhone);
                phoneNumber = phone.getText().toString();

                if(phoneNumber != null){
                    //bndl.putString("phoneNumber", phoneNumber);
                    //bndl.putString("career", career);
                    //intent.putExtras(bndl);



                    final String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/createUser";

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
                                        //JSONObject obj = new JSONObject();
                                        /*try {
                                            obj.put("id", object.getString("id"));
                                            obj.put("name", object.getString("name"));
                                            obj.put("phoneNumber", phoneNumber);
                                            obj.put("career", career);
                                            obj.put("email", object.getString("email"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }*/
                                        //String params = null;
                                        String params = "{ "
                                                + "\"id\": " + object.getString("id")
                                                + ", \"name\": " + "\"" + object.getString("name") + "\""
                                                + ", \"phoneNumber\": " + "\"" + phoneNumber + "\""
                                                + ", \"career\": " + "\"" + career + "\""
                                                + ", \"email\": " + "\"" + object.getString("email") + "\""
                                                +  " }";

                                        Log.d("num", phoneNumber);

                                        new GetHttpRequest().execute(params);

                                        /*
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        client.post(getApplicationContext(), url, params, "application/json", new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onStart() {
                                                // Initiated the request
                                            }

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                // Successfully got a response
                                                Log.d("yay", String.valueOf(responseBody));
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                                Log.d("nope", String.valueOf(error));
                                            }

                                        });
                                        */



                                    } catch (JSONException e) {

                                    }
                                    Log.d("json:", object.toString());
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, email, name");
                    request.setParameters(parameters);
                    request.executeAsync();


                    //JSONObject params = new JSONObject();

                    //int a = 123456;
                    //params.put("id", 123456);
                    //params.put("name", "asd");
                    //params.put("email", "asd");
                    //params.put("phoneNumber", "6642214815");
                    //RequestParams params = new RequestParams("par", obj.toString());


                }
                //startActivity(intent);
            }
        });
    }






    private class GetHttpRequest extends AsyncTask<String, Object, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean bool = null;
            JSONObject obj = null;
            String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/createUser";
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
                Log.d("phonetype value ", obj.getString("career"));

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
            if (result == true){
                startActivity(new Intent(askPhoneNumber.this, UserProfileActivity.class));
            }
            else{
                //startActivity(new Intent(askPhoneNumber.this, firstLogin.class));
            }
        }
    }
}
