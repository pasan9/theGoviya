package com.example.saucecode.thegoviya;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
    private ProgressDialog progress;

    private int selectedCrop = 0;
    private int selectedMethod = 0;
    private List<String> list = new ArrayList<String>();
    private List<String> sellMethodList = new ArrayList<String>();

    private String userNIC = "";
    private String userFName = "";
    private Double unitPrice = 0.0;
    private Double moistureLevel = 0.0;
    private Double quantitiy = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_harvest);

        CurrentUser user = new CurrentUser();
        userNIC = user.getNicNumber();
        userFName = user.getfName();

        progress = new ProgressDialog(this);
        cropList = (Spinner)findViewById(R.id.cropList);
        addBtn = (Button)findViewById(R.id.addBtn);
        qty = (EditText)findViewById(R.id.qty);
        mois = (EditText)findViewById(R.id.mois);
        minPrice = (EditText)findViewById(R.id.minPrice);

        sellMethod = (Spinner)findViewById(R.id.sellMethod);

        list.add("Paddy");
        list.add("Onions");

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
                quantitiy = Double.parseDouble(qty.getText().toString());
                if(selectedCrop == 1)moistureLevel = Double.parseDouble(mois.getText().toString());
                unitPrice = Double.parseDouble(minPrice.getText().toString());
                progress.setMessage("Adding product...");
                progress.show();
                AddProduct add = new AddProduct();
                add.execute("");
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

    @Override
    public void onBackPressed() {
        System.out.println("=========================================================================================\npressed back");
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setTitle("Exit");
        exitDialog.setMessage("Are you sure you want to exit?");
        exitDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                SellHarvest.this.finish();
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

    public class AddProduct extends AsyncTask<String, String,String> {
        String result = "";
        @Override
        protected String doInBackground(String... params) {
            Double value = 0.0;
            String productType = (selectedCrop == 0) ? "Paddy" : "Onions";
            String sellingMethod = (selectedMethod == 0) ? "Auction" : "Sell it now";
            moistureLevel = (selectedMethod == 1) ? moistureLevel : 0.0;
            String query = "INSERT INTO Products (farmerID,Quantity,UnitPrice,MoistureLevel,ProductType,SellingMethod) VALUES ('"+userNIC+"',"+quantitiy+","+unitPrice+","+moistureLevel+",'"+productType+"','"+sellingMethod+"')";
            Crud db = new Crud();
            db.insertData(query);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progress.setMessage("Added");
            progress.show();
            progress.dismiss();
            Toast.makeText(SellHarvest.this,"Product added!",Toast.LENGTH_SHORT);
            Intent intent = new Intent(SellHarvest.this,homeActivity.class);
            startActivity(intent);
            SellHarvest.this.finish();
        }
    }



}
