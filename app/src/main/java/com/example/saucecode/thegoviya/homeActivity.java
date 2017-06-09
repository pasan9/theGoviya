package com.example.saucecode.thegoviya;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class homeActivity extends AppCompatActivity implements View.OnClickListener {


    private Button sellbtn;
    private Button displayProds;
    private Button logOut;
    private Button moistureMeter;
    public static final String PREFS_NAME = "MyLoginStatusFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.my_action_bar_tool_bar);
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.mipmap.ic_thegoviyaicon);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(Color.WHITE);

        logOut = (Button)findViewById(R.id.logOut);
        logOut.setOnClickListener(this);
        sellbtn = (Button)findViewById(R.id.sellBtn);
        sellbtn.setOnClickListener(this);
        displayProds = (Button)findViewById(R.id.displayUserProducts);
        displayProds.setOnClickListener(this);
        moistureMeter = (Button)findViewById(R.id.btnMoistureMonitor);
        moistureMeter.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == sellbtn){
            Intent sellIntent = new Intent(this,SellHarvest.class);
            startActivity(sellIntent);
            this.finish();
        }

        if(v == logOut){
            SharedPreferences settings = getSharedPreferences(this.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("hasLoggedIn", false);
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            this.finish();
        }

        if(v == displayProds){
            Intent intent = new Intent(this,UserProducts.class);
            startActivity(intent);
            this.finish();
        }

        if(v == moistureMeter){
            Intent intent = new Intent(this,MonitorMoisture.class);
            startActivity(intent);
            //this.finish();
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("Exit");
        exitDialog.setMessage("Are you sure you want to exit?");
        exitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                homeActivity.this.finish();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final AlertDialog alert = exitDialog.create();
        alert.show();
    }
}
