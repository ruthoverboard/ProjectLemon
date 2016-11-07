package com.example.projectlemon.projectlemon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

import static com.example.projectlemon.projectlemon.R.styleable.View;

public class firstLogin extends AppCompatActivity {

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    public String career;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);


        Button btnMaps = (Button) findViewById(R.id.btnContinuar);
        btnMaps.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(firstLogin.this, UserProfileActivity.class);
                Bundle bndl = new Bundle();

                if(career != null){
                    //intent.putExtra("Career", career);
                    bndl.putString("Career", career);
                    intent.putExtras(bndl);
                }
                startActivity(intent);
            }
        });

        spinner = (Spinner)findViewById(R.id.spnnrCareer);
        adapter = ArrayAdapter.createFromResource(this, R.array.careersArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " seleccionada.", Toast.LENGTH_LONG).show();
                career = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
    }
}
