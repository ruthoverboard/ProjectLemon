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

                EditText phone = (EditText)findViewById(R.id.txtPhone);
                phoneNumber = phone.getText().toString();

                if(phoneNumber != null){

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
                                        String params = "{ "
                                                + "\"id\": " + object.getString("id")
                                                + ", \"name\": " + "\"" + object.getString("name") + "\""
                                                + ", \"phoneNumber\": " + "\"" + phoneNumber + "\""
                                                + ", \"career\": " + "\"" + career + "\""
                                                + ", \"email\": " + "\"" + object.getString("email") + "\""
                                                +  " }";

                                        new GetHttpRequest().execute(params);

                                    } catch (JSONException e) {

                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, email, name");
                    request.setParameters(parameters);
                    request.executeAsync();


                }
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
                startActivity(new Intent(askPhoneNumber.this, UserProfileActivity.class));
            }
        }
    }
}
