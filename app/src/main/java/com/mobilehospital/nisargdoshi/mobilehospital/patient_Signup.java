package com.mobilehospital.nisargdoshi.mobilehospital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;

public class patient_Signup extends AppCompatActivity {

    EditText fname, lname, email, password, location, contacnumber,uprn;
    Spinner category;
    Button approved;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private ProgressDialog mprog;
    private DatabaseReference mdatabase;
    private Firebase mref;
    TextView status;
    final Context context = this;
    ArrayList<String> userlist=new ArrayList();
    String emailPattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
    String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    String contact_number="^[6-9]\\d{9}$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient__signup);

        fname = (EditText) findViewById(R.id.et_exp_fname);
        lname = (EditText) findViewById(R.id.et_exp_lname);
        email = (EditText) findViewById(R.id.et_exp_email);
        password = (EditText) findViewById(R.id.et_exp_pwd);
        location = (EditText) findViewById(R.id.et_exp_location);
        contacnumber = (EditText) findViewById(R.id.et_exp_contact);
        uprn=(EditText)findViewById(R.id.et_exp_uprn);
        status = (TextView) findViewById(R.id.tv_check_status);
        approved = (Button) findViewById(R.id.btn_apply_approve);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mref = new Firebase("https://mobile-hospital.firebaseio.com/users/tempexp");

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getBaseContext(),"okay",Toast.LENGTH_LONG).show();

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.status_check, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                 final EditText userInput = (EditText) promptsView.findViewById(R.id.et_email_st_check);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("Check Aproval")

                        .setPositiveButton("Check",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        final Firebase c = mref.child(userInput.getText().toString());
                                        c.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {

                                                String Status = dataSnapshot.getValue(String.class);
                                                Toast.makeText(getBaseContext(), Status, Toast.LENGTH_SHORT).show();

                                                if (Status.equals("1")) {
                                                    Firebase ref = new Firebase("https://mobile-hospital.firebaseio.com/users/tempexp/" + userInput.getText().toString());

                                                    ref.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot snapshot) {
                                                            Log.e("Count ", "" + snapshot.getChildrenCount());

                                                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                                userlist.add(String.valueOf(postSnapshot.getValue()));
                                                            }

                                                            Intent i = new Intent(patient_Signup.this, statuscheck.class);
                                                            i.putStringArrayListExtra("data", userlist);
                                                            i.putExtra("uprn", userInput.getText().toString());
                                                            startActivity(i);

                                                            Toast.makeText(getBaseContext(), userlist.get(0), Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onCancelled(FirebaseError firebaseError) {
                                                            Log.e("The read failed: ", firebaseError.getMessage());
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(getBaseContext(), "still panding", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else
                                                {
                                                    Toast.makeText(getBaseContext(),"Sorry no as such data exist",Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(FirebaseError firebaseError) {

                                            }
                                        });

                                    }

//                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();



            }
        });

        approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(fname.getText().toString()) || TextUtils.isEmpty(lname.getText().toString()) || TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(location.getText().toString()) || TextUtils.isEmpty(contacnumber.getText().toString()) || TextUtils.isEmpty(uprn.getText().toString())) {
                    Toast.makeText(getBaseContext(), "All fieds are mendetory", Toast.LENGTH_LONG).show();
                }
                else if((!String.valueOf(contacnumber.getText()).matches(contact_number)) || !String.valueOf(email.getText()).matches(emailPattern) || !String.valueOf(password.getText()).matches(PASSWORD_PATTERN))
                {        Toast.makeText(getBaseContext(), "invalid", Toast.LENGTH_LONG).show();
            }
            else
                {
                Firebase c = mref.child(uprn.getText().toString());
                c.child("status").setValue("0");
                c.child("First Name").setValue(fname.getText().toString());
                c.child("Last Name").setValue(lname.getText().toString());
                c.child("email").setValue(email.getText().toString());
                c.child("password").setValue(password.getText().toString());
                c.child("Location").setValue(location.getText().toString());
                c.child("Contact number").setValue(contacnumber.getText().toString());
                    Toast.makeText(getBaseContext(), "we will analysis your profile", Toast.LENGTH_LONG).show();

            }
            }
        });
    }
}
