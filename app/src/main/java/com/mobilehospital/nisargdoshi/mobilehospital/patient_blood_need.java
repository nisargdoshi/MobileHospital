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
import android.util.Log;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class patient_blood_need extends AppCompatActivity implements LocationListener {
    Spinner type,el;
    TextView location1,cs;
    Double lat,lng;
    Button need;
    EditText doc_name,uprn,cn;
    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private ProgressDialog mprog;
    private DatabaseReference mdatabase;
    private Firebase mref,mref1,mref2;
    Context context=this;
    ArrayList<String> userlist=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_blood_need);
            type=(Spinner)findViewById(R.id.sp_patient_blood_need_bg);
            el=(Spinner)findViewById(R.id.sp_patient_blood_need_el);
            location1=(TextView)findViewById(R.id.tv_patient_blood_need_location);
            cs=(TextView)findViewById(R.id.tv_patient_blood_need_cs);
            need=(Button)findViewById(R.id.btn_patient_need);
            doc_name=(EditText)findViewById(R.id.et_patient_blood_need_dn);
            uprn=(EditText)findViewById(R.id.et_patient_blood_need_uprn);
            cn=(EditText)findViewById(R.id.et_patient_blood_need_cn);


        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mref = new Firebase("https://mobile-hospital.firebaseio.com/need/Blood/temp");
        mref1 = new Firebase("https://mobile-hospital.firebaseio.com/need/Blood/approved");
        mref2 = new Firebase("https://mobile-hospital.firebaseio.com/need/Blood/temp/"+ mAuth.getCurrentUser().getUid());
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    if(String.valueOf(postSnapshot.getKey()).toString().equals(mAuth.getCurrentUser().getUid()))
                    {
                        need.setText("Please Check your status after some time");
                        need.setEnabled(false);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });



        mref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    if(String.valueOf(postSnapshot.getKey()).toString().equals(mAuth.getCurrentUser().getUid()))
                    {
                        need.setText("you are approved");
                        need.setEnabled(false);
                        cs.setText("WE will notify you when some avilable for you");
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });

            need.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Firebase child=mref.child(mAuth.getCurrentUser().getUid());
                    child.child("Blood Group").setValue(type.getSelectedItem().toString());
                    child.child("Emergency Level").setValue(el.getSelectedItem().toString());
                    child.child("Doctor Name").setValue(doc_name.getText().toString());
                    child.child("Latitite").setValue(lat);
                    child.child("Longitude").setValue(lng);
                    child.child("Contact Number").setValue(cn.getText().toString());
                    child.child("Uprn").setValue(uprn.getText().toString());
                    child.child("status").setValue(0);
                    need.setEnabled(false);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            context);

                    alertDialogBuilder
                            .setCancelable(false)
                            .setMessage("We will shorty notify when your need varified ")
                            .setTitle("Approved")
                            .setPositiveButton("Take me Home",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {

                                            Intent i= new Intent(patient_blood_need.this,Home.class);
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
            cs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    mref2.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                String Status = dataSnapshot.getValue(String.class);
                                Toast.makeText(getBaseContext(), Status, Toast.LENGTH_SHORT).show();

                                if (Status.equals("1")) {
                                    mref2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            Log.e("Count ", "" + snapshot.getChildrenCount());

                                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                                userlist.add(String.valueOf(postSnapshot.getValue()));
                                            }

                                            Intent i = new Intent(patient_blood_need.this, bloodstatuscheck.class);
                                            i.putStringArrayListExtra("data", userlist);
                                         //   i.putExtra("uprn", userInput.getText().toString());
                                            startActivity(i);

                                       //     Toast.makeText(getBaseContext(), userlist.get(0), Toast.LENGTH_SHORT).show();
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
            });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
            //location.setText("Location not available");
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