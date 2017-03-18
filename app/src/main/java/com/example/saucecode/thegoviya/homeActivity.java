package com.example.saucecode.thegoviya;

import android.content.Intent;
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
        }
    }
}
