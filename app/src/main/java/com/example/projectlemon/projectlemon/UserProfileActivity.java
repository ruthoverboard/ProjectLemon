package com.example.projectlemon.projectlemon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.kevinsawicki.http.HttpRequest;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import cz.msebera.android.httpclient.Header;


public class UserProfileActivity extends AppCompatActivity {

    String career;
    String[] data;
    public CallbackManager callbackManager;

    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);


        callbackManager = CallbackManager.Factory.create();
        //Bundle extras = getIntent().getExtras();


        //if(extras != null){
            //career = extras.getString("career");
        //}

        /*
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //query que manda a buscar al usuario
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
*/

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    //write your code here what to do when user logout
                    //accessTokenTracker.stopTracking();
                    startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
                }
            }
        };

        accessTokenTracker.startTracking();;

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        //ProfilePictureView imgProfile = (ProfilePictureView)findViewById(R.id.img);

                        //if(career != null){
                        //txtCareer.setText(career);
                        //}

                        TextView txtName = (TextView) findViewById(R.id.txtName);
                        TextView txtFriends = (TextView) findViewById(R.id.txtFriends);
                        //final TextView txtCareer = (TextView)findViewById(R.id.txtCareer);


                        try {
                            txtName.setText(object.getString("name"));

                            JSONObject picture = object.getJSONObject("picture").getJSONObject("data");

                            //new DownloadImageTask((ImageView) findViewById(R.id.imgProfile)).execute(picture.getString("url"));

                            ProfilePictureView profilePictureView;
                            profilePictureView = (ProfilePictureView) findViewById(R.id.img);
                            profilePictureView.setProfileId(object.getString("id"));

                            final String idUser = object.getString("id");
                            JSONObject friends = object.getJSONObject("friends");
                            JSONArray friendsData = friends.getJSONArray("data");
                            for (int i=0; i < friendsData.length(); i++) {
                                JSONObject obj = friendsData.getJSONObject(i);
                                txtFriends.setText(txtFriends.getText() + obj.getString("name") + "\n");
                                Log.d("json:", obj.toString());
                            }


                            new GetHttpRequest().execute(idUser);

                            String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/searchUser/" + idUser;
                            Log.d("url", url);


                            /*
                            AsyncHttpClient client = new AsyncHttpClient();
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
                                        txtCareer.setText(response.getJSONObject(0).get("career").toString());

                                    } catch (JSONException e) {
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
                                }
                            });

*/


                        } catch (JSONException e) {
                            //txtFriends.setText("fallo");
                        }
                        Log.d("json:", object.toString());



                    }
                });
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://www.facebook.com/CetysCarPool/"))
                .build();

        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_btn);
        shareButton.setShareContent(content);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,friends,picture");
        request.setParameters(parameters);
        request.executeAsync();

        //base de datos

        //Dar raite
        Button btnMaps = (Button) findViewById(R.id.btnDarRaite);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(UserProfileActivity.this, MapsActivity.class));
            }
        });

        Button btnGetRaite = (Button) findViewById(R.id.btnPedirRaite);
        btnGetRaite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                startActivity(new Intent(UserProfileActivity.this, MapsActivityPedirRaite.class));
            }
        });

    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
*/

    //new DownloadImageTask((ImageView) findViewById(R.id.imgProfile)).execute(picture.getString("url"));
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            bmImage.setImageBitmap(result);
            bmImage.setAdjustViewBounds(true);
            bmImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }


    private class GetHttpRequest extends AsyncTask<String, Object, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray response = null;
            Boolean bool = null;

            try {
                String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/searchUser/" + params[0];

                response = new JSONArray(HttpRequest.get(url).body());
                String idUserDB = response.getJSONObject(0).get("idUser").toString();
                Log.d("HttpSNAP", idUserDB);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("HttpReq", response.toString());
            return response;
            //return response;

        }

        @Override
        protected void onPostExecute(JSONArray result) {
            Log.d("HttpReq", result.toString());
            try {
                final TextView txtCareer = (TextView)findViewById(R.id.txtCareer);
                txtCareer.setText(result.getJSONObject(0).get("career").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
