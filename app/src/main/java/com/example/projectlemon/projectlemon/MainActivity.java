package com.example.projectlemon.projectlemon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kevinsawicki.http.HttpRequest;
import com.loopj.android.http.*;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
//comeonnnnn

public class MainActivity extends AppCompatActivity {
//comentario loco
    public CallbackManager callbackManager;
    //public FacebookCallback<LoginResult> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        if(isLoggedIn())
        {
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
        }


        final Button button = (Button) findViewById(R.id.btnMaps);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivityPedirRaite.class));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //query que manda a buscar al usuario

                        new GetHttpRequest().execute();


                        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        Log.d("e", accessToken.toString());
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        String idUser = null;
                                        try {
                                            idUser = object.getString("id");
                                            Log.d("request", idUser);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("json:", object.toString());

                                        String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/searchUser/" + idUser;
                                        Log.d("url", url);
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        final String finalIdUser = idUser;
                                        client.get(url, new JsonHttpResponseHandler() {
                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                                //super.onFailure(statusCode, headers, throwable, errorResponse);
                                                Log.d("nope", String.valueOf(errorResponse));

                                            }
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                                //super.onSuccess(statusCode, headers, response);
                                                Log.d("yay2", response.toString());
                                                try {
                                                    String idUserDB = response.getJSONObject(0).get("idUser").toString();
                                                    Log.d("yay2", idUserDB);
                                                    Log.d("yay22", finalIdUser);
                                                    if(finalIdUser.equals(idUserDB)) {
                                                        Log.d("wtf", finalIdUser);
                                                        startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                                                    }
                                                    else{
                                                        startActivity(new Intent(MainActivity.this, firstLogin.class));
                                                    }
                                                } catch (JSONException e) {
                                                    startActivity(new Intent(MainActivity.this, firstLogin.class));
                                                    e.printStackTrace();
                                                }


                                            }
                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                                Log.d("nope2", String.valueOf(errorResponse));


                                            }
                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                super.onFailure(statusCode, headers, responseString, throwable);
                                                Log.d("nope3", String.valueOf(responseString));
                                                startActivity(new Intent(MainActivity.this, firstLogin.class));
                                            }
                                        });
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id");
                        request.setParameters(parameters);
                        request.executeAsync();

                        //AccessToken accessToken = loginResult.getAccessToken();
                        //Profile profile = Profile.getCurrentProfile();

                        //Bundle pass = new Bundle();
                        //Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                        //pass.putString("token", accessToken.getToken());

                        //intent.putExtras(pass);
                        //intent.putExtra("profile", profile.getName());
                        //startActivity(intent);

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }



    private class GetHttpRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = HttpRequest.get("https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/searchUser/1178164815573359").body();
            Log.d("HttpReq", response);
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("HttpReq", result);
        }
    }

}


