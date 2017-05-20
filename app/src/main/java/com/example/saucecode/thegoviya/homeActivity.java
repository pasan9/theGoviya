package com.example.saucecode.thegoviya;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class homeActivity extends AppCompatActivity implements View.OnClickListener {


    private Button sellbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sellbtn = (Button)findViewById(R.id.sellBtn);
        sellbtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v == sellbtn){
            Intent sellIntent = new Intent(this,SellHarvest.class);
            startActivity(sellIntent);
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        System.out.println("=========================================================================================\npressed back");
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
