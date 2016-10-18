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
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        final Bundle extras = getIntent().getExtras();

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
                        ProfilePictureView imgProfile = (ProfilePictureView)findViewById(R.id.img);

                        //txtName.setText(extras.getString("token"));
                        try {
                            txtName.setText(object.getString("name"));

                            JSONObject picture = object.getJSONObject("picture").getJSONObject("data");

                            //new DownloadImageTask((ImageView) findViewById(R.id.imgProfile)).execute(picture.getString("url"));

                            ProfilePictureView profilePictureView;
                            profilePictureView = (ProfilePictureView) findViewById(R.id.img);
                            profilePictureView.setProfileId(object.getString("id"));

                            JSONObject friends = object.getJSONObject("friends");
                            JSONArray friendsData = friends.getJSONArray("data");
                            for (int i=0; i < friendsData.length(); i++) {
                                JSONObject obj = friendsData.getJSONObject(i);
                                txtFriends.setText(txtFriends.getText() + obj.getString("name") + "\n");
                                Log.d("json:", obj.toString());
                            }

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
