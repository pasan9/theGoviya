package com.example.saucecode.thegoviya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class DisplayProducts extends AppCompatActivity {

    ArrayList<Products> prodList = new ArrayList<Products>();
    private ListView listView;
    private static CustomAdapter adapter;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayProds prods = new DisplayProds();
        prods.execute();

    }


    public class DisplayProds extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DisplayProducts.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Loading product list, Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Crud con = new Crud();
            prodList = con.selectData("SELECT * FROM products");
            try
            {
                synchronized (this)
                {
                    int counter = 0;
                    while(counter <= 4)
                    {
                        this.wait(850);
                        counter++;
                        publishProgress(counter*25);
                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            setContentView(R.layout.activity_display_products);
            toolbar = (Toolbar)findViewById(R.id.my_action_bar_tool_bar);
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setSubtitleTextColor(Color.WHITE);
            listView = (ListView)findViewById(R.id.list);
            populateProductList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflowmenutoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int optionId = item.getItemId();
        if(optionId == R.id.action_refresh){
            DisplayProds prods = new DisplayProds();
            prods.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateProductList(){
        adapter = new CustomAdapter(prodList,getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Products dataModel= prodList.get(position);
                System.out.println(dataModel.farmerID+" "+dataModel.type);
                purchaseProduct(position);
            }
        });
    }
    private void purchaseProduct(int position){
        Intent intent = new Intent(this, BuyProduct.class);
        intent.putExtra("product",prodList.get(position));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BuyerHome.class);
        startActivity(intent);
        this.finish();
    }
}
