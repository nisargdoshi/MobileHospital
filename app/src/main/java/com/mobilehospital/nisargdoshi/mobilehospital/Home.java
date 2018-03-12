package com.mobilehospital.nisargdoshi.mobilehospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.net.URL;
import java.util.ArrayList;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<String> userlist=new ArrayList();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private ProgressDialog mprog;
    private DatabaseReference mdatabase;
    private Firebase mref;
    String uprn;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent patient_view_detail_intenrt= new Intent(Home.this,View_patient_profile.class);
                startActivity(patient_view_detail_intenrt);
            }
        });
        final TextView navfirstname = (TextView) headerView.findViewById(R.id.tv_name_patient_home);
        TextView navUsername = (TextView) headerView.findViewById(R.id.tv_username_patient_home);
        final ImageView profilepic=(ImageView)headerView.findViewById(R.id.iv_pp_patient_home);
        navUsername.setText(mAuth.getCurrentUser().getEmail().toString());
        navigationView.setNavigationItemSelectedListener(this);
        profilepic.setImageURI(mAuth.getCurrentUser().getPhotoUrl());
        mref = new Firebase("https://mobile-hospital.firebaseio.com/users/Patient/"+mAuth.getCurrentUser().getUid().toString());


        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());


                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    userlist.add(String.valueOf(postSnapshot.getValue()));
                }


                navfirstname.setText(userlist.get(1)+"\t\t"+userlist.get(2));

                Toast.makeText(getBaseContext(), userlist.get(0), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });

        StorageReference ref = storageReference.child("images/"+ mAuth.getCurrentUser().getUid().toString());
                // Got the download URL for 'users/me/profile.png'
                Glide.with(Home.this)
                        .using(new FirebaseImageLoader())
                        .load(ref)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(profilepic);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_finddoctor) {
            Intent i =new Intent(Home.this,ShowLocationActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_buymedicien) {

        } else if (id == R.id.nav_helpandcontactus) {

        } else if (id == R.id.nav_rateus) {

        } else if (id == R.id.nav_donate) {

        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            startActivity(new Intent(Home.this,usertype.class));

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();

            Toast.makeText(getBaseContext(),"logout sucesfull",Toast.LENGTH_LONG).show();

        }
        else if (id == R.id.nav_settings) {

        }
        else if (id == R.id.nav_need) {
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
