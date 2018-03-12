package com.mobilehospital.nisargdoshi.mobilehospital;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class statuscheck extends AppCompatActivity {
    ArrayList<String> userlist=new ArrayList();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private ProgressDialog mprog;
    private DatabaseReference mdatabase;
    private Firebase mref;
    String uprn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statuscheck);

        Intent i=getIntent();
        userlist=i.getStringArrayListExtra("data");
        uprn=i.getStringExtra("uprn");
        Toast.makeText(getBaseContext(),userlist.get(4),Toast.LENGTH_SHORT).show();
        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
        mref = new Firebase("https://mobile-hospital.firebaseio.com/users/tempexp/"+uprn);







        mAuth.createUserWithEmailAndPassword(userlist.get(4).toString(),userlist.get(5).toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Authentication successful.",
                                    Toast.LENGTH_SHORT).show();
                            String user_id=mAuth.getCurrentUser().getUid();






                            Firebase mref1 = new Firebase("https://mobile-hospital.firebaseio.com/users/expert");

                            Firebase c=mref1.child(user_id);
                            c.child("First Name").setValue(userlist.get(1).toString());
                            c.child("Last Name").setValue(userlist.get(2).toString());
                            c.child("UPRN").setValue(uprn);
                            c.child("Location").setValue(userlist.get(3).toString());
                            c.child("Contact number").setValue(userlist.get(0));
                            FirebaseUser user = mAuth.getCurrentUser();
                            mref.removeValue();
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
        Button backtologin=(Button)findViewById(R.id.btn_backtologin);
        backtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nevigate_login=new Intent(statuscheck.this,Login.class);
                startActivity(nevigate_login);

            }
        });



    }
}
