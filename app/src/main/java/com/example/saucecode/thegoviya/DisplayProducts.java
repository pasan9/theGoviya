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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;


public class DisplayProducts extends AppCompatActivity {

    ArrayList<Products> prodList = new ArrayList<Products>();
    private ListView listView;
    private static CustomAdapter adapter;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    OnDataChangeThread thread = new OnDataChangeThread();
    boolean refreshed = false;
    boolean populatedList = false;
    boolean runThread = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runThread = true;
        thread.start();

        DisplayProds prods = new DisplayProds();
        prods.execute();


    }


    public class DisplayProds extends AsyncTask<Void, Integer, String> {

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
        protected String doInBackground(Void... params) {

            URL mUrl = null;
            try {
                mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Product");
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Content-length", "0");
                httpConnection.setUseCaches(false);
                httpConnection.setAllowUserInteraction(false);
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);
                httpConnection.connect();

                int responseCode = httpConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println("=============================================gggg==============");
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    synchronized (this) {
                        int counter = 0;
                        while (counter <= 4) {
                            this.wait(10);
                            counter++;
                            publishProgress(counter * 25);
                        }
                    }

                    return sb.toString();

                }

                httpConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {

            System.out.println("===========================================================");
            System.out.println(s);

            JSONObject products = null;

            Products prod;
            prodList.clear();
            //prodList = new ArrayList<Products>();
            try {
                products = new JSONObject(s);

                JSONArray dataObject = products.getJSONArray("Table");

                for (int i = 0; i < dataObject.length(); i++) {
                    JSONObject prodObject = dataObject.getJSONObject(i);
                    prod = new Products(prodObject.getInt("productID"), prodObject.getString("farmerID"), prodObject.getDouble("Quantity"), prodObject.getDouble("UnitPrice"), prodObject.getDouble("MoistureLevel"), prodObject.getString("ProductType"), prodObject.getString("SellingMethod"));
                    prodList.add(prod);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            progressDialog.dismiss();
            setContentView(R.layout.activity_display_products);
            toolbar = (Toolbar) findViewById(R.id.my_action_bar_tool_bar);
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setSubtitleTextColor(Color.WHITE);
            listView = (ListView) findViewById(R.id.list);
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
        if (optionId == R.id.action_refresh) {
            DisplayProds prods = new DisplayProds();
            prods.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onOptionsItemSelected(int optionId) {
        refreshed = true;
        if (optionId == R.id.action_refresh) {
            DisplayProds prods = new DisplayProds();
            prods.execute();
        }
    }

    private void populateProductList() {
        populatedList = true;
        adapter = new CustomAdapter(prodList, getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Products dataModel = prodList.get(position);


                //System.out.println(dataModel.farmerID+" "+dataModel.type);
                viewProduct(dataModel);
            }
        });
        refreshed = false;
    }

    private void viewProduct(Products product) {
        Intent intent = new Intent(this, ProductDetails.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        runThread = false;
        Intent intent = new Intent(this, BuyerHome.class);
        startActivity(intent);
        this.finish();
    }

    private class OnDataChangeThread extends Thread {
        public void run() {
            System.out.println("======================Thread runningg");
            while (runThread) {
                System.out.println("=============================================gggg==============");
                if(!refreshed && populatedList){
                    URL mUrl = null;
                    try {
                        mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Product/?refresh=true&found=true");
                        HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                        httpConnection.setRequestMethod("GET");
                        httpConnection.setRequestProperty("Content-length", "0");
                        httpConnection.setUseCaches(false);
                        httpConnection.setAllowUserInteraction(false);
                        httpConnection.setConnectTimeout(100000);
                        httpConnection.setReadTimeout(100000);
                        httpConnection.connect();

                        int responseCode = httpConnection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();

                            JSONObject products = null;

                            Products prod;

                            products = new JSONObject(sb.toString());

                            JSONArray dataObject = products.getJSONArray("Table");

                            JSONObject prodObject = dataObject.getJSONObject(0);

                            System.out.println(prodObject.get("Column1").toString()+" /// "+prodList.size());

                            if(prodObject.getInt("Column1") > prodList.size()){
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        onOptionsItemSelected(R.id.action_refresh);
                                    }
                                });

                            }

                        }
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }



}
