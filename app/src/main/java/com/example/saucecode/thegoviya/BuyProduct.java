package com.example.saucecode.thegoviya;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class BuyProduct extends AppCompatActivity {

    Products product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);
        Intent intent = getIntent();
        product = (Products) intent.getSerializableExtra("product");
        System.out.println(product.farmerID+"|"+product.type+"|"+product.productID+"|"+product.sellingMethod);

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
