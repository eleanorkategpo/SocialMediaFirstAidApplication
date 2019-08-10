package com.example.socialmediafirstaidapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    private EditText Name, Birthday, Email, Password, PhoneNumber;
    private RadioButton Female, Male;
    private Button Register;
    private TextView Signin;
    private CheckBox Responder;
    private FirebaseAuth firebaseAuth;
    private DatePickerDialog datePickerDialog;
    private ProgressDialog progressDialog;
    private DatabaseReference userTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()){
                    //upload to database
                    createEmailAndPassword();
                    progressDialog.setMessage("Creating user...");
                    progressDialog.show();
                }
            }
        });

        Birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Birthday.setText((month+1) + "/" + day + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        Female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Male.setChecked(false);
            }
        });

        Male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Female.setChecked(false);
            }
        });
    }

    private void setupUIViews() {
        Name = (EditText)findViewById(R.id.nameET);
        Female = (RadioButton)findViewById(R.id.femaleRB);
        Male = (RadioButton)findViewById(R.id.maleRB);
        Birthday = (EditText)findViewById(R.id.birthdayET) ;
        Email = (EditText)findViewById(R.id.emailET);
        Password = (EditText)findViewById(R.id.passwordET);
        Register = (Button)findViewById(R.id.loginBT);
        Signin = (TextView)findViewById(R.id.siginTV);
        Responder = (CheckBox)findViewById(R.id.isResponderCB);
        PhoneNumber = (EditText)findViewById(R.id.addressET);

        Birthday.setShowSoftInputOnFocus(false);
    }

    private boolean validate(){
        String name = Name.getText().toString();
        String birthday = Birthday.getText().toString().trim();
        String email = Email.getText().toString();
        String password = Password.getText().toString();
        String email_regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        String phone_number = PhoneNumber.getText().toString();

        if (email.isEmpty() || password.isEmpty() || (!Female.isChecked() && !Male.isChecked()) || birthday.isEmpty() || name.isEmpty() || phone_number.isEmpty() ) {
            Toast.makeText(this, "Please input all the details.", Toast.LENGTH_SHORT);
        }
        else if (password.length() < 6) {
            Toast.makeText(this, "Password should have 6 or more characters.", Toast.LENGTH_SHORT);
        }
        else if (!email.matches(email_regex)) {
            Toast.makeText(this, "Email address is not valid.", Toast.LENGTH_SHORT);
        }
        //check date validity else if ()
        else {
            return true;
        }
        return true;
    }

    private void createUser(String user_id){
        String name = Name.getText().toString().trim();
        String email = Email.getText().toString().trim();
        String gender = (Female.isChecked()) ? "Female" : "Male";
        String birthday = Birthday.getText().toString().trim();
        String phone_number = PhoneNumber.getText().toString();
        boolean isResponder = Responder.isChecked();

        userTable = FirebaseDatabase.getInstance().getReference("Users");
        String id = userTable.push().getKey();

        User user = new User(id, user_id, name, email, gender, birthday, isResponder, phone_number);

        userTable.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    progressDialog.dismiss();
                }

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void createEmailAndPassword() {
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    createUser(user.getUid());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}
