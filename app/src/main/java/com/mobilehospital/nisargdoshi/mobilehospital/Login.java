package com.mobilehospital.nisargdoshi.mobilehospital;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Login extends AppCompatActivity   {
    private String first_name,last_name;
    EditText username,password;
    public Button signin_btn;
    private SignInButton google_btn;
    private int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    public ProgressDialog mprog1;
    private Firebase mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        signin_btn=(Button)findViewById(R.id.btn_login);
        username=(EditText)findViewById(R.id.et_username_login);
        password=(EditText)findViewById(R.id.et_pwd_login);
        final Intent getintent=getIntent();
        final String usertype=getintent.getStringExtra("usertype");

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(usertype.equals("Patient")) {
                    Intent create_new_user_intent = new Intent(Login.this, create_new_user.class);
                    create_new_user_intent.putExtra("usertype", usertype);
                    startActivity(create_new_user_intent);
                }
                else {
                    Intent create_new_user_intent = new Intent(Login.this, patient_Signup.class);
                    create_new_user_intent.putExtra("usertype", usertype);
                    startActivity(create_new_user_intent);
                }
            }
        });
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

if(TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString()))
{Toast.makeText(getBaseContext(),"please Enter username and password",Toast.LENGTH_LONG).show();}
else {
    mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                Toast.makeText(getBaseContext(), "login", Toast.LENGTH_LONG).show();
                // mprog.dismiss();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("user_id", mAuth.getCurrentUser().getUid());
                editor.putString("user_type", usertype);
                //editor.putString("user_login", username.getText().toString());
                editor.commit();
                Intent Home_int = new Intent(getBaseContext(), Home.class);
                startActivity(Home_int);


            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                alertDialog.setTitle("Incorrect Username and Password");
                alertDialog.setMessage("The username and password you entered dosen't appear to belong to an account.\n" +
                        "Please check username and password and try again.");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                //mprog.dismiss();
                alertDialog.show();
            }

        }
    });
}     //Toast.makeText(getBaseContext(),":f",Toast.LENGTH_LONG).show();
            }
        });


    }

    private boolean checkConnection() {     //for checking internet connection
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }


}
