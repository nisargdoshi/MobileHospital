package com.mobilehospital.nisargdoshi.mobilehospital;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class usertype extends AppCompatActivity {
    Button usertype;
    RadioGroup usergroup;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usertype);
        setTitle("Who are you?");

        usertype=(Button)findViewById(R.id.btn_next_usertype);
        usergroup=(RadioGroup)findViewById(R.id.rg_usertype);

        usertype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the text from selected radiobutton
                int radioButtonID = usergroup.getCheckedRadioButtonId();
                View radioButton = usergroup.findViewById(radioButtonID);
                int idx = usergroup.indexOfChild(radioButton);
                RadioButton r = (RadioButton)  usergroup.getChildAt(idx);
                user = r.getText().toString();

                //pass to the Login activity with usertype
                Intent login_intent=new Intent(usertype.this,Login.class);
                login_intent.putExtra("usertype",user);

                Toast.makeText(getApplicationContext(),user,Toast.LENGTH_LONG).show();
                startActivity(login_intent);

                //store the usertype of user so they don't need  to specify untill they change the usertype or logout form the system



            }
        });

    }
}
