package com.example.saucecode.thegoviya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button loginBtn;
    private EditText nic;
    private EditText password;
    private TextView regInForm;
    private ProgressDialog progress;
    private String uNic;
    private String uPass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        progress = new ProgressDialog(this);

        loginBtn = (Button)findViewById(R.id.loginBtn);
        nic = (EditText)findViewById(R.id.nic);
        password = (EditText)findViewById(R.id.password);
        regInForm = (TextView)findViewById(R.id.txtViewlogin);

        loginBtn.setOnClickListener(this);
        regInForm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == loginBtn){
            loginUser();
            hideKeyboard(this);
        }

        if(v == regInForm){
            Intent reginIntent = new Intent(this,regActivity.class);
            startActivity(reginIntent);
            this.finish();
        }
    }

    private void loginUser(){
        uNic = nic.getText().toString().trim();
        uPass = password.getText().toString().trim();

        if(TextUtils.isEmpty(uNic)){
            Toast.makeText(this,"Please enter an email!",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(uPass)){
            Toast.makeText(this,"Please enter a password",Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setMessage("Signing...");
        progress.show();

        CheckLogin checkLogin = new CheckLogin();
        checkLogin.execute("");

    }

    @Override
    public void onBackPressed() {
        System.out.println("=========================================================================================\npressed back");
    }

    public void loadHome(){
        Intent homeIntent = new Intent(this, homeActivity.class);
        startActivity(homeIntent);
        this.finish();
    }

    public void buyerHome(){
        Intent homeIntent = new Intent(this, BuyerHome.class);
        startActivity(homeIntent);
        this.finish();
    }


    public class CheckLogin extends AsyncTask<String, String,String>{
        String result = "";
        @Override
        protected String doInBackground(String... params) {
            Crud con = new Crud();
            result = con.connectDBLogin("SELECT * FROM users WHERE NIC='"+uNic+"' AND password='"+uPass+"';");
            return "Success";
        }

        @Override
        protected void onPostExecute(String s) {
           // writeToFile("hi hi",getApplicationContext());
            if(result.equalsIgnoreCase("farmer")){
                loadHome();
            }else if(result.equalsIgnoreCase("buyer")){
                buyerHome();
            } else {
                progress.hide();
                Toast.makeText(MainActivity.this, "Failed to login! try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void writeToFile(String data,Context context) {
        System.out.println("====================================================================");
        FileOutputStream stream = null;
        try {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, "theGoviyaLogin.txt");
            if (!file.exists()) {
                if(file.mkdir())System.out.println("Folder created");
            }
            System.out.println(file.getAbsoluteFile());
        stream = new FileOutputStream(file);
            stream.write(uNic.getBytes());
            System.out.println("====================================================================");
        } catch (IOException e) {
            System.out.println("E====================================================================");
            e.printStackTrace();
        } finally {
            try {
                System.out.println("C====================================================================");
                stream.close();
            } catch (IOException e) {
                System.out.println("CE====================================================================");
                e.printStackTrace();
            }
        }
    }

}
