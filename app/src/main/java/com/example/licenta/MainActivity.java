package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
     private  Button btnsetari;
     private ImageButton btnjoc;
     private Button btnprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnsetari= findViewById(R.id.button);
        btnsetari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
        btnjoc=findViewById(R.id.imageButton);
        btnjoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBall();
            }
        });
        btnprofile= findViewById(R.id.button3);
        btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });
    }
    public void openSettings(){
        Intent intent=new Intent(this,Settings.class);
        startActivity(intent);
    }
    public void openBall(){
        Intent intent=new Intent(this,Ball.class);
        startActivity(intent);
    }
    public void openProfile(){
        Intent intent=new Intent(this,Profile.class);
        startActivity(intent);
    }
}
