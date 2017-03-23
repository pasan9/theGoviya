package com.example.saucecode.thegoviya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button loginBtn;
    private EditText email;
    private EditText password;
    private TextView regInForm;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        progress = new ProgressDialog(this);

        loginBtn = (Button)findViewById(R.id.loginBtn);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        regInForm = (TextView)findViewById(R.id.txtViewlogin);

        loginBtn.setOnClickListener(this);
        regInForm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == loginBtn){
            loginUser();
        }

        if(v == regInForm){
            Intent reginIntent = new Intent(this,regActivity.class);
            startActivity(reginIntent);
            this.finish();
        }
    }

    private void loginUser(){
        String uEmail = email.getText().toString().trim();
        String uPass = password.getText().toString().trim();

        if(TextUtils.isEmpty(uEmail)){
            Toast.makeText(this,"Please enter an email!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(uPass)){
            Toast.makeText(this,"Please enter a password",Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setMessage("Signing...");
        progress.show();

        firebaseAuth.signInWithEmailAndPassword(uEmail, uPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.hide();

                        if (task.isSuccessful()) {
                            loadHome();
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to login! try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loadHome(){ //launch home activity
        Intent homeIntent = new Intent(this, homeActivity.class);
        startActivity(homeIntent);
        this.finish();
    }
}
