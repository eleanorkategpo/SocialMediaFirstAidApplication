package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RecipientActivity extends AppCompatActivity {
    private Button Help;
    private TextView ConfirmMessage;
    private EditText Situation;
    private DatabaseReference users;

    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        setupUiViews();

        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendHelp();
            }
        });
    }

    private void setupUiViews() {
        Help = (Button) findViewById(R.id.helpBT);
        ConfirmMessage = (TextView) findViewById(R.id.confirmMessageTV);
        Situation = (EditText) findViewById(R.id.situationET);
    }

    private void sendHelp() { //user_id, name, longitude, latitude, situation, responder_id, resolved
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        String situation = Situation.getText().toString().trim();

        //get user name from here;
        Query currentUser = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("user_id")
                .equalTo(user.getUid());

        //get locationhere
    }
}
