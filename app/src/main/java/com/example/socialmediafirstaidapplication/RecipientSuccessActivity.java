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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class RecipientSuccessActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String request_id;
    private TextView statusId;
    private Button resolve;
    private ImageView refresh;
    private FirebaseAuth firebaseAuth;
    private StateProgressBar stateProgressBar;
    private DatabaseReference firstAidRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_success);
        setupUIViews();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setState();
            }
        });

        resolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstAidRequest.child(request_id).child("status").setValue((resolve.getText() == "Mark as Resolved") ? 3 : 2);
                setState();
                Toast.makeText(RecipientSuccessActivity.this, "Status of request has been updated...", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
        refresh = (ImageView) findViewById(R.id.refreshBtn);
        resolve = (Button) findViewById(R.id.resolvedBtn);

        progressDialog = new ProgressDialog(this);
        request_id = getIntent().getStringExtra("REQUEST_ID");

        statusId = (TextView) findViewById(R.id.requestId);
        statusId.setText("Request ID: " + request_id);

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
                        resolve.setText("Mark as Resolved");
                        resolve.setVisibility(View.VISIBLE);
                    }
                    else {
                        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
                        resolve.setText("Revert to Previous Status");

                        resolve.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


