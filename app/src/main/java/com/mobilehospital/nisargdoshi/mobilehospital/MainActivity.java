package com.mobilehospital.nisargdoshi.mobilehospital;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.CAPTURE_VIDEO_OUTPUT;
import static android.Manifest.permission.READ_SMS;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private GoogleApiClient mGoogleApiClient;
    private Firebase mref;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private View view;
    public String userid,usertype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        userid=pref.getString("user_id", null);
        usertype=pref.getString("user_type", null);
        if(userid!=null)
        {
            if(usertype.equals("Patient")) {
                Intent create_new_user_intent = new Intent(MainActivity.this, Home.class);
                create_new_user_intent.putExtra("usertype", usertype);
                startActivity(create_new_user_intent);
            }
            else {
                Intent create_new_user_intent = new Intent(MainActivity.this,Home_doctor.class);
                create_new_user_intent.putExtra("usertype", usertype);
                startActivity(create_new_user_intent);
            }
        }
        else {

            if (!checkPermission()) {

                requestPermission();


            } else {

                Toast.makeText(getApplication(), "permission Granted" + userid + "" + usertype, Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent splesh_intent = new Intent(MainActivity.this, usertype.class);
                        startActivity(splesh_intent);
                    }
                }, 3000);
            }
        }
        Firebase.setAndroidContext(this);


    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
       int result7 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result8 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int result9 = ContextCompat.checkSelfPermission(getApplicationContext(), CAPTURE_VIDEO_OUTPUT);

     return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED && result6 == PackageManager.PERMISSION_GRANTED && result8 == PackageManager.PERMISSION_GRANTED && result7 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,READ_CONTACTS,CALL_PHONE,READ_PHONE_STATE,READ_SMS,ACCESS_NETWORK_STATE,CAPTURE_VIDEO_OUTPUT}, PERMISSION_REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readexternalstordge = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean writeexternalstordge = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean videocapture = grantResults[9] == PackageManager.PERMISSION_GRANTED;
                    boolean phonecall = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean sms = grantResults[8] == PackageManager.PERMISSION_GRANTED;
                    boolean readcontacts = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean readphone = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean accessnwlocation = grantResults[4] == PackageManager.PERMISSION_GRANTED;


                    if (locationAccepted && cameraAccepted && readexternalstordge && readexternalstordge && writeexternalstordge && phonecall && sms && readcontacts && readphone && accessnwlocation)
                    {
                        Toast.makeText(getBaseContext(), "permission Granted", Toast.LENGTH_LONG).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent splesh_intent=new Intent (MainActivity.this,usertype.class);
                                startActivity(splesh_intent);
                            }
                        },3000);
                                           }
                    else
                    {

                        //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to all the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA,WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE,READ_CONTACTS,CALL_PHONE,READ_PHONE_STATE,READ_SMS,ACCESS_NETWORK_STATE,CAPTURE_VIDEO_OUTPUT},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
