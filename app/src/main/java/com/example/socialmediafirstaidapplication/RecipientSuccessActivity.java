package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.Date;


public class RecipientSuccessActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String request_id;
    private TextView statusId, responderPhone, responderName, dateAccepted;
    private Button resolve;
    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private StateProgressBar stateProgressBar;
    private DatabaseReference firstAidRequest;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_success);
        setupUIViews();

        resolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstAidRequest.child(request_id).child("status").setValue((resolve.getText() == "Mark as Resolved") ? 3 : 2);
                setState();
                Toast.makeText(RecipientSuccessActivity.this, "Status of request has been updated...", Toast.LENGTH_SHORT);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setState();
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
                startActivity(new Intent(RecipientSuccessActivity.this, HomeActivity.class));
                return true;
            case R.id.viewRequests:
                startActivity(new Intent(RecipientSuccessActivity.this, RecipientRequests.class));
                return true;
            case R.id.logoutItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                Toast.makeText(RecipientSuccessActivity.this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RecipientSuccessActivity.this, LoginActivity.class));
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

    private void setupUIViews() {
        resolve = (Button) findViewById(R.id.resolvedBtn);
        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        progressDialog = new ProgressDialog(this);

        scrollView = (ScrollView) findViewById(R.id.acceptedMessage);

        request_id = getIntent().getStringExtra("REQUEST_ID");

        statusId = (TextView) findViewById(R.id.requestId);
        statusId.setText("Request ID: " + request_id);

        dateAccepted = (TextView) findViewById(R.id.dateAccepted);
        responderName = (TextView) findViewById(R.id.responderName);
        responderPhone = (TextView) findViewById(R.id.responderPhone);

        firebaseAuth = FirebaseAuth.getInstance();
        firstAidRequest = FirebaseDatabase.getInstance().getReference("FirstAidRequest");

        String[] descriptionData = {"Submitted", "Accepted", "Resolved"};
        stateProgressBar = (StateProgressBar) findViewById(R.id.statusStepView);
        stateProgressBar.setStateDescriptionData(descriptionData);

        setState();
    }

    private void setState() {
        Query request = firstAidRequest
                        .orderByChild("id")
                        .equalTo(request_id);

        request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    FirstAidRequest firstAidRequest = snap.getValue(FirstAidRequest.class);
                    int status = firstAidRequest.getStatus();

                    if (status == 1) {
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                        resolve.setVisibility(View.INVISIBLE);
                    }
                    else if (status == 2) {
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                        getResponderDetails();
                        resolve.setText("Mark as Resolved");
                        resolveRevert(3);

                        resolve.setVisibility(View.VISIBLE);
                    }
                    else {
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                        getResponderDetails();
                        resolve.setText("Revert to Previous Status");
                        resolveRevert(2);

                        resolve.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getResponderDetails (){
        progressDialog.setMessage("Your data is loading. Please wait...");
        progressDialog.show();

        Query query = FirebaseDatabase.getInstance().getReference("FirstAidRequest")
                .orderByChild("id")
                .limitToFirst(1)
                .equalTo(request_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot  snapshot : dataSnapshot.getChildren()) {
                        FirstAidRequest firstAidRequest = snapshot.getValue(FirstAidRequest.class);
                        final String responderId = firstAidRequest.getResponder_id();
                        dateAccepted.setText(firstAidRequest.getDateAccepted());
                        getResponderInfo(responderId);
                    }
                }
                else {
                    Toast.makeText(RecipientSuccessActivity.this, "Something went wrong.", Toast.LENGTH_SHORT);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getResponderInfo(String responderId) {
        Query userQuery = FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("user_id")
                .limitToFirst(1)
                .equalTo(responderId);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        responderName.setText(user.getName());
                        responderPhone.setText("Contact Number: " + user.getPhoneNumber());

                        //done
                        scrollView.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                }
                else {
                    Toast.makeText(RecipientSuccessActivity.this, "Something went wrong.", Toast.LENGTH_SHORT);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void resolveRevert(final int newStatus) {

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Query request = firstAidRequest.orderByChild("id").limitToFirst(1).equalTo(request_id);
        request.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    FirstAidRequest firstAidRequest = snap.getValue(FirstAidRequest.class);
                    firstAidRequest.setStatus(newStatus); //set to accepted

                    DatabaseReference dR = FirebaseDatabase.getInstance().getReference("FirstAidRequest").child(request_id);
                    dR.setValue(firstAidRequest);

                    Toast.makeText(RecipientSuccessActivity.this, "Status changed successfully.", Toast.LENGTH_SHORT);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}


