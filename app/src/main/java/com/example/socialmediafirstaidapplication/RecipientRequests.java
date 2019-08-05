package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RecipientRequests extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private FirstAidRequestAdapter requestAdapter;
    private List<FirstAidRequest> requestList;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_requests);
        setupUIViews();
        getRequestsByUser();
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
                startActivity(new Intent(RecipientRequests.this, RecipientRequests.class));
                return true;
            case R.id.logoutItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                Toast.makeText(RecipientRequests.this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RecipientRequests.this, LoginActivity.class));
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


    private void setupUIViews(){
        firebaseAuth = firebaseAuth.getInstance();
        user_id = firebaseAuth.getUid();

        requestList = new ArrayList<>();
        getRequestsByUser();

        recyclerView = (RecyclerView)findViewById(R.id.requestsRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestAdapter = new FirstAidRequestAdapter(this,requestList );
        recyclerView.setAdapter(requestAdapter);
    }

    private void getRequestsByUser() {  //todo add real data

    }
}
