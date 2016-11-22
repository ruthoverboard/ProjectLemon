package com.example.projectlemon.projectlemon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
                Intent intent = new Intent(askPhoneNumber.this, UserProfileActivity.class);
                Bundle bndl = new Bundle();

                if(phoneNumber != null){
                    bndl.putString("phoneNumber", phoneNumber);
                    bndl.putString("career", career);
                    intent.putExtras(bndl);
                }
                startActivity(intent);
            }
        });
    }
}
