package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResponderActivity extends AppCompatActivity implements FirstAidRequestAdapter.OnRequestListener{
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private List<FirstAidRequest> requestList;
    private FirstAidRequestAdapter requestAdapter;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder);
        setupUIViews();

        getAllRequests();
    }

    private void setupUIViews(){
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        requestList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.requestsRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new FirstAidRequestAdapter(this, requestList, this );
        recyclerView.setAdapter(requestAdapter);
    }

    private void getAllRequests() {  //todo add real data
        progressDialog.setMessage("Your data is loading. Please wait...");
        progressDialog.show();

        Query query = FirebaseDatabase.getInstance().getReference("FirstAidRequest")
                .orderByChild("status")
                .equalTo(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot  snapshot : dataSnapshot.getChildren()) {
                        FirstAidRequest firstAidRequest = snapshot.getValue(FirstAidRequest.class);
                        requestList.add(firstAidRequest);
                    }
                    requestAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
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
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewRequests:
                startActivity(new Intent(ResponderActivity.this, RecipientRequests.class));
                return true;
            case R.id.logoutItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                Toast.makeText(ResponderActivity.this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResponderActivity.this, LoginActivity.class));
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
    public void onRequestClick(int position) {
        FirstAidRequest request = requestList.get(position);
        Intent intent = new Intent(ResponderActivity.this, ResponderRequestActivity.class);
        intent.putExtra("REQUEST_ID", request.getId());
        startActivity(intent);
    }
}
