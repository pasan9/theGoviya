package com.example.saucecode.thegoviya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class regActivity extends AppCompatActivity implements View.OnClickListener{

    private Button regBtn;
    private EditText email;
    private EditText password;
    private EditText fName;
    private EditText nic;
    private EditText add;
    private EditText telNo;
    private TextView logInForm;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);
        fName = (EditText)findViewById(R.id.fName);
        nic = (EditText)findViewById(R.id.nic);
        regBtn = (Button)findViewById(R.id.regBtn);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        logInForm = (TextView)findViewById(R.id.txtViewRegd);
        add = (EditText)findViewById(R.id.add);
        telNo = (EditText)findViewById(R.id.telNo);

        regBtn.setOnClickListener(this);
        logInForm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == regBtn){
            registerUser();
        }

        if(v == logInForm){
            Intent loginIntent = new Intent(this,MainActivity.class);
            startActivity(loginIntent);
            this.finish();
        }
    }

    private void registerUser(){
        final String uEmail = email.getText().toString().trim();
        final String uPass = password.getText().toString().trim();

        if(TextUtils.isEmpty(uEmail)){
            //if email is empty
            Toast.makeText(this,"Please enter your email!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(uPass)){
            //password is empty
            Toast.makeText(this,"Please enter your password!",Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setMessage("Registering user...");
        progress.show();

        firebaseAuth.createUserWithEmailAndPassword(uEmail,uPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.hide();
                        if(task.isSuccessful()){
                            Toast.makeText(regActivity.this,"Registeration Successful!",Toast.LENGTH_SHORT).show();
                            addToDB();
                            progress.setMessage("Singing now....");
                            progress.show();
                            signIn(uEmail,uPass);


                        }else {
                            Toast.makeText(regActivity.this,"Failed to register!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void signIn(String uEmail,String uPass){
        firebaseAuth.signInWithEmailAndPassword(uEmail, uPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progress.hide();

                        if (task.isSuccessful()) {
                            loadHome();
                        } else {
                            Toast.makeText(regActivity.this, "Failed to login! try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addToDB(){

        ref.child("User").child(fName.getText().toString()).child("nic").setValue(nic.getText().toString());
        ref.child("User").child(fName.getText().toString()).child("fName").setValue(fName.getText().toString());
        ref.child("User").child(fName.getText().toString()).child("add").setValue(add.getText().toString());
        ref.child("User").child(fName.getText().toString()).child("telNo").setValue(telNo.getText().toString());
        ref.child("User").child(fName.getText().toString()).child("email").setValue(email.getText().toString());
        ref.child("User").child(fName.getText().toString()).child("password").setValue(password.getText().toString());
        //ref.child("samUsers").child("NIC").setValue(nic.getText());
    }

    public void loadHome(){
        Intent homeIntent = new Intent(this, homeActivity.class);
        startActivity(homeIntent);
        this.finish();
    }
}
