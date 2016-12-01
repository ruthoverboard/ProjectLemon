package com.example.projectlemon.projectlemon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.regions.Regions;
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
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cz.msebera.android.httpclient.Header;


public class UserProfileActivity extends AppCompatActivity {

    String career;
    String[] data;

    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        //Bundle extras = getIntent().getExtras();


        //if(extras != null){
            //career = extras.getString("career");
        //}


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
            }
        });



        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        TextView txtName = (TextView) findViewById(R.id.txtName);
                        TextView txtFriends = (TextView) findViewById(R.id.txtFriends);
                        //ProfilePictureView imgProfile = (ProfilePictureView)findViewById(R.id.img);
                        final TextView txtCareer = (TextView)findViewById(R.id.txtCareer);

                        //if(career != null){
                            //txtCareer.setText(career);
                        //}
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


                            String url = "https://p4x0vleufi.execute-api.us-east-1.amazonaws.com/dev/searchUser/" + idUser;
                            Log.d("url", url);
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



                        } catch (JSONException e) {
                            txtFriends.setText("fallo");
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

}
