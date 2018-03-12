package com.mobilehospital.nisargdoshi.mobilehospital;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class View_patient_profile extends AppCompatActivity {

    TextView displayname,fname,lname,email_tv,birthdate;
    ImageView profilepic;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistner;
    private DatabaseReference mdatabase;
    private Firebase mref;
    ArrayList<String> userlist=new ArrayList();
    final Context context = this;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;
    private ProgressDialog mprog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Patient Detial");
        mprog=new ProgressDialog(this);

        com.github.clans.fab.FloatingActionButton basic = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_patient_basic);
        final com.github.clans.fab.FloatingActionButton password = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_patient_password);

        final FloatingActionMenu fm=(FloatingActionMenu) findViewById(R.id.FloatingActionMenu1);
        basic.setColorNormalResId(R.color.colorPrimaryDark);
        password.setColorNormalResId(R.color.colorPrimaryDark);
        basic.setColorPressedResId(R.color.colorPrimary);
        password.setColorPressedResId(R.color.colorPrimary);


        displayname=(TextView)findViewById(R.id.tv_displayname_patient_view);
        email_tv=(TextView)findViewById(R.id.tv_email_patient_view);
        birthdate=(TextView)findViewById(R.id.tv_birthdate_patient_view);
        profilepic=(ImageView)findViewById(R.id.iv_prifilepic_patient_view);

        Firebase.setAndroidContext(this);
        mAuth = FirebaseAuth.getInstance();
//        String Displayname=mAuth.getCurrentUser().getDisplayName();
        mprog.setMessage("loding");
        mprog.show();
        mref = new Firebase("https://mobile-hospital.firebaseio.com/users/Patient/"+mAuth.getCurrentUser().getUid().toString());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        email_tv.setText(mAuth.getCurrentUser().getEmail());
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count ", "" + snapshot.getChildrenCount());

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    userlist.add(String.valueOf(postSnapshot.getValue()));
                }


                birthdate.setText(userlist.get(0));
                displayname.setText(userlist.get(1)+"\t\t"+userlist.get(2));

                Toast.makeText(getBaseContext(), userlist.get(0), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });

        basic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.patient_change_basic_detail, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

            final EditText fname = (EditText) promptsView.findViewById(R.id.et_change_fname);
            final EditText lname = (EditText) promptsView.findViewById(R.id.et_change_lname);
            final EditText bdate = (EditText) promptsView.findViewById(R.id.et_Change_bdate);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("Update Basic Information")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                           .setPositiveButton("Update",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        if(TextUtils.isEmpty(fname.getText().toString()) && TextUtils.isEmpty(lname.getText().toString()) && TextUtils.isEmpty(bdate.getText().toString()) ) {
                                            Toast.makeText(getBaseContext(),"Plase field all details",Toast.LENGTH_LONG).show();
                                        }
                                        else {mref.child("First Name").setValue(fname.getText().toString());
                                    mref.child("Last Name").setValue(lname.getText().toString());
                                    mref.child("Date of bith").setValue(bdate.getText().toString());
                                    Toast.makeText(getBaseContext(),"Profile updated",Toast.LENGTH_LONG).show();
                                    }}
                                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.patient_update_password, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText old = (EditText) promptsView.findViewById(R.id.tv_oldpwd_pateint);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setTitle("Update Passsword")

                        .setPositiveButton("Chnage Password",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        FirebaseUser user = mAuth.getCurrentUser();

                                        AuthCredential credential = EmailAuthProvider
                                                .getCredential(mAuth.getCurrentUser().getEmail().toString(),old.getText().toString() );

// Prompt the user to re-provide their sign-in credentials
                                        user.reauthenticate(credential)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                      if(task.isSuccessful())
                                                      {
                                                          mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                              @Override
                                                              public void onComplete(@NonNull Task<Void> task) {
                                                                  if (task.isSuccessful()) {
                                                                      Toast.makeText(getBaseContext(),"password Reset mail sent successfull",Toast.LENGTH_LONG).show();
                                                                  }
                                                              }
                                                          });
                                                      }
                                                      else {
                                                          Toast.makeText(getBaseContext(),"please enter correct password",Toast.LENGTH_LONG).show();
                                                      }
                                                    }
                                                });


                                    }
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
        StorageReference ref1 = storageReference.child("images/"+ mAuth.getCurrentUser().getUid().toString());
        Glide.with(View_patient_profile.this)
                .using(new FirebaseImageLoader())
                .load(ref1)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profilepic);
        mprog.dismiss();

profilepic.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
            alertDialogBuilder
                .setCancelable(false)
                .setTitle("Are you sure you want to cnnhange image")
                .setPositiveButton("change profile picture",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(filePath != null)
                                {
                                    final ProgressDialog progressDialog = new ProgressDialog(View_patient_profile.this);
                                    progressDialog.setTitle("Uploading...");
                                    progressDialog.show();
                                    StorageReference ref = storageReference.child("images/"+ mAuth.getCurrentUser().getUid().toString());
                                    ref.putFile(filePath)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(View_patient_profile.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(View_patient_profile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                                            .getTotalByteCount());
                                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                                }
                                            });
                                }
                            }
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilepic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}


