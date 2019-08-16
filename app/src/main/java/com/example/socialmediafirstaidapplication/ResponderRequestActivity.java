package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResponderRequestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView situationTV, nameTV, addressTV, phoneNumberTV, header, dateTV, currentStatus;
    private Button accept, decline, resolved;
    private String request_id;
    private DatabaseReference firstAidRequest, users;
    private ProgressDialog progressDialog;
    private ScrollView scrollView, buttons;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_request);
        setupUIViews();

        getRequestDetails();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatus(2);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResponderRequestActivity.this, ResponderActivity.class));
            }
        });

        resolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((resolved.getText().toString()).equals("Mark as resolved")) {
                    changeStatus(3);
                }
                else {
                    changeStatus(2);
                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupButtons();
                getRequestDetails();
            }
        });
    }

    private void setupUIViews() {
        request_id = getIntent().getStringExtra("REQUEST_ID");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        header = (TextView)findViewById(R.id.header);
        header.setText(header.getText() + ": " + request_id);
        dateTV = (TextView)findViewById(R.id.date);
        situationTV = (TextView)findViewById(R.id.situationTV);
        nameTV = (TextView)findViewById(R.id.nameTV);
        addressTV = (TextView)findViewById(R.id.addressTV);
        phoneNumberTV = (TextView)findViewById(R.id.phoneNumberTV);
        currentStatus = (TextView)findViewById(R.id.currentStatusTV);

        refresh = (FloatingActionButton)findViewById(R.id.floating_action_button);

        resolved = (Button) findViewById(R.id.revertResolveBtn);
        accept = (Button)findViewById(R.id.acceptBT);
        decline = (Button)findViewById(R.id.declineBT);

        scrollView = (ScrollView)findViewById(R.id.accepted);
        buttons = (ScrollView)findViewById(R.id.buttons);

        progressDialog = new ProgressDialog(this);

        firstAidRequest = FirebaseDatabase.getInstance().getReference("FirstAidRequest");
        users = FirebaseDatabase.getInstance().getReference("Users");

        setupButtons();
    }

    private void getRequestDetails() { //String situation, String name, String address, String phoneNumber
        progressDialog.setMessage("Data is loading. Please wait...");
        progressDialog.show();

        Query request = firstAidRequest.orderByChild("id").limitToFirst(1).equalTo(request_id);
        request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    FirstAidRequest firstAidRequest = snap.getValue(FirstAidRequest.class);

                    dateTV.setText(firstAidRequest.getDateRequested());
                    situationTV.setText((firstAidRequest.getSituation()).isEmpty() ? "Situation not explained" : firstAidRequest.getSituation());
                    nameTV.setText("Name: " + firstAidRequest.getName());
                    addressTV.setText("Address: " + firstAidRequest.getFormattedAddress());
                    phoneNumberTV.setText("Phone Number: " + (firstAidRequest.getPhoneNumber() == null ?  "not provided" : firstAidRequest.getPhoneNumber()));

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

    private void changeStatus(final int newStatus) {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Query request = firstAidRequest.orderByChild("id").limitToFirst(1).equalTo(request_id);
        request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String toastMsg;

                    FirstAidRequest firstAidRequest = snap.getValue(FirstAidRequest.class);
                    firstAidRequest.setStatus(newStatus); //set to accepted

                    if (newStatus == 2){
                        firstAidRequest.setResponderId(firebaseUser.getUid()); //set to responder
                        firstAidRequest.setDateAccepted( dateFormat.format(new Date())); //set date accepted
                        toastMsg = "You have accepted the request. The recipient will be expecting you.";
                    }
                    else {
                        toastMsg = "Status changed successfully.";
                    }

                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("FirstAidRequest").child(request_id);
                    dR.setValue(firstAidRequest);

                    setupButtons();
                    Toast.makeText(ResponderRequestActivity.this, toastMsg, Toast.LENGTH_SHORT);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setupButtons () {
        Query request = firstAidRequest.orderByChild("id").limitToFirst(1).equalTo(request_id);
        request.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot snap : dataSnapshot.getChildren()) {
                   FirstAidRequest request = snap.getValue(FirstAidRequest.class);

                   if (request.getStatus() == 1) {
                        currentStatus.setText("Current Status: SUBMITTED");
                        buttons.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.INVISIBLE);
                   }
                   else if (request.getStatus() == 2) {
                       currentStatus.setText("Current Status: ACCEPTED");
                       resolved.setText("Mark as resolved");
                       scrollView.setVisibility(View.VISIBLE);
                       buttons.setVisibility(View.INVISIBLE);
                   }
                   else {
                       currentStatus.setText("Current Status: RESOLVED");
                       resolved.setText("Revert to previous status");
                       scrollView.setVisibility(View.VISIBLE);
                       buttons.setVisibility(View.INVISIBLE);
                   }

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_responder,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewResponses:
                Intent intent = new Intent(ResponderRequestActivity.this, ResponderActivity.class);
                intent.putExtra("VIEW_MY_RESPONSES", "true");
                startActivity(intent);
                return true;
            case R.id.backHome:
                startActivity(new Intent(ResponderRequestActivity.this, HomeActivity.class));
                return true;
            case R.id.logoutItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                Toast.makeText(ResponderRequestActivity.this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResponderRequestActivity.this, LoginActivity.class));
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
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
