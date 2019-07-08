package com.example.ankitjha.buddyfinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountSettingsActivity extends AppCompatActivity {


    ActionBar bar;
    EditText nameE,passE,confPassE;
    TextView nameV,passV,confPassV;
    boolean updateVal;
    Users userLoggedIn;

    public void updateRequest(View view)
    {
        if(updateVal==false){
            updateVal=true;
            nameE.setEnabled(true);
            passE.setEnabled(true);
            confPassE.setEnabled(true);
            passE.setText("");
            confPassE.setVisibility(View.VISIBLE);
        }
        else
        {

            if(passE.getText().toString().isEmpty()==true || nameE.getText().toString().isEmpty()==true||confPassE.getText().toString().isEmpty()==true)
            {
                Toast.makeText(getApplicationContext(),"Enter all Fields First",Toast.LENGTH_SHORT).show();
                return;
            }
            if(passE.getText().toString().equals(confPassE.getText().toString()))
            {
                ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
                if(stat==false)
                {
                    Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
                    return;
                }
                userLoggedIn.uname=nameE.getText().toString().trim();
                userLoggedIn.password=passE.getText().toString().trim();
                DatabaseReference accRef= FirebaseDatabase.getInstance().getReference("Users");
                accRef.child(String.valueOf(userLoggedIn.getUid())).setValue(userLoggedIn);
                Toast.makeText(getApplicationContext(),"Changes will be reflected after you Sign In",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Enter same passwords",Toast.LENGTH_SHORT).show();
                return;
            }



            nameE.setEnabled(false);
            passE.setEnabled(false);
            confPassE.setText("");
            confPassE.setEnabled(false);
            confPassE.setVisibility(View.INVISIBLE);
            updateVal=false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        bar=getSupportActionBar();
        bar.setTitle("Account Settings");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        nameE=findViewById(R.id.nameEditText);
        passE=findViewById(R.id.passEditText);
        confPassE=findViewById(R.id.confPass2);
        nameV=findViewById(R.id.uname11);
        passV=findViewById(R.id.pass3);
        confPassV=findViewById(R.id.confPass1);
        nameE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nameE.getText().toString().isEmpty()==true)
                    nameV.setVisibility(View.INVISIBLE);
                else
                    nameV.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passE.getText().toString().isEmpty())
                    passV.setVisibility(View.INVISIBLE);
                else
                    passV.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confPassE.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(confPassE.getText().toString().isEmpty()==true)
                    confPassV.setVisibility(View.INVISIBLE);
                else
                    confPassV.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateVal=false;

        Intent recvIn=getIntent();
        int id=recvIn.getIntExtra("userID",-1);
        for(Users u:LogInActivity.userList)
            if(u.getUid()==id)
                userLoggedIn=u;

        nameE.setText(userLoggedIn.getUname());
        passE.setText(userLoggedIn.getPassword());

        nameE.setEnabled(false);
        passE.setEnabled(false);
        confPassE.setEnabled(false);

    }
}
