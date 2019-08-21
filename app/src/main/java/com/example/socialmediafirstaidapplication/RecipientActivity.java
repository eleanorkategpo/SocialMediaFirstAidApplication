package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecipientActivity extends AppCompatActivity {
    private Button Help;
    private EditText Situation;
    private DatabaseReference firstAidRequest;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        setupUiViews();

        Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Processing request...");
                progressDialog.show();

                String currentSituation = Situation.getText().toString();
                if (currentSituation.isEmpty()){
                    Toast.makeText(RecipientActivity.this, "Kindly explain the situation.", Toast.LENGTH_SHORT).show();
                }
                else {
                    requestLocation();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recipient,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backHome:
                startActivity(new Intent(RecipientActivity.this, HomeActivity.class));
                return true;
            case R.id.viewRequests:
                startActivity(new Intent(RecipientActivity.this, RecipientRequests.class));
                return true;
            case R.id.logoutItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                Toast.makeText(RecipientActivity.this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RecipientActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupUiViews() {
        firebaseAuth = FirebaseAuth.getInstance();
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

        firstAidRequest = FirebaseDatabase.getInstance().getReference("FirstAidRequest");

        requestQueue = Volley.newRequestQueue(this);
    }

    private void sendHelp(final double longitude, final double latitude, final String formattedAddress) { //First Aid Request (data): String id, String user_id, String user_name, String situation, String responder_id, double longitude, double latitude, int status
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = user.getUid();
        final String situation = Situation.getText().toString().trim();
        final String id = firstAidRequest.push().getKey();

        Query currentUser = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("user_id")
                .equalTo(user.getUid());

        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    String user_name = user.getName();
                    String phoneNumber = user.getPhoneNumber();

                    if (longitude != 0 && latitude != 0) {
                        //String id, String user_id, String user_name, String situation, String responder_id, double longitude, double latitude,
                        // String formattedAddress, Sting phoneNumber, String dateRequested, int status

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        FirstAidRequest request = new FirstAidRequest(id, user_id, user_name, situation, "0", longitude, latitude, formattedAddress, phoneNumber, dateFormat.format(new Date()),null,  1);
                                firstAidRequest.child(id).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RecipientActivity.this, "Request submitted!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();

                                            Intent intent = new Intent(RecipientActivity.this, RecipientSuccessActivity.class);
                                            intent.putExtra("REQUEST_ID", id);
                                            startActivity(intent);
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    String formattedAddress = getFormattedAddress(location.getLongitude(), location.getLatitude());

                    sendHelp(location.getLongitude(), location.getLatitude(), formattedAddress);
                }
                else {
                    Toast.makeText(RecipientActivity.this, "Something went wrong while getting location. Check your GPS and try again.", Toast.LENGTH_LONG);
                }
            }
        }
    }

    private String getFormattedAddress(double longitude, double latitude){
        String address = "";
        Geocoder geocoder = new Geocoder(RecipientActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0);
           // myCity = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }
}
