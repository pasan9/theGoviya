package com.example.saucecode.thegoviya;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


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
    //private String[] SortOptions;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private SwipeRefreshLayout swiper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runThread = true;
        //thread.start();

        DisplayProds prods = new DisplayProds();
        prods.execute();


    }


    public class DisplayProds extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DisplayProducts.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Loading product list, Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            // progressDialog.show();
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

                    /*synchronized (this) {
                        int counter = 0;
                        while (counter <= 4) {
                            this.wait(10);
                            counter++;
                            publishProgress(counter * 25);
                        }
                    }*/

                    return sb.toString();

                }

                httpConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {

            System.out.println("===========================================================");
            System.out.println(s);

            JSONObject products = null;

            Products prod;
            prodList.clear();
            try {
                products = new JSONObject(s);

                JSONArray dataObject = products.getJSONArray("Table");


                for (int i = 0; i < dataObject.length(); i++) {
                    JSONObject prodObject = dataObject.getJSONObject(i);
                    prod = new Products(prodObject.getInt("productID"), prodObject.getString("farmerID"), prodObject.getDouble("Quantity"), prodObject.getDouble("UnitPrice"), prodObject.getDouble("MoistureLevel"), prodObject.getString("ProductType"), prodObject.getString("SellingMethod"), prodObject.getString("BidDuration"));
                    if (compareDates(prod.bidDuration) || prod.sellingMethod.equalsIgnoreCase("Sell it now"))
                        //System.out.println(prod.sellingMethod);
                        prodList.add(prod);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e1) {
                e1.printStackTrace();
            }

            progressDialog.dismiss();
            setContentView(R.layout.activity_display_products);
            toolbar = (Toolbar) findViewById(R.id.my_action_bar_tool_bar);
            setSupportActionBar(toolbar);
            toolbar.setTitleTextColor(Color.WHITE);
            toolbar.setSubtitleTextColor(Color.WHITE);
            listView = (ListView) findViewById(R.id.list);
            populateProductList();
            swiper = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshDis);
            swiper.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
            swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onOptionsItemSelected(R.id.action_refresh);
                }
            });

            loadNavBar();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflowmenutoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        progressDialog.dismiss();
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

                viewProduct(dataModel);
            }
        });
        refreshed = false;
    }

    private void viewProduct(Products product) {
        runThread = false;
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

    private void setSwipeAction() {
        swiper = (SwipeRefreshLayout) DisplayProducts.this.findViewById(R.id.swipeRefresh);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onOptionsItemSelected(R.id.action_refresh);
            }
        });
    }

    private class OnDataChangeThread extends Thread {
        public void run() {
            System.out.println("======================Thread runningg");
            while (runThread) {
                System.out.println("=============================================gggg==============");
                if (!refreshed && populatedList) {
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

                            System.out.println(prodObject.get("Column1").toString() + " /// " + prodList.size());

                            if (prodObject.getInt("Column1") > prodList.size()) {
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


    private boolean compareDates(String givenDate) {
        int year;
        int month;
        int day;
        int hour;
        int minute;

        String[] array = givenDate.split("T");
        String date = array[0];
        String time = array[1];
        SimpleDateFormat dateTimeFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();

        try {

            cal.setTime(dateTimeFor.parse(date + " " + time));
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            hour = cal.get(Calendar.HOUR);
            minute = cal.get(Calendar.MINUTE);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar now = Calendar.getInstance();

        System.out.println("date : " + cal.getTime() + " mils " + cal.getTimeInMillis() + " today : " + now.getTime() + " mils : " + now.getTimeInMillis());
        long mil = cal.getTimeInMillis() - now.getTimeInMillis();
        if (mil > 0) return true;
        return false;
    }


    void loadNavBar() {

        String[] SortOption = {"Sort in price", "Filter With", "Hello", "Yoyo"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        //set adapter for drawer listview

        //mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,SortOption));

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SortOption);

        mDrawerList.setAdapter(mAdapter);


    }


}
