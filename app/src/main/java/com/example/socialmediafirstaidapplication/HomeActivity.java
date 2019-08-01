package com.example.socialmediafirstaidapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    private Button Recipient;
    private Button Responder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupUIViews();

        Recipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RecipientActivity.class));
            }
        });

        Responder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ResponderActivity.class));
            }
        });
    }

    private void setupUIViews() {
        Recipient = (Button)findViewById(R.id.recipientBT);
        Responder = (Button)findViewById(R.id.responderBT);
    }
}
