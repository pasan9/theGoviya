package com.example.saucecode.thegoviya;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SellHarvest extends AppCompatActivity {

    private Spinner cropList;
    private Spinner sellMethod;
    private Button addBtn;
    private EditText qty,mois,minPrice;
    private int selectedCrop = 0;
    private int selectedMethod = 0;
    private List<String> list = new ArrayList<String>();
    private List<String> sellMethodList = new ArrayList<String>();
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_harvest);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        cropList = (Spinner)findViewById(R.id.cropList);
        addBtn = (Button)findViewById(R.id.addBtn);
        qty = (EditText)findViewById(R.id.qty);
        mois = (EditText)findViewById(R.id.mois);
        minPrice = (EditText)findViewById(R.id.minPrice);

        sellMethod = (Spinner)findViewById(R.id.sellMethod);

        list.add("Paddy");
        list.add("Oninons");

        sellMethodList.add("Auction");
        sellMethodList.add("Sell it now");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropList.setAdapter(dataAdapter);

        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, sellMethodList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sellMethod.setAdapter(dataAdapter);

        cropList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSelectedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = firebaseAuth.getCurrentUser().getEmail();
                addToDb(email);
            }
        });

        sellMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSellingMethod(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void getSelectedItem(int pos){
        if(pos == 1){
            mois.setVisibility(View.INVISIBLE);
        }
        if(pos == 0){
            mois.setVisibility(View.VISIBLE);
        }

        selectedCrop = pos;
    }

    public void getSellingMethod(int pos){
        selectedMethod = pos;
    }

    private void addToDb(String email){
        String x;
        ref.child("Products").child(x = (selectedCrop == 0) ? "Paddy" : "Onions").child("Email").setValue(email);
        ref.child("Products").child(x = (selectedCrop == 0) ? "Paddy" : "Onions").child("Quantity").setValue(qty.getText().toString());
        ref.child("Products").child(x = (selectedCrop == 0) ? "Paddy" : "Onions").child("Unit price").setValue(minPrice.getText().toString());
        if(selectedMethod == 0){
            ref.child("Products").child("Paddy").child("Moisture Level").setValue(mois.getText().toString());
        }
        ref.child("Products").child(x = (selectedCrop == 0) ? "Paddy" : "Onions").child("Selling method").setValue(x = (selectedMethod == 0) ? "Auction" : "Sell it now");
        Toast.makeText(this,"Product added!",Toast.LENGTH_SHORT);
        Intent intent = new Intent(this,homeActivity.class);
        startActivity(intent);
        this.finish();

    }



}
