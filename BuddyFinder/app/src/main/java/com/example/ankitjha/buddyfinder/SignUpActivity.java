package com.example.ankitjha.buddyfinder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    ActionBar bar;
    DatabaseReference userRef,keyRef;
    EditText uname1,user,pass;
    TextView unameView,userView,passView;
    Keys keyClass;
    public void signUpUser(View view)
    {
        String name,username,password;
        EditText t=findViewById(R.id.uname);
        name=uname1.getText().toString();
        //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();
        username=user.getText().toString();
        password=pass.getText().toString();
        ConnectivityManager connectivitymanager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean stat=connectivitymanager.getActiveNetworkInfo().isConnected();
        if(stat==false)
        {
            Toast.makeText(getApplicationContext(),"You are not connected to a Server",Toast.LENGTH_SHORT).show();
            return;
        }

        if(userView.getText().toString().equals("Username already Exists !!")==true)
        {
            Toast.makeText(getApplicationContext(),"Username already Exists. Please choose another username",Toast.LENGTH_SHORT).show();
            return;
        }




        Users user1=new Users(keyClass.getUid(),name,username,password,-1.0,-1.0,null,1);
        String id=String.valueOf(keyClass.getUid());
        userRef.child(id).setValue(user1);
        keyClass.uid++;
        keyRef.child("0").setValue(keyClass);

        Toast.makeText(getApplicationContext(),"Sign Up done Successfully",Toast.LENGTH_SHORT).show();

        finish();


    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bar=getSupportActionBar();
        bar.setTitle("Sign Up for Buddy Finder");

        uname1=findViewById(R.id.uname);
        user=findViewById(R.id.user1);
        pass=findViewById(R.id.pass1);
        unameView=findViewById(R.id.unameView);
        userView=findViewById(R.id.userView1);
        passView=findViewById(R.id.passView1);
        uname1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(uname1.getText().toString().isEmpty()==true)
                {
                    unameView.setVisibility(View.INVISIBLE);
                }
                else
                {
                    unameView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(user.getText().toString().isEmpty()==true)
                {
                    userView.setVisibility(View.INVISIBLE);
                    userView.setText("Username");
                }
                else
                {
                    userView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                String us=user.getText().toString().trim();
                for(Users u:LogInActivity.userList)
                {
                    if(us.equals(u.getUsername()))
                    {
                        userView.setText("Username already Exists !!");
                        return;
                    }
                }
                userView.setText("Username Valid ");
            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(pass.getText().toString().isEmpty()==true)
                {
                    passView.setVisibility(View.INVISIBLE);
                }
                else
                {
                    passView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        userRef=FirebaseDatabase.getInstance().getReference("Users");
        keyRef=FirebaseDatabase.getInstance().getReference("Keys");
        keyClass=new Keys();
        keyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d:dataSnapshot.getChildren())
                    keyClass=d.getValue(Keys.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
}
