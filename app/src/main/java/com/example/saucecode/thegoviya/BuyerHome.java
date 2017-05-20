package com.example.saucecode.thegoviya;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BuyerHome extends AppCompatActivity implements View.OnClickListener {
    Button viewProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_home);

        viewProducts = (Button)findViewById(R.id.viewProducts);
        viewProducts.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == viewProducts){
            Intent products = new Intent(this,DisplayProducts.class);
            startActivity(products);
            this.finish();
        }

    }
}
