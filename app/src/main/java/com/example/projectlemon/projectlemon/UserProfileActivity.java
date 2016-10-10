package com.example.projectlemon.projectlemon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

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
                        ImageView imgProfile = (ImageView)findViewById(R.id.imgProfile);
                        //txtName.setText(extras.getString("token"));
                        try {
                            txtName.setText(object.getString("name"));

                            JSONObject picture = object.getJSONObject("picture").getJSONObject("data");
                            try{
                                InputStream is = (InputStream) new URL(picture.getString("url")).getContent();
                                Drawable profilePic = Drawable.createFromStream(is, "src name");
                                imgProfile.setImageDrawable(profilePic);
                            }catch (Exception e) {
                                Log.d("URL:", picture.getString("url"));
                                Log.d("URL:", e.toString());
                            }

                            //imgProfile.setImageURI(Uri.parse(picture.getString("url")));


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
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,friends,picture");
        request.setParameters(parameters);
        request.executeAsync();




    }
}
