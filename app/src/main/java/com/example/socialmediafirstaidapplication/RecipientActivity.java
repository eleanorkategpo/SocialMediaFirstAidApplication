package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RecipientActivity extends AppCompatActivity {
    private Button Help;
    private EditText Situation;
    private DatabaseReference firstAidRequest;
    private ProgressDialog progressDialog;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        setupUiViews();

        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Processing request..");
                progressDialog.show();

                requestLocation();
            }
        });
    }

    private void setupUiViews() {
        progressDialog = new ProgressDialog(this);
        Help = (Button) findViewById(R.id.helpBT);
        Situation = (EditText) findViewById(R.id.situationET);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
    }

    private void sendHelp(double longitude, double latitude) { //First Aid Request (data): String id, String user_id, double longitude, double latitude, String situation, String responder_id, int status
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = user.getUid();

        String situation = Situation.getText().toString().trim();

        firstAidRequest = FirebaseDatabase.getInstance().getReference("FirstAidRequest");
        String id = firstAidRequest.push().getKey();

        if (longitude != 0 && latitude != 0) {
            FirstAidRequest request = new FirstAidRequest(id, user_id, longitude, latitude, situation, "0", 1);
            firstAidRequest.child(id).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RecipientActivity.this, "Request submitted!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RecipientActivity.this, RecipientSuccessActivity.class));
                        progressDialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RecipientActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(RecipientActivity.this, "Unable to find location. Check gps and try again.",Toast.LENGTH_LONG);
        }
        /*//get user name from here;
        Query currentUser = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("user_id")
                .equalTo(user.getUid());*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    requestLocation();
                return;
        }
    }

    private void requestLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);

                return;
            }
            else {

                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null && locationListener != null) {
                    //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    sendHelp(location.getLongitude(), location.getLatitude());
                }
                else {
                    Toast.makeText(RecipientActivity.this, "Something went wrong while getting location. Check your GPS and try again.", Toast.LENGTH_LONG);
                }
            }
        }
    }
}
