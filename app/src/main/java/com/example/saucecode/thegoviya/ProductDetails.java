package com.example.saucecode.thegoviya;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ProductDetails extends AppCompatActivity {

    TextView ProductType,SaleType,Price,Qty,Moisture,MoistureUpdate;
    Button contactSeller,sellerLocation;
    Products product;
    String MoistureUpDate = "22/05/2017";
    String MoistureUpTime = "10:23";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        //get Product from intent

        product = (Products) getIntent().getSerializableExtra("product");

        //UI elements

        ProductType = (TextView)findViewById(R.id.productType);
        //SaleType = (TextView)findViewById(R.id.saleType);
        Price = (TextView)findViewById(R.id.priceTxt);
        Qty = (TextView)findViewById(R.id.QtyTxt);
        Moisture = (TextView)findViewById(R.id.moistureTxt);
        MoistureUpdate = (TextView)findViewById(R.id.MoistureLastUpTxt);

        contactSeller = (Button)findViewById(R.id.btnContactSeller);
        sellerLocation = (Button)findViewById(R.id.btnSellerLocation);

        //Set Values

        ProductType.setText(product.type);
        Price.setText("Price : Rs."+product.price+" Per Kg");
        Qty.setText("Quantity :"+product.qty+"Kg");
        Moisture.setText("Moisture :"+product.mois);
        MoistureUpdate.setText("Last Updated On : "+MoistureUpDate+" at "+MoistureUpTime);




    }
}
