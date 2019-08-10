package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ResponderRequestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView requestIdTV, situationTV, nameTV, addressTV, phoneNumberTV;
    private String request_id;
    private DatabaseReference firstAidRequest, users;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_request);
        setupUIViews();
        getRequestDetails();

    }

    private void setupUIViews() {
        request_id = getIntent().getStringExtra("REQUEST_ID");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);


        requestIdTV = (TextView)findViewById(R.id.requestIdTV);
        situationTV = (TextView)findViewById(R.id.situationTV);
        nameTV = (TextView)findViewById(R.id.nameTV);
        addressTV = (TextView)findViewById(R.id.addressTV);
        phoneNumberTV = (TextView)findViewById(R.id.phoneNumberTV);

        progressDialog = new ProgressDialog(this);

        firstAidRequest = FirebaseDatabase.getInstance().getReference("FirstAidRequest");
        users = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void getRequestDetails() { //String situation, String name, String address, String phoneNumber
        progressDialog.setMessage("Data is loading. Please wait...");
        progressDialog.show();

        Query request = firstAidRequest.orderByChild("id").equalTo(request_id);

        request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    FirstAidRequest firstAidRequest = snap.getValue(FirstAidRequest.class);

                    requestIdTV.setText("(" + request_id + ")");
                    situationTV.setText(firstAidRequest.getSituation());
                    nameTV.setText("Name: " + firstAidRequest.getName());
                    addressTV.setText("Address: " + firstAidRequest.getFormattedAddress());
                    phoneNumberTV.setText("Phone Number: " + firstAidRequest.getPhoneNumber());

                    LatLng sydney = new LatLng(firstAidRequest.getLatitude(), firstAidRequest.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Location of recipient"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        * */
    }
}
