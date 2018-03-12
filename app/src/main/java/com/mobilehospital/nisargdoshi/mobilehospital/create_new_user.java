package com.mobilehospital.nisargdoshi.mobilehospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

public class create_new_user extends AppCompatActivity {
    EditText firstname, lastname, email, password, dateofbirth;
    Button createaccount;
    Boolean checkFormat = false;
    String emailPattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
    String datepattern = "([1-9]{1}|[0]{1}[1-9]{1}|[1]{1}[0-9]{1}|[2]{1}[0-9]{1}|[3]{1}[0-1]{1})" +
            "([/]{1})" +
            "([0]{1}[1-9]{1}|[1]{1}[0-2]{1}|[1-9]{1})" +
            "([/]{1})" +
            "([19]{2}[0-9]{2}|[20]{2}[0-9]{2})";
    String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private ProgressDialog mprog;
    private DatabaseReference mdatabase;

    private Firebase mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user);
        setTitle("Create new account");
        firstname = (EditText) findViewById(R.id.et_patient_fname);
        lastname = (EditText) findViewById(R.id.et_patient_lname);
        email = (EditText) findViewById(R.id.et_patient_uname);
        password = (EditText) findViewById(R.id.et_patient_pwd);
        dateofbirth = (EditText) findViewById(R.id.et_patient_dob);
        createaccount = (Button) findViewById(R.id.btn_patinent_crtacc);
        Intent getintent=getIntent();
        final String usertype=getintent.getStringExtra("usertype");
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mref = new Firebase("https://mobile-hospital.firebaseio.com/users/"+usertype);


        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(TextUtils.isEmpty(firstname.getText().toString()) ||TextUtils.isEmpty(lastname.getText().toString())|| TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(dateofbirth.getText().toString()))
               {
                   Toast.makeText(getBaseContext(), "All fieds are mendetory", Toast.LENGTH_LONG).show();
               }
                else  if((!String.valueOf(dateofbirth.getText()).matches(datepattern)) ||!String.valueOf(email.getText()).matches(emailPattern) || !String.valueOf(password.getText()).matches(PASSWORD_PATTERN))
                    Toast.makeText(getBaseContext(), "invalid", Toast.LENGTH_LONG).show();
                else{

                  Toast.makeText(getBaseContext(), "valid", Toast.LENGTH_LONG).show();
                  startRegister();
                }

            }
        });
    }


    public void startRegister() {


        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            String user_id=mAuth.getCurrentUser().getUid();

                            Firebase c=mref.child(user_id);
                            c.child("First Name").setValue(firstname.getText().toString());
                            c.child("Last Name").setValue(lastname.getText().toString());
                            c.child("Date of bith").setValue(dateofbirth.getText().toString());
                            FirebaseUser user = mAuth.getCurrentUser();

                             user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getBaseContext(),"sent mail",Toast.LENGTH_LONG);
                                            }
                                        }
                                    });
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}