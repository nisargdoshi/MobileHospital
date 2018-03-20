package com.mobilehospital.nisargdoshi.mobilehospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class bloodstatuscheck extends AppCompatActivity {
    ArrayList<String> userlist=new ArrayList();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private ProgressDialog mprog;
    private DatabaseReference mdatabase;
    private Firebase mref,mref2,mref3,mref4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloodstatuscheck);
        Intent i=getIntent();
        userlist=i.getStringArrayListExtra("data");
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mref = new Firebase("https://mobile-hospital.firebaseio.com/need/Blood/approved");
        mref2 = new Firebase("https://mobile-hospital.firebaseio.com/need/Blood/temp/"+ mAuth.getCurrentUser().getUid());
        mref3=new Firebase("https://mobile-hospital.firebaseio.com/donate/Blood");


        String user_id=mAuth.getCurrentUser().getUid();

        Firebase child=mref.child(mAuth.getCurrentUser().getUid());
        child.child("Blood Group").setValue(userlist.get(0));
        child.child("Emergency Level").setValue(userlist.get(3));
        child.child("Doctor Name").setValue(userlist.get(2));
        child.child("Latitite").setValue(userlist.get(3));
        child.child("Longitude").setValue(userlist.get(4));
        child.child("Contact Number").setValue(userlist.get(1));
        child.child("Uprn").setValue(userlist.get(5));
        mref2.removeValue();



     mref3.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot snapshot) {
             Log.e("Count ", "" + snapshot.getChildrenCount());

             for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                 mref4=postSnapshot.getRef();

                    mref4.child("Blood Group").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(getBaseContext(),String.valueOf(dataSnapshot.getValue()),Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });






                                     }

              }
         @Override
         public void onCancelled(FirebaseError firebaseError) {
             Log.e("The read failed: ", firebaseError.getMessage());
         }
     });



    }
}
