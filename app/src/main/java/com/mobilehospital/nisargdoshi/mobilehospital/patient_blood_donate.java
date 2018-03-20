package com.mobilehospital.nisargdoshi.mobilehospital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class patient_blood_donate extends AppCompatActivity implements LocationListener {
        TextView location1;
        Spinner type,first,freq;
        Button donate;
    private LocationManager locationManager;
    private String provider;
    Double lat,lng;
    EditText email,contact;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private ProgressDialog mprog;
    private DatabaseReference mdatabase;
    private Firebase mref;
    Context context=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_blood_donate);
        location1=(TextView)findViewById(R.id.tv_patient_donate_blood);
        type=(Spinner)findViewById(R.id.sp_patient_donate_blood_type);
        first=(Spinner)findViewById(R.id.sp_patient_donate_blood_first);
        freq=(Spinner)findViewById(R.id.sp_patient_donate_blood_freq);
        donate=(Button)findViewById(R.id.btn_patinent_donate_blood_Aggree);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        email=(EditText)findViewById(R.id.et_patient_donate_blood_email);
        contact=(EditText)findViewById(R.id.et_patient_donate_blood_cno);


        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mref = new Firebase("https://mobile-hospital.firebaseio.com/donate/Blood");

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

            if(String.valueOf(postSnapshot.getKey()).toString().equals(mAuth.getCurrentUser().getUid()))
            {
                donate.setText("you already appliend for donate blood...");
                donate.setEnabled(false);

                   }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });

        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Firebase child=mref.child(mAuth.getCurrentUser().getUid());
                child.child("Blood Group").setValue(type.getSelectedItem().toString());
                child.child("First Time").setValue(first.getSelectedItem().toString());
                child.child("Frequency").setValue(freq.getSelectedItem().toString());
                child.child("Latitite").setValue(lat);
                child.child("Longitude").setValue(lng);
                child.child("Contact Number").setValue(contact.getText().toString());
                child.child("email").setValue(email.getText().toString());
                donate.setEnabled(false);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder
                        .setCancelable(false)
                        .setMessage("Thanks for showing your avaliblity we are notify you once some one require it!!")
                        .setTitle("Applied Sucessfull")
                        .setPositiveButton("Take me Home",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        Intent i= new Intent(patient_blood_donate.this,Home.class);
                                        startActivity(i);
                                    }
                                });


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);
                // show it
                alertDialog.show();

            }
        });
        email.setText(mAuth.getCurrentUser().getEmail());
        email.setEnabled(false);


        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            location1.setText("Location not available");
            //longitudeField.setText("Location not available");

        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
         lat = (Double) (location.getLatitude());
         lng = (Double) (location.getLongitude());
        //latituteField.setText(String.valueOf(location.getLatitude()));
        //longitudeField.setText(String.valueOf(location.getLongitude()));
        location1.setText("You are Currently at\t"+getAdress(lat,lng));


        //  Toast.makeText(getBaseContext(),getAdress(lat,lng),Toast.LENGTH_LONG).show();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    public String getAdress(Double lat, Double lng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String Address = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Address=address;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Address;

    }

}