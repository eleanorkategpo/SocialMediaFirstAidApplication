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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class RecipientSuccessActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String request_id;
    private TextView statusId;
    private FirebaseAuth firebaseAuth;
    private StateProgressBar stateProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_success);
        setupUIViews();
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
        progressDialog = new ProgressDialog(this);
        request_id = getIntent().getStringExtra("REQUEST_ID");

        statusId = (TextView) findViewById(R.id.requestId);
        statusId.setText("Request ID: " + request_id);

        firebaseAuth = FirebaseAuth.getInstance();

        String[] descriptionData = {"Submitted", "Accepted", "Resolved"};
        stateProgressBar = (StateProgressBar) findViewById(R.id.statusStepView);
        stateProgressBar.setStateDescriptionData(descriptionData);

        setState();
    }

    private void setState() {
        //set state here
        //stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
    }
}


