package com.example.licenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Ball extends AppCompatActivity {

    private Button Done;
    private  RadioGroup RadioGroup;
    private FirebaseAuth mAuth;
    private String userID;
    private DatabaseReference currentUserDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball);

        RadioGroup= findViewById(R.id.radioGroup);
        Done=findViewById(R.id.button5);

        mAuth=FirebaseAuth.getInstance();

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectID=RadioGroup.getCheckedRadioButtonId();

                final RadioButton radioButton= findViewById(selectID);

                if(radioButton.getText() == null) {
                    return;
                }
                else{
                    userID=mAuth.getCurrentUser().getUid();
                    currentUserDb=FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userID);
                }
            }
        });
    }
}
