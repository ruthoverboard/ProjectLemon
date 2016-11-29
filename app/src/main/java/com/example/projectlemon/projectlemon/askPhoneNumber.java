package com.example.projectlemon.projectlemon;

import android.content.Context;
import android.content.Intent;
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

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

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
        EditText phone = (EditText)findViewById(R.id.txtPhone);
        phoneNumber = phone.getText().toString();



        Button btnProfile = (Button) findViewById(R.id.btnContinuar);
        btnProfile.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(askPhoneNumber.this, UserProfileActivity.class);
                Bundle bndl = new Bundle();

                if(phoneNumber != null){
                    bndl.putString("phoneNumber", phoneNumber);
                    bndl.putString("career", career);
                    intent.putExtras(bndl);

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
                                        StringEntity params = null;
                                        try {
                                            params = new StringEntity("{ "
                                                    + "\"id\": " + object.getString("id")
                                                    + ", \"name\": " + "\"" + object.getString("name") + "\""
                                                    + ", \"phoneNumber\": " + "\"" + 98765421 + "\""
                                                    + ", \"career\": " + "\"" + career + "\""
                                                    + ", \"email\": " + "\"" + object.getString("email") + "\""
                                                    +  " }");

                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("num", phoneNumber);
                                        Log.d("str", "{ "
                                                + "\"id\": " + object.getString("id")
                                                + ", \"name\": " + "\"" + object.getString("name") + "\""
                                                + ", \"phoneNumber\": " + "\"" + phoneNumber + "\""
                                                + ", \"career\": " + "\"" + career + "\""
                                                + ", \"email\": " + "\"" + object.getString("email") + "\""
                                                +  " }");
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


}
